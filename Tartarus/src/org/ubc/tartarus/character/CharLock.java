package org.ubc.tartarus.character;

import org.ubc.tartarus.R;
import org.ubc.tartarus.utils.Rectangle;

public class CharLock extends Character {
	private static final Rectangle[] walkLeft = { 
		new Rectangle(189,616,209,657),
		new Rectangle(214,616,234,659),
		new Rectangle(238,616,258,658),
		new Rectangle(262,615,283,657),
		new Rectangle(287,616,308,659),
		new Rectangle(310,617,331,658),
	}; 
	
	private static final Rectangle[] walkRight = { // NEED TO FLIP 
		new Rectangle(189,616,209,657),
		new Rectangle(214,616,234,659),
		new Rectangle(238,616,258,658),
		new Rectangle(262,615,283,657),
		new Rectangle(287,616,308,659),
		new Rectangle(310,617,331,658),
	}; 
	
	private static final Rectangle[] walkUp = { 
		new Rectangle(186,519,206,565),
		new Rectangle(212,519,234,562),
		new Rectangle(239,520,261,564),
		new Rectangle(265,520,286,565),
		new Rectangle(289,520,311,562),
		new Rectangle(317,520,337,564),
	}; 
	
	private static final Rectangle[] walkDown = { 
		new Rectangle(186,710,208,753),
		new Rectangle(212,710,234,755),
		new Rectangle(240,710,260,755),
		new Rectangle(265,710,287,753),
		new Rectangle(292,710,313,755),
		new Rectangle(319,710,339,755),
	}; 
	
	public CharLock() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].setFlip(true);
		super.setRefFrame((int)(walkDown[2].topRight.x - walkDown[2].bottomLeft.x), 
				(int)(walkDown[2].topRight.y - walkDown[2].bottomLeft.y));
		super.setResource(R.drawable.sprite_lock);
	}
}
