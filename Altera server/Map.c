/*
 *  This implements the Map class which loads a map from a text file and
 *  can draw it to the screen.
 */
#include "Map.h"
#include "display.h"
#include <string.h>
#include <stdlib.h>
#include "sdcard.h"
#include "random.h"

Map map;

static int initializeMap(void);

static Point *quad1, *quad2, *quad3, *quad4;
static int quad1Size, quad2Size, quad3Size, quad4Size;

// Constructor
void makeMap( char* mapF)
{
    strcpy(map.mapFile, mapF);
    initializeMap();
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
// Params: startTileX + startTileY - position in array to begin drawing, in tiles.
// Params: numXTiles/numYTiles - the width and height of portion in tiles.
void drawMapPortion( int startTileX,
                        int startTileY,
                        int numXTiles,
                        int numYTiles)
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

void erasePositionAt(short absX, short absY)
{
	colour blackCol;
	blackCol.r = blackCol.g = blackCol.b = 0;

	drawPlayerAt(absX, absY, blackCol);
}

void drawPlayerAt(short absX, short absY, colour col)
{
	int tileX = absX / TILE_WIDTH;
	int tileY = absY / TILE_HEIGHT;

	printf("Tile X: %d, Tile Y: %d\n", tileX, tileY);
	if( tileX < 0 || tileY < 0 || tileX >= map.mapWidth || tileY >= map.mapHeight) return;

	printf("Drawing pixel, with colour (%d, %d, %d)\n", col.r, col.g, col.b);
	draw_pixel(tileX, tileY, col);
}

static int insertQuadPoint(Point* quad, int* size, int col, int row)
{
	int curIndex = (*size)++;

	quad[curIndex].x = col;
	quad[curIndex].y = row;
}

// initialize:
// Loads the map from the map file and stores it in the solid-map array.
static int initializeMap(void)
{
    // Open the file to parse
    file_handle map_file = open_file(map.mapFile, false);

    // Read the header
    readMapFileHeader(map_file);

    map.mapInfo = (unsigned char*) malloc(map.sizeInfo * sizeof(unsigned char));
    quad1 = (Point*) malloc((map.sizeInfo/4)*sizeof(Point));
    quad2 = (Point*) malloc((map.sizeInfo/4)*sizeof(Point));
    quad3 = (Point*) malloc((map.sizeInfo/4)*sizeof(Point));
    quad4 = (Point*) malloc((map.sizeInfo/4)*sizeof(Point));
    quad1Size = quad2Size = quad3Size = quad4Size = 0;

    // Read the file
    int i;
    for( i = 0; i < map.sizeInfo; ++i)
    {
    	// Set the solid map
        map.mapInfo[i] = isTileSolid(readShort(map_file));

        if (map.mapInfo[i] == 0) {
			int row = i / map.mapWidth;
			int col = i % map.mapWidth;

			if (row < map.mapWidth/2)
			{
				// Left half of map
				if (col < map.mapHeight/2)
				{
					// Quadrant 1
					insertQuadPoint(quad1, &quad1Size, col, row);
				}
				else
				{
					// Quadrant 3
					insertQuadPoint(quad3, &quad3Size, col, row);
				}
			}
			else
			{
				// Right half
				if (col < map.mapHeight/2)
				{
					// Quadrant 2
					insertQuadPoint(quad2, &quad2Size, col, row);
				}
				else
				{
					// Quadrant 4
					insertQuadPoint(quad4, &quad4Size, col, row);
				}
			}
        }
    }

    // Close the file
    close_file(map_file);

    return 1;

}

Point getRandomPoint(int quadNum)
{
	Point* quad;
	int quadSize;

	switch(quadNum)
	{
		case 1:
			quad = quad1;
			quadSize = quad1Size;
			break;
		case 2:
			quad = quad2;
			quadSize = quad2Size;
			break;
		case 3:
			quad = quad3;
			quadSize = quad3Size;
			break;
		case 4:
		default:
			quad = quad4;
			quadSize = quad4Size;
			break;

	}

	int rand = nextRand() % quadSize;
	return quad[rand];
}

int readShort(file_handle handle)
{
	char buf[10]; // No short will be this large...
	int i;

	for (i = 0; i < 9; i++) {
		buf[i] = read_file(handle);

		if (buf[i] == ' ' || buf[i] == '\0') break;
	}

	// Terminate string
	buf[i] = '\0';

	return atoi(buf);
}

void readMapFileHeader(file_handle handle)
{
	printf("Reading Header!");
	map.mapWidth = readShort(handle);
	printf("Width: %d\n", map.mapWidth);
    map.mapHeight = readShort(handle);
	printf("Height: %d\n", map.mapHeight);
    map.sizeInfo = map.mapWidth * map.mapHeight;
}

// For now, just tells us if a tile is solid or not.
// In the future, we could extend this to give a code representing the type of
// terrain of the tile (castle, lava, etc...), so that we could draw a different
// colour depending on the type (i.e., green for grassland, red for lava, ...).
unsigned char isTileSolid(unsigned short tileId)
{
	// Returns if the tile with id tileId is solid.
	if (tileId == 11 || tileId == 28 || tileId == 29 || tileId == 34 ||(tileId >= 36 && tileId <= 63) ||
			tileId == 61 || tileId == 62 || (tileId >= 72 && tileId <= 79) || (tileId >= 180 && tileId <= 183) ||
			(tileId >= 288 && tileId <= 295) || tileId == 329 || (tileId >= 398 && tileId <= 400) || (tileId >= 468 && tileId <= 478) ||
			(tileId >= 504 && tileId <= 521) || (tileId >= 540 && tileId <= 555) || (tileId >= 597 && tileId <= 600) ||
			(tileId >= 648 && tileId <= 677) || (tileId >= 699 && tileId <= 711) || (tileId >= 720 && tileId <= 738) ||
			(tileId >= 756 && tileId <= 757) || (tileId >= 792 && tileId <= 797) || tileId == 801 || tileId == 806 ||
			tileId == 818 || (tileId >= 828 && tileId <= 845) || (tileId >= 853 && tileId <= 857) || tileId == 864 ||
			tileId == 865 || tileId == 906 || tileId == 907 ||tileId == 908 || tileId == 914 || (tileId >= 936 && tileId <= 961)) {
		return 1;
	}

	return 0;
}
