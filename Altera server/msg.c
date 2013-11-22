#include <stdlib.h>
#include "msg.h"
#include "Map.h"
#include "display.h"
#include "player.h"

extern Map map;
extern alt_up_rs232_dev* uart_dev;
extern unsigned char numPlayers;
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

	sendJoinResponse(playerID, response);
}

int parseReadyMsg(GenericMsg* msg)
{
	// No data with this message.
	printf("Ready message!");
	int playerNo = findPlayerByDevice(msg->clientID_);
	static unsigned char readyPlayers = 0;

	if (playerNo == -1)
	{
		printf("Error: No player found with device %d\n", msg->clientID_);
	}
	else
	{
		readyPlayers += setPlayerReady(playerNo);
	}

	// Send a broadcast message indicating that this player is now ready.

	if(readyPlayers == numPlayers) {
		printf("\nReady: %d\n", readyPlayers);
		sendStartResponse(playerNo);
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
		updatePlayerChar(player, msg->msg_[0]);
		sendCharacterChosenMsg(player, msg->msg_[0]);
	}
}

int parseTestMsg(GenericMsg* msg)
{
	printf("Received test message from %d! Length: %d\n", msg->clientID_, msg->msgLength_);
	printf("Message: %s\n", msg->msg_);
}

void writeMsg(alt_up_rs232_dev* uart, GenericMsg* msg)
{
	byte clientID = msg->clientID_;
	byte msgLength = msg->msgLength_;
	byte msgID = msg->msgID_;
	int i = 0;

	// write first byte (client #)
	writeSerialData(uart, clientID);
	printf("Writing data to %x!\n", clientID);

	// write size (the size of the message)
	writeSerialData(uart, msgLength);
	printf("Length of Message: %d\n", msgLength);

	// Send the rest of the data
	for (i = 0; i < msgLength; i++) {
		writeSerialData(uart, msg->msg_[i]);
	}
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
	charChosenMsg.msg_[0] = (unsigned char) player_id;
	charChosenMsg.msg_[1] = charID;
	sendBroadcastExclusive(uart_dev, &charChosenMsg, player_id);
	free(charChosenMsg.msg_);
}

// A response of 0 indicates that the player cannot join (too many players).
// A response of 1 indicates that the player can.
void sendJoinResponse(int player_id, unsigned char response)
{
	// The player with ID player_id has
	// selected the character with ID charID. Send a message
	// to all devices indicating the player can no longer be chosen.
	GenericMsg joinRespMsg;
	int err_code = 0;
	joinRespMsg.clientID_ = getPlayerDevice(player_id, &err_code); //Doesn't matter, since this is broadcast.

	if (err_code == -1) return;

	joinRespMsg.msgID_ = JOIN_RESPONSE;
	joinRespMsg.msgLength_ = 1;
	joinRespMsg.msg_ = (unsigned char*) malloc(joinRespMsg.msgLength_);
	joinRespMsg.msg_[0] = response;
	writeMsg(uart_dev, &joinRespMsg);
	free(joinRespMsg.msg_);
}

//Ready response
void sendStartResponse(int player_id)
{
	// The player with ID player_id has
	// selected the character with ID charID. Send a message
	// to all devices indicating the player can no longer be chosen.
	GenericMsg startMsg;
	int err_code = 0;
	startMsg.clientID_ = getPlayerDevice(player_id, &err_code); //Doesn't matter, since this is broadcast.

	if (err_code == -1) return;

	startMsg.msgID_ = START;
	startMsg.msgLength_ = 1;
	startMsg.msg_ = (unsigned char*) malloc(startMsg.msgLength_);
	startMsg.msg_[0] = 1;
	writeMsg(uart_dev, &startMsg);
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

