#include <stdlib.h>
#include "msg.h"

/* Number of Players specified */
extern int numPlayers = 0;

GAME_STATE getGameState(GameMsg g)
{
    return g.gameState_;
}

void setGameState(GameMsg g, GAME_STATE gs)
{
    g.gameState_ = gs;
}

byte getID(GameMsg g)
{
    return g.id_;
}

void setID(GameMsg g, int id)
{
    g.id_ = id;
}

// Used to decline more people from joining
byte isGameStart(GameMsg g)
{
    return (g.gameStart_ == GAME_STARTED);
}

// Begin the game!
void setGameStart(GameMsg g)
{
    g.gameStart_ = GAME_STARTED;
}

// Determines if someone has requested a game
byte isGameRequested(GameMsg g)
{
    return (g.gameRequest_ == GAME_REQUESTED);
}

// Has a player joined the game? 
byte getPlayerJoin(GameMsg g, int player)
{
    // Do some bitmasking to determine if the specified
    // player has joined.

    // Player is either 0, 1, 2, 3
    if( player > 3 || player < 1 ) return;

    return (g.lobbyState_ >> player) & 0x01;
}

// Has a player said they're ready?
byte getPlayerReady(GameMsg g, int player)
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
byte areAllPlayersReady(GameMsg g)
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

byte getAffectedPlayers(PowerUpMsg p)
{
    return p.players_;
}


int getPositionX(MoveMsg m)
{
    return m.x_;
}

int getPositionY(MoveMsg m)
{
    return m.y_;
}

byte getCapturedFlags(MoveMsg m)
{
    return m.flagsCaptured_;
}

void setPositionX(MoveMsg m,  int x)
{
    m.x_ = x;
}

void setPositionY(MoveMsg m,  int y)
{
    m.y_ = y;
}

void setCapturedFlags(MoveMsg m, byte flagsCaptured)
{
    m.flagsCaptured_ = flagsCaptured;
}

void setCapturedFlag(MoveMsg m, int flagID)
{
    // Set the bit for the specified flag
    if( flagID > 3 || flagID < 1 ) return;

    m.flagsCaptured_ |= 1 << flagID;
}


int determineMsgType()
{
}

int makeLoadMsg()
{
}

int makeGameMsg()
{
}

int makeMoveMsg()
{
}

int makePowerUpMsg()
{
}

void readMsg()
{
}

void makeMsg()
{
}

void sendMsg()
{
}

