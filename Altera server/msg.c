#include <stdlib.h>
#include <stdio.h>
#include "msg.h"
#include "display.h"
#include "player.h"

extern Map map;
extern FILE* uart_dev;
extern unsigned char numPlayers;
extern sPlayer playerDevTable[MAX_PLAYERS];

static unsigned char gemsSent = 0;
static unsigned char readyPlayers = 0;

/*
GAME_STATE getGameState(GameMsg g)
{
    return g.gameState_;
}

void setGameState(GameMsg g, GAME_STATE gs)
{
    g.gameState_ = gs;
}

unsigned char getID(GameMsg g)
{
    return g.id_;
}

void setID(GameMsg g, int id)
{
    g.id_ = id;
}

// Used to decline more people from joining
unsigned char isGameStart(GameMsg g)
{
    return (g.gameStart_ == GAME_STARTED);
}

// Begin the game!
void setGameStart(GameMsg g)
{
    g.gameStart_ = GAME_STARTED;
}

// Determines if someone has requested a game
unsigned char isGameRequested(GameMsg g)
{
    return (g.gameRequest_ == GAME_REQUESTED);
}

// Has a player joined the game?
unsigned char getPlayerJoin(GameMsg g, int player)
{
    // Do some bitmasking to determine if the specified
    // player has joined.

    // Player is either 0, 1, 2, 3
    if( player > 3 || player < 1 ) return;

    return (g.lobbyState_ >> player) & 0x01;
}

// Has a player said they're ready?
unsigned char getPlayerReady(GameMsg g, int player)
{
    // Do smome bitmasking to determined if the specified player
    // has signalled ready.

    // Player is either 0, 1, 2, 3
    if( player > 3 || player < 0 ) return;

    return (g.lobbyState_ >> (player+4)) & 0x01;
}

// Tell others a player joiend
void setPlayerJoin(GameMsg g, int player)
{
    // Set the specified bit to indicate a player has joined
    switch(player)
    {
    case FIRST_PLAYER:
        g.lobbyState_ |= 1 << 1;
        break;
    case SECOND_PLAYER:
        g.lobbyState_ |= 1 << 2;
        break;
    case THIRD_PLAYER:
        g.lobbyState_ |= 1 << 3;
        break;
    case FOURTH_PLAYER:
        g.lobbyState_ |= 1 << 4;
        break;
    default:
        break;
    }
}

// Indicate that a player is ready
void setPlayerReady(GameMsg g, int player)
{
    // Set the specified bit to indicate a player is ready
    switch(player)
    {
    case FIRST_PLAYER:
        g.lobbyState_ |= 1 << 5;
        break;
    case SECOND_PLAYER:
        g.lobbyState_ |= 1 << 6;
        break;
    case THIRD_PLAYER:
        g.lobbyState_ |= 1 << 7;
        break;
    case FOURTH_PLAYER:
        g.lobbyState_ |= 1 << 8;
        break;
    default:
        break;
    }
}

// Can we begin the game?!
unsigned char areAllPlayersReady(GameMsg g)
{
    if( numPlayers > 3 || numPlayers < 1 ) return 0;

    int checkVal = 0;
    switch(numPlayers)
    {
    case 1:
        checkVal = ONE_READY;
        break;
    case 2:
        checkVal = TWO_READY;
        break;
    case 3:
        checkVal = THREE_READY;
        break;
    case 4:
        checkVal = FOUR_READY;
        break;
    default:
        break;
    }

    return (g.lobbyState_ == checkVal);
}

// Determine power up type
POWER_UP_TYPE getPowerUpType(PowerUpMsg p)
{
    return p.type_;
}

void setPowerUpType(PowerUpMsg p, POWER_UP_TYPE type)
{
    p.type_ = type;
}

unsigned char getAffectedPlayers(PowerUpMsg p)
{
    return p.players_;
}

void setCapturedFlags(MoveMsg m, unsigned char flagsCaptured)
{
    m.flagsCaptured_ = flagsCaptured;
}

void setCapturedFlag(MoveMsg m, int flagID)
{
    // Set the bit for the specified flag
    if( flagID > 3 || flagID < 1 ) return;

    m.flagsCaptured_ |= 1 << flagID;
}
*/

