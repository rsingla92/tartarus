#include <stdio.h>
#include <stdlib.h>
#include "sdcard.h"
#include "bitmap.h"
#include "display.h"
#include "background.h"
#include "priv/alt_busy_sleep.h"
#include "sys/alt_alarm.h"
#include "io.h"
#include "sys/alt_timestamp.h"
#include "state_machine.h"
#include "audio.h"
#include "msg.h"
#include "random.h"
#include "serialport.h"
#include "Map.h"

#define NUM_FILES 0

#define leds (volatile char *) LEDS_BASE

int* menuSoundBuf;
int menuSoundBufLen;

GenericMsg* msgHead = NULL;
GenericMsg* msgTail = NULL;
FILE* uart_dev;
static int writeQueueCounter = 0;
GenericMsg* writeMsgHead = NULL;
GenericMsg* writeMsgTail = NULL;

extern Map map;

/* Reads from the TCP/IP socket. Takes in the client ID, message length, and message type
 * and puts them into a generic MSG struct.
 * Puts this generic struct into a messageQueue;
 */
static int readSocket(FILE* uart)
{
	byte clientID = 0;
	byte msgLength = 0;
	byte msgID = 0;
	byte lengthMSB = 0;
	byte lengthLSB = 0;
	unsigned short packetLength = 0;

	// Make element to add to queue
	GenericMsg* newElement;

	sendRequestData(uart);

	// first byte
	readSerialData(uart, &clientID);
	printf("Dat: %d\n", clientID);

	// If nothing to read, return.
	if (clientID == EOF) {
		printf("Got an EOF!!\n");
		return 0;
	}

	// Read size
	readSerialData(uart, &lengthMSB);
	readSerialData(uart, &lengthLSB);

	packetLength = (lengthMSB << 8) | lengthLSB;
	printf("Length of Packet: %d\n", packetLength);

	newElement = (GenericMsg*) malloc(sizeof(GenericMsg));
	newElement->clientID_ = clientID;
	newElement->next = NULL;

	newElement->msgLength_ = packetLength--;
	printf("Length: %d, ", newElement->msgLength_);

	// Read in the message ID:
	readSerialData(uart, &msgID);
	newElement->msgID_ = msgID;
	printf("Type of message: %x, ", newElement->msgID_);

	newElement->msg_ = (byte*) malloc(sizeof(newElement->msgLength_));

	int j;
	printf("Data: ");
	for (j = 0; j < packetLength; j++) {
		readSerialData(uart, &(newElement->msg_[j]));
		printf("%x, ", newElement->msg_[j]);
	}

	// If it is the first element then the queue is not set up
	if( !msgHead )
	{
		// Initialize the queue
		msgHead = newElement;
		msgTail = msgHead;
	}
	else
	{
		// Make current tail point to cur element if tail exists
		if( msgTail)
		{
			msgTail->next = newElement;
		}

		msgTail = newElement;
	}

	return 1;
}

 static void resetQueue()
 {
	//Destroy all remaining GenericMsg?
	writeQueueCounter = 0;
 }

/* Takes the next element of the messageQueue and
 * creates the appropriate structure for it.
 * Uses msgHead
 */
static void parseNextMessage()
{
	// if queue is empty
	if(!msgHead) return;

	// do while the queue is not empty
	// there's something in the queue

	GenericMsg* tmp = NULL;
	do {
		switch(msgHead->msgID_)
		{
		case JOIN:
			parseJoinMsg(msgHead);
			break;
		case READY:
			parseReadyMsg(msgHead);
			break;
		case MOVE:
			parseMoveMsg(msgHead);
			break;
		case SELECT_CHAR:
			parseSelectCharMsg(msgHead);
			break;
		case DISCONNECT:
			parseDisconnectMsg(msgHead);
			break;
		case GEM_ACK:
			parseGemAckMsg(msgHead);
			break;
		case GEM_PICKED:
			parseGemPicked(msgHead);
			break;
		case TEST:
			parseTestMsg(msgHead);
			break;
		default:
			printf("Unknown message type: %x\n", msgHead->msgID_);
			break;
		}

		// clean up the msg struct
		tmp = msgHead->next;
		free(msgHead->msg_);
		free(msgHead);
		msgHead = tmp;

	} while ( msgHead );

	return;
}

int main(void) {
	//Init RS232
	uart_dev = initSerialPort("/dev/rs232_0");

	alt_timestamp_start();
	sdcard_handle *sd_dev = init_sdcard(); //TODO: REMOVE COMMENT
//	initAudio();

	printf("Initializing display...\n");
	// Set latch and clock to 0.
	init_display();

	clear_display();

	if (sd_dev == NULL)
		return 1;

	printf("Creating map!\n");

	makeMap("tmap1.txt");
    printf("Drawing map! Width: %d, Height: %d\n", map.mapWidth, map.mapHeight);
	drawMapPortion(0, 0, map.mapWidth, map.mapHeight);

	//printf("Swapping buffers!\n");
	swap_buffers();
	drawMapPortion(0, 0, map.mapWidth, map.mapHeight);

	printf("Drawn!\n");
	seed(alt_timestamp());

	while (true)
	{
		readSocket(uart_dev);
		parseNextMessage();
		//runState();
	}

	return 0;
}
