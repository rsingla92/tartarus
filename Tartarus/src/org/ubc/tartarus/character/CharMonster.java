package org.ubc.tartarus.character;

import org.ubc.tartarus.R;
import org.ubc.tartarus.utils.Rectangle;

public class CharMonster extends Character {
	private static final Rectangle[] walkLeft = { 
		 new Rectangle(194,384-189,287,384-100),
		 new Rectangle(97,384-191,192,384-101),
		 new Rectangle(0,384-189,94,384-99),
		 new Rectangle(97,384-191,192,384-101),
//		 new Rectangle(131,194,186,268),
//		 new Rectangle(37,194,87,268),
//		 new Rectangle(131,194,186,268),
//		 new Rectangle(228,194,279,267),
	}; 
	
	private static final Rectangle[] walkRight = { 
		new Rectangle(193,384-286,286,384-198),
		new Rectangle (96,384-286,190,384-196),
		new Rectangle (0,384-285,96,384-196),
		new Rectangle (96,384-286,190,384-196),
		
//		 new Rectangle(103,99,156,172),
//		 new Rectangle(7,97,59,172),
//		 new Rectangle(103,99,156,172),
//		 new Rectangle(198,97,251,172),
	}; 
	
	private static final Rectangle[] walkUp = { 
		 new Rectangle (100,384-382,190,384-288),
		 new Rectangle (11,384-382,90,384-290),
		 new Rectangle (100,384-382,190,384-288),
		 new Rectangle (192,384-382,278,384-290),
		
//		 new Rectangle(99,2,190,79),
//		 new Rectangle(3,1,93,76),
//		 new Rectangle(99,2,190,79),
//		 new Rectangle(194,1,285,76),
	}; 
	
	private static final Rectangle[] walkDown = { 
		 new Rectangle(96,384-94,192,384-2),
		 new Rectangle(4,384-94,92,384-2),
		 new Rectangle(96,384-94,192,384-2),
		 new Rectangle(198,384-90,284,384-2),
		 
//		 new Rectangle(98,288,189,363),
//		 new Rectangle(4,288,94,362),
//		 new Rectangle(98,288,189,363),
//		 new Rectangle(194,290,285,361),
	}; 
	
	public CharMonster() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].setFlip(false);
		super.setRefFrame((int)(walkDown[1].topRight.x - walkDown[1].bottomLeft.x), 
				(int)(walkDown[1].topRight.y - walkDown[1].bottomLeft.y));
		super.setResource(R.drawable.sprite_dragon);
	}
}
