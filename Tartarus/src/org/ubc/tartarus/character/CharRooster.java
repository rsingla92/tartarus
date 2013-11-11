package org.ubc.tartarus.character;
import org.ubc.tartarus.utils.Rectangle;

public class CharRooster extends Character {

	private static final Rectangle[] walkLeft = { 
		 new Rectangle(84,128,114,179),
		 new Rectangle(46,129,78,181),
		 new Rectangle(6,131,36,180),
		 new Rectangle(123,127,152,176)
	}; 
	
	private static final Rectangle[] walkRight = {
		 new Rectangle(44,70,75,120),
		 new Rectangle(80,72,112,122),
		 new Rectangle(122,72,151,122),
		 new Rectangle(4,69,34,118),
	}; 
	
	private static final Rectangle[] walkUp = { 
		 new Rectangle(6,4,31,56),
		 new Rectangle(48,0,73,60),
		 new Rectangle(82,4,108,56),
		 new Rectangle(124,0,148,55), 
	}; 
	
	private static final Rectangle[] walkDown = { 
		 new Rectangle(10,191,34,246),
		 new Rectangle(48,192,74,248),
		 new Rectangle(85,190,109,246),
		 new Rectangle(124,190,150,243), 
	}; 
	
	public CharRooster() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].setFlip(false);
		super.setRefFrame((int)(walkLeft[2].topRight.x - walkLeft[2].bottomLeft.x), 
				(int)(walkLeft[2].topRight.y - walkLeft[2].bottomLeft.y));
	}
}
