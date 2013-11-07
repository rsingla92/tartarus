package org.ubc.tartarus;

public class CharMagus extends Character {
	
	private static final Rectangle[] walkLeft = { 
		 new Rectangle(78,124,98,92),
		 new Rectangle(8,123,24,91),
		 new Rectangle(30,123,46,91),
		 new Rectangle(52,123,74,92),
		 new Rectangle(102,123,118,92),
		 new Rectangle(123,124,141,93),
		 new Rectangle(8,123,24,91) // repeated: trying to get animation fluid
	}; 
	
	private static final Rectangle[] walkRight = { // NEED TO FLIP
		 new Rectangle(78,124,98,92),
		 new Rectangle(8,123,24,91),
		 new Rectangle(30,123,46,91),
		 new Rectangle(52,123,74,92),
		 new Rectangle(102,123,118,92),
		 new Rectangle(123,124,141,93),
		 new Rectangle(8,123,24,91) // repeated: trying to get animation fluid
	}; 
	
	private static final Rectangle[] walkUp = { 
		 new Rectangle(6,163,26,131),
		 new Rectangle(31,162,54,132),
		 new Rectangle(59,163,81,131),
		 new Rectangle(84,163,105,131),
		 new Rectangle(109,163,132,131),
		 new Rectangle(138,163,160,132) 
	}; 
	
	private static final Rectangle[] walkDown = { 
		 new Rectangle(58,83,80,52),
		 new Rectangle(84,83,106,53),
		 new Rectangle(110,83,132,52),
		 new Rectangle(137,83,158,52),
		 new Rectangle(6,83,28,52),
		 new Rectangle(31,83,52,52)
	}; 
	
	public CharMagus() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].flip= true;
	}

}
