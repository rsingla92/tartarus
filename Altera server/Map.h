/*
 *  This defines the Map structure which loads a map from a text file
 *  and draws it to the screen. 
 */

#define INT_SIZE 32
#define TILE_WIDTH 100
#define TILE_HEIGHT 100 
#define SCREEN_WIDTH 100
#define SCREEN_HEIGHT 100

enum OFFSET_DIR_HORIZ { OFFSET_RIGHT, OFFSET_LEFT, HORIZ_NONE };
enum OFFSET_DIR_VERT { OFFSET_UP, OFFSET_DOWN, VERT_NONE };

struct BitmapHandle; 

typedef struct Tile {
    int id;
    int solid;
    BitmapHandle* tile;
} Tile;

typedef struct Map {
    char* mapFile;
    char* tileFile;
    int mapWidth;
    int mapHeight;
    int sizeInfo;
    int setWidth;
    int setHeight;

    /* One array containing the tile information for the map */
    Tile* mapInfo;

    /* Image for the tilset */
    BitmapHandle* tileset;

} Map;

// Constructor
void makeMap( char*, char*, int, int);

// Destructor
void cleanupMap();

// drawScreenAll:
// This simply draws this map (in the current viewport) to the full screen.
// Params: portX/portY - Position of the viewport
void drawScreenAll( int portX,
                    int portY );

// drawScreenPortion:
// This draws a portion of the map to the map at a specified starting location.
// Params: startMapX + startMapY - position on screen (relative) to begin drawing in tiles
// Params: startTileX + startTileY - position in array to begin drawing, in tiles.
// Params: numXTiles/numYTiles - the width and height of portion in tiles.
void drawScreenPortion( int startMapX, 
                        int startMapY,
                        int startTileX,
                        int startTileY,
                        int numXTiles,
                        int numYTiles,
                        int offsetHoriz,
                        int offsetVert);

// initialize:
// Loads the map from the map file and stores it in the array of Tiles
int initialize();

void readMapFileHeader(void);
