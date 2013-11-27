/*
 * Msg Definitions for the different types used
 * in communication between Android and DE2.
 */

#ifndef MSG_H
#define MSG_H

#include "Map.h"

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

typedef enum { JOIN, READY, MOVE, SELECT_CHAR, DISCONNECT, GEM_ACK, GEM_PICKED, TEST } IN_MSG_TYPE ;
typedef enum { JOIN_RESPONSE, START, CHAR_CHOSEN_MSG, GEM_MSG, UPDATE_GEM, GAME_OVER_MSG, PLAYER_POS_MSG} OUT_MSG_TYPE ;
//typedef unsigned char unsigned char;

/* Generic message structure for queuing messages */
struct gMsg {
	unsigned char msgID_;
	unsigned short msgLength_;
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
int parseGemPicked(GenericMsg *msg);

void sendCharacterChosenMsg(int player_id, unsigned char charID);
void sendJoinResponse(int player_id, unsigned char response,  unsigned char devID);
void sendStartResponse(void);
void sendGemMsg(int player_id);
void sendUpdateGemMsg(int player_id, Point newGem, Point oldGem);
void sendGameOverMsg(void);
void sendPlayerPosMsg(void);

void writeMsg(alt_up_rs232_dev* uart, GenericMsg* msg);

unsigned int getInt(unsigned char* buf, int offset);
unsigned short getShort(unsigned char* buf, int offset);

#endif
