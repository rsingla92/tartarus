package org.ubc.tartarus.character;

import org.ubc.tartarus.utils.Rectangle;

public class CharSerdic extends Character {
	private static final Rectangle[] walkLeft = { 
		 new Rectangle(5,58,26,94),
		 new Rectangle(39,58,59,92),
		 new Rectangle(71,58,92,94),
		 new Rectangle(104,58,125,93),
	}; 
	
	private static final Rectangle[] walkRight = { 
		 new Rectangle(5,10,26,46),
		 new Rectangle(37,10,58,44),
		 new Rectangle(69,10,90,46),
		 new Rectangle(101,10,122,45),
	}; 
	
	private static final Rectangle[] walkUp = { 
		 new Rectangle(4,106,25,142),
		 new Rectangle(36,106,57,141),
		 new Rectangle(68,106,89,142),
		 new Rectangle(100,106,121,141),
	}; 
	
	private static final Rectangle[] walkDown = { 
		 new Rectangle(5,154,26,190),
		 new Rectangle(36,154,58,189),
		 new Rectangle(69,154,90,190),
		 new Rectangle(101,154,122,189),
	}; 
	
	public CharSerdic() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].setFlip(false);
		super.setRefFrame((int)(walkDown[1].topRight.x - walkDown[1].bottomLeft.x), 
				(int)(walkDown[1].topRight.y - walkDown[1].bottomLeft.y));
	}
}
