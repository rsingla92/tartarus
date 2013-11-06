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

enum MSG_TYPE { GAME, MOVE, POWER_UP };
enum GAME_STATE { NO_GAME, INITAL, WAITING, READY, PLAYING, WIN, LOSE };
enum POWER_UP_TYPE {FREEZE_ALL, FREEZE_ONE, SLOW_ALL, SLOW_ONE, FAST_ONE, RESTART_ONE, RESTART_ALL};

typedef unsigned char byte;

typedef struct GameMsg{
    // GAME_STATE
    GAME_STATE gameState_;

    // ASSIGNED ID
    int id_;

    // GAME START SIGN
    byte gameStart_;

    // Game request flag
    byte gameRequest_;

    // This variable refers to the players state 
    // while in the lobby. The bottom four bits represent
    // if a player has joined. The upper four bits represent
    // if a player is ready.
    byte lobbyState_;

} GameMsg;

typedef struct PowerUpMsg{
    // POWER UP TYPE
    POWER_UP_TYPE type_;

    // POWER ARGUMENTS

    // PLAYER(S) TO AFFECT
    byte players_;
} PowerUpMsg;

typedef struct MoveMsg{
    // PLAYER X IS IN PLAYER Y'S VIEWPORT
    
    // MOVEMENT REQUEST
    
    // X & Y
    unsigned short x_;
    unsigned short y_;

    // CAPTURED FLAG
    // In the form where the first four bits are T/F
    // The upper four bits indicate which flag is captured
    unsigned char flagCaptured_;

} MoveMsg;

GAME_STATE getGameState(GameMsg g);
void setGameState(GameMsg g, GAME_STATE gs);
byte getID(GameMsg g);
void setID(GameMsg g, int id);
byte isGameStart(GameMsg g);
void setGameStart(GameMsg g);
byte isGameRequested(GameMsg g);
byte getPlayerJoin(GameMsg g, int player);
byte getPlayerReady(GameMsg g, int player);
void setPlayerJoin(GameMsg g, int player);
void setPlayerReady(GameMsg g, int player);
byte areAllPlayersReady(GameMsg g);

POWER_UP_TYPE getPowerUpType(PowerUpMsg p);
void setPowerUpType(PowerUpMsg p, POWER_UP_TYPE type);
byte getAffectedPlayers(PowerUpMsg p);
void setAffectedPlayers(PowerUpMsg p, int numPlayers);
