/*
 * player.h
 *
 *  Created on: 2013-11-11
 *      Author: Jeremy
 */

#ifndef PLAYER_H_
#define PLAYER_H_

#include "display.h"

#define MAX_PLAYERS 		4
#define VIEWPORT_WIDTH 		240
#define VIEWPORT_HEIGHT		128

typedef enum { NOT_CONNECTED, JOINED, READY, PLAYING, DEAD } playerState;

typedef struct
{
	playerState state;
	short x, y;
	unsigned char deviceId;
	unsigned char character;
	colour playerCol;
} sPlayer;

unsigned char doesPlayerExist(int player);
short getPlayerX(int player);
short getPlayerY(int player);

void setPlayerPosition(int player, short x, short y);

short getViewportX(int player);
short getViewportY(int player);

unsigned char addPlayer(int deviceId);
void setPlayerReady(int player);
int findPlayerByDevice(unsigned char deviceId);
void updatePlayerChar(int player, unsigned char charType);
void getPlayerColour(int player, colour* col);

#endif /* PLAYER_H_ */
