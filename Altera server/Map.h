/*
 *  This defines the Map structure which loads a map from a text file
 *  and draws it to the screen. 
 */

#ifndef MAP_H
#define MAP_H
#include "bitmap.h"

#define INT_SIZE 32
#define TILE_WIDTH 16
#define TILE_HEIGHT 16
#define SCREEN_WIDTH 100
#define SCREEN_HEIGHT 100

enum OFFSET_DIR_HORIZ { OFFSET_RIGHT, OFFSET_LEFT, HORIZ_NONE };
enum OFFSET_DIR_VERT { OFFSET_UP, OFFSET_DOWN, VERT_NONE };

struct BitmapHandle; 

typedef struct {
    char* mapFile;
    int mapWidth;
    int mapHeight;
    int sizeInfo;

    /* One array representing solid-map. */
    unsigned char* mapInfo;

} Map;

typedef struct {
	unsigned short x, y;
} Point;

// Constructor
void makeMap( char*);

// Destructor
void cleanupMap();

// drawScreenPortion:
// This draws a portion of the map to the map at a specified starting location.
// Params: startMapX + startMapY - position on screen (relative) to begin drawing in tiles
// Params: startTileX + startTileY - position in array to begin drawing, in tiles.
// Params: numXTiles/numYTiles - the width and height of portion in tiles.
void drawMapPortion( int startTileX,
                        int startTileY,
                        int numXTiles,
                        int numYTiles);

void readMapFileHeader(file_handle handle);

unsigned char isTileSolid(unsigned short tileId);
int readShort(file_handle handle);
void erasePositionAt(short absX, short absY);
void drawPlayerAt(short absX, short absY, colour col);
Point getRandomPoint(int quadNum);
int getQuadrant(short row, short col);

#endif
