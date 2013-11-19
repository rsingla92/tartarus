#include <stdlib.h>
#include "msg.h"
#include "Map.h"
#include "display.h"

extern Map map;

/* Number of Players specified */
extern int numPlayers = 0;
extern GenericMsg* msgHead;

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
		// Draw the new pixel for the player.
		colour col;
		getPlayerColour(player, &col);
		drawPlayerAt(xPos, yPos, col);
		// Swap buffers to see the pixel
		swap_buffers();
	}
}

int parseJoinMsg(GenericMsg* msg)
{
	// JOIN message from a player attempting to join the game.
	// The data is just four-bytes: The integer for the device ID.
	printf("Join Message\n");
	unsigned char playerID = addPlayer(msg->clientID_);
	if (playerID == -1)
	{
		// Player not added. Send a message back
		// indicating failure.
	}
	else
	{
		// Player successfully added. Send a message
		// back with the player ID.
	}
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
		setPlayerReady(playerNo);
	}

	// Send a broadcast message indicating that this player is now ready.

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
	}
}

int parseTestMsg(GenericMsg* msg)
{
	printf("Received test message from %d! Length: %d\n", msg->clientID_, msg->msgLength_);
	printf("Message: %s\n", msg->msg_);
}

void readMsg(GenericMsg* msg)
{
}

void makeMsg(GenericMsg* msg)
{
}

void sendMsg(GenericMsg* msg)
{
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

