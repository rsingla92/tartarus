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

unsigned char numPlayers = 0;

sPlayer playerDevTable[MAX_PLAYERS] =
{
		{NOT_CONNECTED, 0, 0, 0, 0, 0, 0, 0, {0xff, 0, 0}, {-1, -1, -1, -1}},
		{NOT_CONNECTED, 0, 0, 0, 0, 0, 0, 0, {0, 0xff, 0}, {-1, -1, -1, -1}},
		{NOT_CONNECTED, 0, 0, 0, 0, 0, 0, 0, {0, 0, 0xff}, {-1, -1, -1, -1}},
		{NOT_CONNECTED, 0, 0, 0, 0, 0, 0, 0, {0xe3, 0xff, 0x42}, {-1, -1, -1, -1}}
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
	if (numPlayers >= MAX_PLAYERS || numPlayers < 0) return -1;

	unsigned char playerId;

	if (playerDevTable[numPlayers].state == NOT_CONNECTED)
	{
		playerId = numPlayers;
	}
	else
	{
		int i;
		// Loop until we reach an empty slot.
		for (i = 0; i < MAX_PLAYERS &&
			playerDevTable[i].state != NOT_CONNECTED; i++);

		if (i >= MAX_PLAYERS || playerDevTable[i].state == NOT_CONNECTED) return -1;

		playerId = i;
	}

	playerDevTable[playerId].deviceId = deviceId;
	playerDevTable[playerId].state = JOINED;
	generateGems(playerId);

	numPlayers++;
	return playerId;
}

void removePlayer(int deviceId)
{
	int playerId = findPlayerByDevice(deviceId);

	if (playerId != -1)
	{
		printf("Removed player with device ID %d\n", deviceId);
		playerDevTable[playerId].state = NOT_CONNECTED;
		playerDevTable[playerId].chosen = 0;
		playerDevTable[playerId].deviceId = 0;
		numPlayers--;
	}
}

unsigned char playersReady(void)
{
	int i;
	for (i = 0; i < numPlayers; i++)
	{
		if (playerDevTable[i].state != PLAYER_READY) return 0;
	}

	return 1;
}

int setPlayerReady(int player)
{
	if (player < 0 || player >= MAX_PLAYERS || !doesPlayerExist(player)) return 0;

	playerDevTable[player].state = PLAYER_READY;
	return 1;
}

int updatePlayerChar(int player, unsigned char charType)
{
	if (player < 0 || player >= MAX_PLAYERS || !doesPlayerExist(player)) {
		printf("Player not valid!\n");
		return 0;
	}

	int i;
	for (i = 0; i < MAX_PLAYERS; i++)
	{
	   // Cannot choose if character is already taken.
	   if (playerDevTable[i].state != NOT_CONNECTED &&
			   playerDevTable[i].chosen &&
			   playerDevTable[i].character == charType)
	   {
		   printf("Player %d already chose character %d\n", i, playerDevTable[i].character);
		   return 0;
	   }
	}

	playerDevTable[player].character = charType;
	playerDevTable[player].chosen = 1;

	return 1;
}

int findPlayerByDevice(unsigned char deviceId)
{
	int i;
	for (i = 0; i < numPlayers; i++)
	{
		if (playerDevTable[i].deviceId == deviceId &&
				playerDevTable[i].state != NOT_CONNECTED) return i;
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

unsigned char getPlayerDevice(int player, int* error)
{
	if (player < 0 || player >= MAX_PLAYERS || !doesPlayerExist(player))
	{
		printf("Error: Player not valid.\n");
		*error = -1;
		return 0;
	}

	*error = 0;
	return playerDevTable[player].deviceId;
}

void setStoredX(int player, int x)
{
	playerDevTable[player].storedX = x;
}

void setStoredY(int player, int y)
{
	playerDevTable[player].storedY = y;
}

short getStoredX(int player)
{
	return playerDevTable[player].storedX;
}

short getStoredY(int player)
{
	return playerDevTable[player].storedY;
}

void generateGems(unsigned char player)
{
	int i;
	// Generate the gems for each quadrant
	printf("Generating Gems: ");
	for (i = 0; i < NUM_GEMS_PER_PLAYER_PER_QUAD*4; i++)
	{
		int quad = i % 4 + 1;
		playerDevTable[player].gemList[i] = getRandomPoint(quad);
		printf("Gem %d: (%d, %d), ", i, playerDevTable[player].gemList[i].x, playerDevTable[player].gemList[i].y);
	}
}

void sendBroadcast(alt_up_rs232_dev* uart, GenericMsg* msg)
{
	sendBroadcastExclusive(uart, msg, -1);
}

void sendBroadcastExclusive(alt_up_rs232_dev* uart, GenericMsg* msg, int excluded_player)
{
	int i;

	for (i = 0; i < numPlayers; i++)
	{
		if(i != excluded_player && playerDevTable[i].state != NOT_CONNECTED)
		{
			// Send the message
			msg->clientID_ = playerDevTable[i].deviceId;
			writeMsg(uart, msg);
		}
	}
}
