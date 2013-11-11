/*
 * Msg Definitions for the different types used
 * in communication between Android and DE2.
 */

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

typedef enum { LOAD, GAME, MOVE, POWER_UP, TEST } MSG_TYPE ;
typedef enum { NO_GAME, INITAL, WAITING, READY, PLAYING, WIN, LOSE } GAME_STATE;
typedef enum {FREEZE_ALL, FREEZE_ONE, SLOW_ALL, SLOW_ONE, FAST_ONE, RESTART_ONE, RESTART_ALL} POWER_UP_TYPE;

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

typedef struct GameMsg{
    // GAME_STATE
	GAME_STATE gameState_;

    // ASSIGNED ID
    int id_;

    // GAME START SIGN
    unsigned char gameStart_;

    // Game request flag
    unsigned char gameRequest_;

    // This variable refers to the players state 
    // while in the lobby. The bottom four bits represent
    // if a player has joined. The upper four bits represent
    // if a player is ready.
    unsigned char lobbyState_;

} GameMsg;

typedef struct PowerUpMsg{
    // POWER UP TYPE
    POWER_UP_TYPE type_;

    // POWER ARGUMENTS

    // PLAYER(S) TO AFFECT
    unsigned char players_;
} PowerUpMsg;

typedef struct MoveMsg{
    // PLAYER X IS IN PLAYER Y'S VIEWPORT
    
    // MOVEMENT REQUEST
    
    // X & Y
    int x_;
    int y_;

    // CAPTURED FLAG
    // In the form where the first four bits are T/F
    // The upper four bits indicate which flag is captured
    unsigned char flagsCaptured_;

} MoveMsg;

/* TO DO: Load Msg Struct */

GAME_STATE getGameState(GameMsg g);
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

int getPositionX(MoveMsg m);
int getPositionY(MoveMsg m);
unsigned char getCapturedFlags(MoveMsg m);

void setPositionX(MoveMsg m,  int x);
void setPositionY(MoveMsg m,  int y);
void setCapturedFlags(MoveMsg m, unsigned char flagsCaptured);
void setCapturedFlag(MoveMsg m, int flagID);

/* TO DO : Implement */
// Helper function to determine Android message type
int makeLoadMsg(GenericMsg* msg);
int makeGameMsg(GenericMsg* msg);
int makeMoveMsg(GenericMsg* msg);
int makePowerUpMsg(GenericMsg* msg);
int makeTestMsg(GenericMsg* msg);

void readMsg();
void makeMsg();
void sendMsg();
