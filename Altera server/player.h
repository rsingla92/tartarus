/*
 * player.h
 *
 *  Created on: 2013-11-11
 *      Author: Jeremy
 */

#ifndef PLAYER_H_
#define PLAYER_H_

#include "altera_up_avalon_rs232.h"
#include "msg.h"
#include "Map.h"
#include "display.h"

#define MAX_PLAYERS 		4
#define VIEWPORT_WIDTH 		240
#define VIEWPORT_HEIGHT		128

#define NUM_GEMS_PER_PLAYER_PER_QUAD	1

typedef enum { NOT_CONNECTED, JOINED, PLAYER_READY, PLAYING, DEAD } playerState;

typedef struct
{
	playerState state;
	unsigned short x, y;
	unsigned short storedX, storedY;
	unsigned char deviceId;
	unsigned char character;
	unsigned char chosen;
	colour playerCol;
	Point gemList[NUM_GEMS_PER_PLAYER_PER_QUAD*4];
} sPlayer;

unsigned char doesPlayerExist(int player);
short getPlayerX(int player);
short getPlayerY(int player);

void setPlayerPosition(int player, short x, short y);

short getViewportX(int player);
short getViewportY(int player);
short getStoredY(int player);
short getStoredX(int player);
void setStoredY(int player, int y);
void setStoredX(int player, int x);

unsigned char addPlayer(int deviceId);
void removePlayer(int deviceId);
int setPlayerReady(int player);
int findPlayerByDevice(unsigned char deviceId);
unsigned char getPlayerDevice(int player, int* error);
int updatePlayerChar(int player, unsigned char charType);
void getPlayerColour(int player, colour* col);
void sendBroadcastExclusive(alt_up_rs232_dev* uart, GenericMsg* msg, int excluded_player);
void sendBroadcast(alt_up_rs232_dev* uart, GenericMsg* msg);

#endif /* PLAYER_H_ */
