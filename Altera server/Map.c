/*
 *  This implements the Map class which loads a map from a text file and
 *  can draw it to the screen.
 */
#include "Map.h"
#include <string.h>
#include <stdlib.h>
//#include "sdcard.h"

Map map;

// Constructor
void makeMap( char* mapF, char* tileF, int tileSetWidth, int tileSetHeight)
{
    strcpy(map.mapFile, mapF);
    strcpy(map.tileFile, tileF);
    map.setWidth = tileSetWidth;
    map.setHeight = tileSetHeight;
}

// Destructor
void cleanupMap()
{
    if( map.mapInfo )
    {
        free(map.mapInfo);
    }

    if ( map.tileset )
    {
        free(map.tileset);
    }

}

// drawScreenAll:
// This simply draws this map (in the current viewport) to the full screen.
// Params: portX/portY - Position of the viewport
void drawScreenAll( int portX,
                    int portY )
{
    /* Indices of the first tile*/ 
    int tilePortX = portX / TILE_WIDTH;
    int tilePortY = portY / TILE_HEIGHT;

    int endPortX = ( SCREEN_WIDTH ) / (INT_SIZE) + tilePortX;
    int endPortY = SCREEN_HEIGHT/INT_SIZE + tilePortY;

    if(endPortX > map.mapWidth) endPortX--; 
    if(endPortY > map.mapHeight) endPortY--;

    int i;
    int j;

    for(i = tilePortX; i < endPortX; ++i)
    {
        for( j = tilePortY; j < endPortY; ++j)
        {
            int ID = map.mapInfo[i+j*(map.mapWidth)].id;
            int xImage = (ID%(map.setWidth/INT_SIZE))*TILE_WIDTH;
            int yImage = (ID/(map.setWidth/INT_SIZE))*TILE_HEIGHT;

            // draw the image using the
            // tileset
            // xImage, yImage
            // TILE_WIDTH, TILE_HEIGHT,
            // i-tilePortX * TILE_WIDTH
            // j-tilePortY * TILE_HEIGHT
            // 0
        }
    }

    return;
}

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
                        int offsetVert)
{
    if( startTileX < 0 || startTileY < 0 || numXTiles < 0 || numYTiles < 0) return;

    int i;
    int j;

    for(i = startTileX; i < startTileX+numXTiles; i++)
    {
        for(j = startTileY; j < startTileY+numYTiles; j++)
        {
            int ID = map.mapInfo[i+j*(map.mapWidth)].id;
            int xImage = (ID%(map.setWidth/INT_SIZE))*TILE_WIDTH;
            int yImage = (ID/(map.setWidth/INT_SIZE))*TILE_HEIGHT;

            // draw the bitmap at using
            // tileset
            // xImage
            // yImage
            // TILE_WIDTH
            // TILE_HEIGHT 
            //(i-startTileX)*TILE_WIDTH + startMapX*TILE_WIDTH + offsetHoriz
            // (j-startTileY)*TILE_HEIGHT + startMapY*TILE_HEIGHT + offsetVert
            //0
        }
    }

}

// initialize:
// Loads the map from the map file and stores it in the array of Tiles
int initialize()
{
    int newWidth;
    int newHeight;
    int boolVal;

    // Open the file to parse

    // Read the header
    readMapFileHeader();

    map.mapInfo = (Tile*) malloc(map.sizeInfo * sizeof(Tile));

    // Read the file
    int i;
    for( i = 0; i < map.sizeInfo; ++i)
    {
        // Check if the file is null
        //if() break;

        // Read the file into 
        // read file and assign map.mapInfo[i].id;
        
        // read file and assign to boolVal;

        // set map.mapInfo[i].solid to boolVal;
    }

    // Close the file

    // Load the tileset image.
    // map.tileset 

    // Check if failed to load bitmap


    return 1;

}

void readMapFileHeader(void)
{
    // Read the txt file header
    // Retrieve the map width ( first number )
    // Retrieve the map height (second number)

    map.mapWidth;
    map.mapHeight; 
    map.sizeInfo;
}
