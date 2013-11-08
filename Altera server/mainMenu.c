/*
 * mainMenu.c
 *
 *  Created on: Oct 8, 2013
 *      Author: singla
 */

#include "mainMenu.h"
#include "bitmap.h"
#include "state_machine.h"

static BitmapHandle* background_bmp;
static colour back_alpha = { 0x00, 0x00, 0x00 };
extern unsigned char button_states[4];
extern controller_buttons controller_state;
extern controller_buttons prev_controller_state;

void updateMainMenu(void)
{
	// Start in this state
	// Wait for a game request
	// If game request then transition to lobby
	// no character bitmap loading needed
	//if (!button_states[0] ||
	//		(!prev_controller_state.START && controller_state.START))
	//{
	//
	//		close_bmp(background_bmp);
	//	changeState(LOADING_SCREEN);
//	}

	printf("Updating main \n");


}

void draw_main_menu(void)
{
	/*
	short int ret = load_bmp("MM1.BMP", &background_bmp);
	// Draw the background to both buffers.
	draw_bmp(background_bmp, 0, 240 - background_bmp->bmp_info_header->height,
			false, back_alpha, 1);
	swap_buffers();
	draw_bmp(background_bmp, 0, 240 - background_bmp->bmp_info_header->height,
			false, back_alpha, 1);
	*/

	printf("Drawing main menu \n");

}

