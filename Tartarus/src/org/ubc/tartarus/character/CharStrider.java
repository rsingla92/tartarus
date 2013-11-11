package org.ubc.tartarus.character;

import org.ubc.tartarus.R;
import org.ubc.tartarus.utils.Rectangle;

public class CharStrider extends Character {
	private static final Rectangle[] walkLeft = { 
		 new Rectangle(6,54,22,87),
		 new Rectangle(37,54,55,88),
		 new Rectangle(70,54,87,87),
		 new Rectangle(102,54,119,88),
	}; 
	
	private static final Rectangle[] walkRight = { 
		 new Rectangle(5,6,22,40),
		 new Rectangle(38,6,54,39),
		 new Rectangle(69,6,86,40),
		 new Rectangle(101,6,117,39),
	}; 
	
	private static final Rectangle[] walkUp = { 
		 new Rectangle(4,102,25,135),
		 new Rectangle(35,102,58,136),
		 new Rectangle(68,102,89,135),
		 new Rectangle(99,102,122,136),
	}; 
	
	private static final Rectangle[] walkDown = { 
		 new Rectangle(3,150,24,186),
		 new Rectangle(34,151,57,187),
		 new Rectangle(67,150,88,186),
		 new Rectangle(98,151,121,187),
	}; 
	
	public CharStrider() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].setFlip(false);
		super.setRefFrame((int)(walkDown[1].topRight.x - walkDown[1].bottomLeft.x), 
				(int)(walkDown[1].topRight.y - walkDown[1].bottomLeft.y));
		super.setResource(R.drawable.sprite_strider);
	}
}
