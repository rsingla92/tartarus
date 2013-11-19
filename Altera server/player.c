/*
 * player.c
 *
 *  Created on: 2013-11-11
 *      Author: Jeremy
 */

#include "player.h"
#include "Map.h"

/* From map. */
extern Map map;

static unsigned char numPlayers = 0;

static sPlayer playerDevTable[MAX_PLAYERS] =
{
		{NOT_CONNECTED, 0, 0, 0, 0, {0xff, 0, 0}},
		{NOT_CONNECTED, 0, 0, 0, 0, {0, 0xff, 0}},
		{NOT_CONNECTED, 0, 0, 0, 0, {0, 0, 0xff}},
		{NOT_CONNECTED, 0, 0, 0, 0, {0x8f, 0, 0xff}}
};

unsigned char doesPlayerExist(int player)
{
	if (player < 0 || player >= MAX_PLAYERS) return 0;

	return (playerDevTable[player].state != NOT_CONNECTED);
}

short getPlayerX(int player)
{
	if (player < 0 || player >= MAX_PLAYERS) return -1;

	return playerDevTable[player].x;
}

short getPlayerY(int player)
{
	if (player < 0 || player >= MAX_PLAYERS) return -1;

	return playerDevTable[player].y;
}

void getPlayerColour(int player, colour* col)
{
	if (player < 0 || player >= MAX_PLAYERS || !doesPlayerExist(player)) return -1;

	(*col).r = playerDevTable[player].playerCol.r;
	(*col).g = playerDevTable[player].playerCol.g;
	(*col).b = playerDevTable[player].playerCol.b;
}

short getViewportX(int player)
{
	if (player < 0 || player >= MAX_PLAYERS || !doesPlayerExist(player)) return -1;

	short playerX = playerDevTable[player].x;

	if (playerX <= (VIEWPORT_WIDTH / 2))
	{
		return 0;
	}
	else if (playerX >= map.mapWidth*TILE_WIDTH - (VIEWPORT_WIDTH / 2))
	{
		return (short)(map.mapWidth*TILE_WIDTH - VIEWPORT_WIDTH);
	}
	else
	{
		return (short)(playerX - (VIEWPORT_WIDTH / 2));
	}
}

short getViewportY(int player)
{
	if (player < 0 || player >= MAX_PLAYERS ||!doesPlayerExist(player)) return -1;

	short playerY = playerDevTable[player].y;

	if (playerY <= (VIEWPORT_HEIGHT / 2))
	{
		return 0;
	}
	else if (playerY >= map.mapHeight*TILE_HEIGHT - (VIEWPORT_HEIGHT / 2))
	{
		return (short)(map.mapWidth*TILE_HEIGHT - VIEWPORT_HEIGHT);
	}
	else
	{
		return (short)(playerY - (VIEWPORT_HEIGHT / 2));
	}
}

unsigned char addPlayer(int deviceId)
{
	if (numPlayers >= MAX_PLAYERS) return -1;

	unsigned char playerId = numPlayers++;

	playerDevTable[playerId].deviceId = deviceId;
	playerDevTable[playerId].state = JOINED;

	return playerId;
}

unsigned char playersReady(void)
{
	int i;
	for (i = 0; i < numPlayers; i++)
	{
		if (playerDevTable[i].state != READY) return 0;
	}

	return 1;
}

void setPlayerReady(int player)
{
	if (player < 0 || player >= MAX_PLAYERS || !doesPlayerExist(player)) return;

	playerDevTable[player].state = READY;
}

void updatePlayerChar(int player, unsigned char charType)
{
	if (player < 0 || player >= MAX_PLAYERS || !doesPlayerExist(player)) return;

	playerDevTable[player].character = charType;
}

int findPlayerByDevice(unsigned char deviceId)
{
	int i;
	for (i = 0; i < numPlayers; i++)
	{
		if (playerDevTable[i].deviceId == deviceId) return i;
	}

	return -1;
}

void setPlayerPosition(int player, short x, short y)
{
	if (player < 0 || player >= MAX_PLAYERS || !doesPlayerExist(player))
	{
		printf("Error: Player not valid.\n");
		return;
	}

	playerDevTable[player].x = x;
	playerDevTable[player].y = y;
}