int makeTestMsg(GenericMsg* msg)
{
	printf("Received test message from %d! Length: %d\n", msg->clientID_, msg->msgLength_);
	printf("Message: %s\n", msg->msg_);
}

int parseMoveMsg(GenericMsg* msg)
{
	short xPos = getShort(msg->msg_, 0);
	short yPos = getShort(msg->msg_, sizeof(short));
	printf("Got Move Message! X: %d, Y: %d \n", xPos, yPos);
	int player = findPlayerByDevice(msg->clientID_);

	if (player == -1)
	{
		printf("Error: No player found with device %d\n", msg->clientID_);
	}
	else
	{
		// Erase old pixel for the player.
		//	erasePositionAt(getPlayerX(player), getPlayerY(player));
		setPlayerPosition(player, xPos, yPos);

		if (abs(getStoredX(player) - xPos) >= 16 || abs(getStoredY(player) - yPos) >= 16)
		{
			// Draw the new pixel for the player.
			colour col;
			getPlayerColour(player, &col);
			drawPlayerAt(xPos, yPos, col);
			// Swap buffers to see the pixel
			swap_buffers();
			drawPlayerAt(xPos, yPos, col);
			setStoredX(player, xPos);
			setStoredY(player, yPos);
		}
	}
}

int parseJoinMsg(GenericMsg* msg)
{
	// JOIN message from a player attempting to join the game.
	// The data is just four-bytes: The integer for the device ID.
	printf("Join Message\n");
	unsigned char playerID = addPlayer(msg->clientID_);
	unsigned char response = 1;

	if (playerID == -1)
	{
		// Player not added. Send a message back
		// indicating failure.
		response = 0;
	}
	else
	{
		// Player successfully added. Send a message
		// back with the player number.
		response = playerID + 1;
	}

	sendJoinResponse(playerID, response, msg->clientID_);
}

int parseDisconnectMsg(GenericMsg *msg)
{
    printf("Disconnect Message\n");
    int player = findPlayerByDevice(msg->clientID_);

    if (player != -1)
    {
    	if(playerDevTable[player].state == PLAYER_READY) readyPlayers--;
    }

    removePlayer(msg->clientID_);
}

int parseReadyMsg(GenericMsg* msg)
{
	// No data with this message.
	printf("Ready message!");
	int playerNo = findPlayerByDevice(msg->clientID_);

	if (playerNo == -1)
	{
		printf("Error: No player found with device %d\n", msg->clientID_);
	}
	else
	{
		readyPlayers += setPlayerReady(playerNo);
	}
	// Send a broadcast message indicating that this player is now ready.

	printf("Player %d is ready!\n", playerNo);

	if(readyPlayers == numPlayers) {
		printf("\nReady: %d\n", readyPlayers);
		sendGemMsg(playerNo);
	}
}

int parseSelectCharMsg(GenericMsg* msg)
{
	int player = findPlayerByDevice(msg->clientID_);

	if (player == -1)
	{
		printf("Error: No player found with device ID %d\n", msg->clientID_);
	}
	else
	{
		if (updatePlayerChar(player, msg->msg_[0]))
		{
			// Only send message if character hasn't been
			// chosen.
			sendCharacterChosenMsg(player, msg->msg_[0]);
		}
		else
		{
			printf("Player already chose character %d\n", msg->msg_[0]);
		}
	}
}

