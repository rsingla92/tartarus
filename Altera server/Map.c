/*
 *  This implements the Map class which loads a map from a text file and
 *  can draw it to the screen.
 */
#include "Map.h"
#include "display.h"
#include <string.h>
#include <stdlib.h>
#include "sdcard.h"

Map map;

// Constructor
void makeMap( char* mapF)
{
    strcpy(map.mapFile, mapF);
}

// Destructor
void cleanupMap()
{
    if( map.mapInfo )
    {
        free(map.mapInfo);
    }
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

    int i, j;
	colour solidCol;
	solidCol.r = 127;
	solidCol.g = 127;
	solidCol.b = 127;

    for(i = startTileX; i < startTileX + numXTiles; i++)
    {
        for(j = startTileY; j < startTileY + numYTiles; j++)
        {
            unsigned char tile_type = map.mapInfo[i+j*(map.mapWidth)];

            if (tile_type == 1)
            {
            	// Solid -- right now assuming that the world map
            	// and the map file are the same size... (scaling 1:1).
            	draw_pixel(i, j, solidCol);
            }
        }
    }

}

// initialize:
// Loads the map from the map file and stores it in the solid-map array.
int initialize(void)
{
    // Open the file to parse
    file_handle map_file = open_file(map.mapFile, false);

    // Read the header
    readMapFileHeader(map_file);

    map.mapInfo = (unsigned char*) malloc(map.sizeInfo * sizeof(unsigned char));

    // Read the file
    int i;
    for( i = 0; i < map.sizeInfo; ++i)
    {
    	// Set the solid map
        map.mapInfo[i] = isTileSolid(readShort(map_file));
    }

    // Close the file
    close_file(map_file);

    return 1;

}

int readShort(file_handle handle)
{
	char buf[10]; // No short will be this large...
	int i;

	for (i = 0; i < 9; i++) {
		buf[i] = read_file(handle);

		if (buf[i] == ' ' || buf[i] == '\0');
	}

	// Terminate string
	buf[i] = '\0';

	return atoi(buf);
}

void readMapFileHeader(file_handle handle)
{
	map.mapWidth = readShort(handle);
    map.mapHeight = readShort(handle);
    map.sizeInfo = map.mapWidth * map.mapHeight;
}

// For now, just tells us if a tile is solid or not.
// In the future, we could extend this to give a code representing the type of
// terrain of the tile (castle, lava, etc...), so that we could draw a different
// colour depending on the type (i.e., green for grassland, red for lava, ...).
unsigned char isTileSolid(unsigned short tileId)
{
	// Returns if the tile with id tileId is solid.
	if (tileId == 0) return 1;

	return 0;
}
