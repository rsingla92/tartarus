package org.ubc.tartarus;

public class CharMagus extends Character {
	
	private static final Rectangle[] walkLeft = { 
		 new Rectangle(78,304,98,336),
		 //new Rectangle(8,305,24,337),
		 new Rectangle(30,305,46,337),
		 new Rectangle(52,305,74,336),
		 new Rectangle(102,305,118,336),
		 new Rectangle(123,304,141,335),
		 new Rectangle(8,305,24,337) // repeated: trying to get animation fluid
	}; 
	
	private static final Rectangle[] walkRight = { // NEED TO FLIP
		 new Rectangle(78,304,98,336),
		 //new Rectangle(8,305,24,337),
		 new Rectangle(30,305,46,337),
		 new Rectangle(52,305,74,336),
		 new Rectangle(102,305,118,336),
		 new Rectangle(123,304,141,335),
		 new Rectangle(8,305,24,337) // repeated: trying to get animation fluid
	}; 
	
	private static final Rectangle[] walkUp = { 
		 new Rectangle(6,265,26,297),
		 new Rectangle(31,266,54,296),
		 new Rectangle(59,265,81,297),
		 new Rectangle(84,265,105,297),
		 new Rectangle(109,265,132,297),
		 new Rectangle(138,265,160,296) 
	}; 
	
	private static final Rectangle[] walkDown = { 
		 new Rectangle(58,345,80,376),
		 new Rectangle(84,345,106,375),
		 new Rectangle(110,345,132,376),
		 new Rectangle(137,345,158,376),
		 new Rectangle(6,345,28,376),
		 new Rectangle(31,345,52,376)
	}; 
	
	public CharMagus() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].setFlip(true);
		super.setRefFrame((int)(walkLeft[3].topRight.x - walkLeft[3].bottomLeft.x), 
				(int)(walkLeft[3].topRight.y - walkLeft[3].bottomLeft.y));
	}

}