int parseGemPicked(GenericMsg *msg)
{
   int playerID = findPlayerByDevice(msg->clientID_);
   int gemType = playerID - 1;

   if (msg->msgLength_ < 4) return -1;

   unsigned short row = getShort(msg->msg_, 0);
   unsigned short col = getShort(msg->msg_, 2);

   Point newGem = respawnGem(row, col, playerID);

   // Add points to player
   playerDevTable[playerID].points += 10;

   // Send an update gem message
   Point oldGem;
   oldGem.x = col;
   oldGem.y = row;
   sendUpdateGemMsg(playerID, newGem, oldGem);
}

void sendUpdateGemMsg(int player_id, Point newGem, Point oldGem)
{
	GenericMsg updateGemMsg;
	updateGemMsg.clientID_ = 0; //Doesn't matter, since this is broadcast.
	updateGemMsg.msgID_ = UPDATE_GEM;
	updateGemMsg.msgLength_ = 9;
	updateGemMsg.msg_ = (unsigned char*) malloc(updateGemMsg.msgLength_);
	updateGemMsg.msg_[0] = (unsigned char) (player_id); // Sending the GEM type.
	updateGemMsg.msg_[1] = newGem.x >> 8;
	updateGemMsg.msg_[2] = newGem.x & 0x00FF;
	updateGemMsg.msg_[3] = newGem.y >> 8;
	updateGemMsg.msg_[4] = newGem.y & 0x00FF;
	updateGemMsg.msg_[5] = oldGem.x >> 8;
	updateGemMsg.msg_[6] = oldGem.x & 0x00FF;
	updateGemMsg.msg_[7] = oldGem.y >> 8;
	updateGemMsg.msg_[8] = oldGem.y & 0x00FF;

	printf("Printing msg (update gem msg): ");
	int i;
	for (i = 0; i < 9; i++) printf("%d, ", updateGemMsg.msg_[i]);

	sendBroadcast(uart_dev, &updateGemMsg);
	free(updateGemMsg.msg_);
}

int parseTestMsg(GenericMsg* msg)
{
	printf("Received test message from %d! Length: %d\n", msg->clientID_, msg->msgLength_);
	printf("Message: %s\n", msg->msg_);
}

void writeMsg(alt_up_rs232_dev* uart, GenericMsg* msg)
{
	byte clientID = msg->clientID_;
	unsigned short msgLength = msg->msgLength_ + 1; // An extra 1 for the ID.
	byte msgID = msg->msgID_;
	int i = 0;

	// write first byte (client #)
	writeSerialData(uart, clientID);
	printf("Writing data to %x!\n", clientID);

	// write size (the size of the message)
	byte lengthMSB = msgLength >> 8;
	byte lengthLSB = msgLength & 0x00FF;

	writeSerialData(uart, lengthMSB);
	writeSerialData(uart, lengthLSB);
	printf("Length of Message: %d\n", msgLength);

	// Send the message ID
	writeSerialData(uart, msgID);
	printf("Message ID: %d\n", msgID);

	// Send the rest of the data
	for (i = 0; i < msg->msgLength_; i++) {
		printf("i: %d, Writing: %d, ", i, msg->msg_[i]);
		writeSerialData(uart, msg->msg_[i]);
	}

    printf("\n");

	fflush(uart);
 }

void parseGemAckMsg(GenericMsg* msg)
{
	gemsSent++;

	if (gemsSent >= numPlayers)
	{
		printf("Sending start signal.\n");
		sendStartResponse();
	}
}

