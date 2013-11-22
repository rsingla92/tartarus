#include <stdio.h>
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

#define NUM_FILES 0

#define leds (volatile char *) LEDS_BASE

/*
static char* file_list[NUM_FILES] = { "4B.BMP", "B1.BMP", "B2.BMP", "B3.BMP",
		"B4.BMP", "B5.BMP", "DK1.BMP", "DK2.BMP", "DK3.BMP", "DK4.BMP",
		"DK5.BMP", "DK6.BMP", "DK7.BMP", "DK8.BMP", "DK9.BMP", "DK10.BMP",
		"DK11.BMP", "FIRE.BMP", "FIRE1.BMP", "FIRE2.BMP", "FIRE3.BMP",
		"HMR.BMP", "M1.BMP", "M2.BMP", "M3.BMP", "M4.BMP", "M5.BMP", "M6.BMP",
		"M7.BMP", "M8.BMP", "M9.BMP", "M10.BMP", "M11.BMP", "M12.BMP",
		"M13.BMP", "M14.BMP", "M15.BMP", "P1.BMP", "P2.BMP", "PP1.BMP",
		"PP2.BMP", "PP3.BMP", "PURSE.BMP", "UMBRLA.BMP", "MM1.BMP", "MM2.BMP"};
*/
static BitmapHandle* bmp;
static alt_u32 ticks_per_sec;
static alt_u32 num_ticks;
static alt_32 update(void *context);

int* menuSoundBuf;
int menuSoundBufLen;

GenericMsg* msgHead = NULL;
GenericMsg* msgTail = NULL;

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

	if (getSerialUsedSpace(uart) == 0))
	{
		//If nothing to read, then just return
		return;
	}

	// Make element to add to queue
	GenericMsg* newElement = (GenericMsg*) malloc(sizeof(GenericMsg));

	// first byte
	readSerialData(uart, &(newElement->clientID_), &parity);

	// second byte. adjust because we actually read the first byte.
	readSerialData(uart, &(newElement->msgLength_), &parity);
	newElement->msgLength_--;

	// third byte
	readSerialData(uart, &(newElement->msgID_), &parity);

	// store the data
	newElement->msg_ = (byte*) malloc(sizeof(newElement->msgLength_));

	// allocate the rest of it.
	int i;
	for( i = 0; i < newElement->msgLength_; ++i)
	{
		// read data
		readSerialData(uart, &(newElement->msg_[i]), &parity);
	}

	// if it's first element then queue is not set up
	if( !msgHead )
	{
		// initialize the queue
		msgHead = newElement;
		msgTail = msgHead;
		return;
	}

	// make current tail point to cur element if tail exists
	if( msgTail)
	{
		msgTail->next = newElement;
	}

	// new tail
	msgTail = newElement;
	newElement->next = NULL;

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
		case LOAD:
			makeLoadMsg(msgHead);
			break;
		case GAME:
			makeGameMsg(msgHead);
			break;
		case MOVE:
			makeMoveMsg(msgHead);
			break;
		case POWER_UP:
			makePowerUpMsg(msgHead);
			break;
		default:
			printf("Unknown message type: %d\n", msgHead->msgID_);
			break;
		}

		// clean up the msg struct
		tmp = msgHead->next;
		free(msgHead->msg_);
		free(msgHead);
		msgHead = tmp;

	} while ( msgHead );

	// queue's parse it all mang
	// lator gator
	return;
}

int main(void) {
	// Start the timestamp -- will be used for seeding the random number generator.

	//Init RS232
	alt_up_rs232_dev* uart = initSerialPort("/dev/rs232_0");

	alt_timestamp_start();
	sdcard_handle *sd_dev = init_sdcard();
	initAudio();

	menuSoundBufLen = loadSound("Tit2.wav", &menuSoundBuf, 0.5);
	swapInSound(menuSoundBuf, menuSoundBufLen, 1);

	// Set latch and clock to 0.
	init_display();

	clear_display();

	if (sd_dev == NULL)
		return 1;

	printf("Card connected.\n");

	ticks_per_sec = alt_ticks_per_second();

	seed(alt_timestamp());

	alt_u32 tickCount = alt_nticks();
	num_ticks = ticks_per_sec / 30;

	while (true)
	{
		if (alt_nticks() - tickCount >= num_ticks)
		{
			tickCount = alt_nticks();
			update(0);
		}
	}

	return 0;
}

alt_32 update(void *context) {
	//int i;
	//for (i = 0; i < 4; i++) prev_state[i] = button_states[i];

	//readDat();
	readSocket(uart);
	parseNextMessage();
	runState();
	return 1;

}
