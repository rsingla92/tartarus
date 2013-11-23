/*
 * Msg Definitions for the different types used
 * in communication between Android and DE2.
 */

#ifndef MSG_H
#define MSG_H

#include "altera_up_avalon_rs232.h"

#define GAME_STARTED 1
#define GAME_REQUESTED 1
#define FIRST_PLAYER 0
#define SECOND_PLAYER 1
#define THIRD_PLAYER 2
#define FOURTH_PLAYER 3

#define ONE_READY 0x00010001
#define TWO_READY 0x00110011
#define THREE_READY 0x01110111
#define FOUR_READY 0x11111111

typedef enum { JOIN, READY, MOVE, SELECT_CHAR, DISCONNECT, TEST } IN_MSG_TYPE ;
typedef enum { JOIN_RESPONSE, START, CHAR_CHOSEN_MSG} OUT_MSG_TYPE ;
//typedef enum { NO_GAME, INITAL, WAITING, READY, PLAYING, WIN, LOSE } GAME_STATE;
//typedef enum {FREEZE_ALL, FREEZE_ONE, SLOW_ALL, SLOW_ONE, FAST_ONE, RESTART_ONE, RESTART_ALL} POWER_UP_TYPE;

//typedef unsigned char unsigned char;

/* Generic message structure for queuing messages */
struct gMsg {
	unsigned char msgID_;
	unsigned char msgLength_;
	unsigned char clientID_;
	unsigned char* msg_;

	struct gMsg* next;
};

typedef struct gMsg GenericMsg;

typedef struct MoveMsg{
    
    // X & Y
    short x_;
    short y_;


} MoveMsg;

/* TO DO: Load Msg Struct */

/*GAME_STATE getGameState(GameMsg g);
void setGameState(GameMsg g, GAME_STATE gs);
unsigned char getID(GameMsg g);
void setID(GameMsg g, int id);
unsigned char isGameStart(GameMsg g);
void setGameStart(GameMsg g);
unsigned char isGameRequested(GameMsg g);
unsigned char getPlayerJoin(GameMsg g, int player);
unsigned char getPlayerReady(GameMsg g, int player);
void setPlayerJoin(GameMsg g, int player);
void setPlayerReady(GameMsg g, int player);
unsigned char areAllPlayersReady(GameMsg g);

POWER_UP_TYPE getPowerUpType(PowerUpMsg p);
void setPowerUpType(PowerUpMsg p, POWER_UP_TYPE type);
unsigned char getAffectedPlayers(PowerUpMsg p);
void setAffectedPlayers(PowerUpMsg p, int numPlayers);
*/

/*
int getPositionX(MoveMsg m);
int getPositionY(MoveMsg m);
unsigned char getCapturedFlags(MoveMsg m);

void setPositionX(MoveMsg m,  int x);
void setPositionY(MoveMsg m,  int y);
void setCapturedFlags(MoveMsg m, unsigned char flagsCaptured);
void setCapturedFlag(MoveMsg m, int flagID);
*/

/*
 * The following helper functions are for
 * receiving messages:
 */
int parseJoinMsg(GenericMsg* msg);
int parseMoveMsg(GenericMsg* msg);
int parseReadyMsg(GenericMsg* msg);
int parseTestMsg(GenericMsg* msg);
int parseSelectCharMsg(GenericMsg* msg);
int parseDisconnectMsg(GenericMsg *msg);

void sendCharacterChosenMsg(int player_id, unsigned char charID);
void sendJoinResponse(int player_id, unsigned char response,  unsigned char devID);
void sendStartResponse(int player_id);

void writeMsg(alt_up_rs232_dev* uart, GenericMsg* msg);

unsigned int getInt(unsigned char* buf, int offset);
unsigned short getShort(unsigned char* buf, int offset);

#endif