// Send all gem lists to the player with ID player_id.
void sendGemMsg(int player_id)
{
	int err_code = 0;
	GenericMsg gemMsg;
	gemMsg.clientID_ =  getPlayerDevice(player_id, &err_code);

	if (err_code == -1)
	{
		printf("Invalid player ID.\n");
		return;
	}

	gemMsg.msgID_ = GEM_MSG;
	// The extra 2 is for the header: number of gems and player ID.
	gemMsg.msgLength_ = numPlayers*NUM_GEMS_PER_PLAYER_PER_QUAD*4*sizeof(Point)
		+ 2*numPlayers;

	printf("Gem Message Length: %d, NumPlayers: %d\n", gemMsg.msgLength_, numPlayers);

	gemMsg.msg_ = (unsigned char*) malloc(gemMsg.msgLength_);
	int i, msgCounter;
	msgCounter = 0;

	for (i = 0; i < numPlayers; i++)
	{
		gemMsg.msg_[msgCounter++] = i+1;
		gemMsg.msg_[msgCounter++] = NUM_GEMS_PER_PLAYER_PER_QUAD*4;

		int j;
		for (j = 0; j < NUM_GEMS_PER_PLAYER_PER_QUAD*4; j++)
		{
			Point gem = playerDevTable[i].gemList[j];
			gemMsg.msg_[msgCounter++] = (gem.x >> 8);
			gemMsg.msg_[msgCounter++] = (gem.x & 0x00ff);
			gemMsg.msg_[msgCounter++] = (gem.y >> 8);
			gemMsg.msg_[msgCounter++] = (gem.y & 0x00ff);
		}
	}

	sendBroadcast(uart_dev, &gemMsg);
	free(gemMsg.msg_);
}

void sendCharacterChosenMsg(int player_id, unsigned char charID)
{
	// The player with ID player_id has
	// selected the character with ID charID. Send a message
	// to all devices indicating the player can no longer be chosen.
	GenericMsg charChosenMsg;
	charChosenMsg.clientID_ = 0; //Doesn't matter, since this is broadcast.
	charChosenMsg.msgID_ = CHAR_CHOSEN_MSG;
	charChosenMsg.msgLength_ = 2;
	charChosenMsg.msg_ = (unsigned char*) malloc(charChosenMsg.msgLength_);
	charChosenMsg.msg_[0] = (unsigned char) (player_id + 1);
	charChosenMsg.msg_[1] = charID;
	//sendBroadcastExclusive(uart_dev, &charChosenMsg, player_id);
    // For testing purposes with one device:
	sendBroadcast(uart_dev, &charChosenMsg);
	free(charChosenMsg.msg_);
}

// A response of 0 indicates that the player cannot join (too many players).
// A response of 1 indicates that the player can.
void sendJoinResponse(int player_id, unsigned char response, unsigned char devID)
{
	// The player with ID player_id has
	// selected the character with ID charID. Send a message
	// to all devices indicating the player can no longer be chosen.
	GenericMsg joinRespMsg;
	joinRespMsg.clientID_ = devID;

	joinRespMsg.msgID_ = JOIN_RESPONSE;
	joinRespMsg.msgLength_ = 1;
	joinRespMsg.msg_ = (unsigned char*) malloc(joinRespMsg.msgLength_);
	joinRespMsg.msg_[0] = response;
	writeMsg(uart_dev, &joinRespMsg);
	free(joinRespMsg.msg_);
}

//Ready response
void sendStartResponse(void)
{
	// The player with ID player_id has
	// selected the character with ID charID. Send a message
	// to all devices indicating the player can no longer be chosen.
	GenericMsg startMsg;
	int err_code = 0;
	startMsg.clientID_ = 0; //Doesn't matter, since this is broadcast.

	if (err_code == -1) return;

	startMsg.msgID_ = START;
	startMsg.msgLength_ = 1;
	startMsg.msg_ = (unsigned char*) malloc(startMsg.msgLength_);
	startMsg.msg_[0] = 1;
	sendBroadcast(uart_dev, &startMsg);
	free(startMsg.msg_);
}

unsigned int getInt(unsigned char* buf, int offset)
{
	unsigned int val = 0;
	val = (buf[offset] << 8*3) | (buf[offset + 1] << 8*2)
			| (buf[offset + 2] << 8*1) | (buf[offset + 3]);
	return val;
}

unsigned short getShort(unsigned char* buf, int offset)
{
	unsigned short val = 0;
	val = (buf[offset] << 8) | (buf[offset + 1]);
	return val;
}

