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
#include "altera_up_avalon_rs232.h"
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
static alt_up_rs232_dev* uart_dev;

extern Map map;

/* Reads from the TCP/IP socket. Takes in the client ID, message length, and message type
 * and puts them into a generic MSG struct.
 * Puts this generic struct into a messageQueue;
 */
static void readSocket(alt_up_rs232_dev* uart)
{
	byte clientID = 0;
	byte msgLength = 0;
	byte msgID = 0;
	byte parity;
	byte packetLength = 0;

	if (getSerialUsedSpace(uart) == 0)
	{
		//If nothing to read, then just return
		return;
	}

	// Make element to add to queue
	GenericMsg* newElement;

	// first byte
	readSerialData(uart, &clientID, &parity);
	printf("Got data from %x!\n", clientID);

	// Read size
	readSerialDataWait(uart, &packetLength, &parity);
	printf("Length of Packet: %d\n", packetLength);

	int i;
	for( i = 0; i < packetLength; i += msgLength + 3)
	{
		newElement = (GenericMsg*) malloc(sizeof(GenericMsg));
		newElement->clientID_ = clientID;
		newElement->next = NULL;

		// HACK: Trying to get to the bottom of this...
		byte throwaway;
		readSerialDataWait(uart, &throwaway, &parity);

		//Message length...
		readSerialDataWait(uart, &msgLength, &parity);
		msgLength--; // We read in the type
		newElement->msgLength_ = msgLength;
		printf("Length: %d, ", newElement->msgLength_);

		// Read in the message ID:
		readSerialDataWait(uart, &msgID, &parity);
		newElement->msgID_ = msgID;
		printf("Type of message: %x, ", newElement->msgID_);

		newElement->msg_ = (byte*) malloc(sizeof(newElement->msgLength_));

		int j;
		printf("Data: ");
		for (j = 0; j < msgLength; j++) {
			readSerialDataWait(uart, &(newElement->msg_[j]), &parity);
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
	}

	return;
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
	sdcard_handle *sd_dev = init_sdcard();
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

	printf("Swapping buffers!\n");
	swap_buffers();
	drawMapPortion(0, 0, map.mapWidth, map.mapHeight);

	printf("Drawn!\n");
	seed(alt_timestamp());

	while (true)
	{
		readSocket(uart_dev);
		parseNextMessage();
		runState();
	}

	return 0;
}
