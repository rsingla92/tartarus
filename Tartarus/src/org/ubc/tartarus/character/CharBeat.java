package org.ubc.tartarus.character;

import org.ubc.tartarus.R;
import org.ubc.tartarus.utils.Rectangle;

public class CharBeat extends Character {
	// skateboard
	private static final Rectangle[] walkLeft = { 
		 new Rectangle(388,1057,429,1114),
		 new Rectangle(439,1057,480,1116),
		 new Rectangle(487,1058,528,1116),
		 new Rectangle(536,1058,577,1116),
		 new Rectangle(584,1058,625,1119),
		 new Rectangle(633,1059,674,1117),
	}; 
	
	// running
	/*private static final Rectangle[] walkLeft = { 
		 new Rectangle(543,1269,576,1324),
		 new Rectangle(582,1272,628,1324),
		 new Rectangle(632,1270,681,1323),
		 new Rectangle(686,1270,726,1323),
		 new Rectangle(733,1269,766,1323),
		 new Rectangle(772,1271,817,1324),
		 new Rectangle(437,12710,486,1325),
		 new Rectangle(492,12701,533,1324),
	};
	*/
	private static final Rectangle[] walkRight = { // NEED TO FLIP
		 new Rectangle(388,1057,429,1114),
		 new Rectangle(439,1057,480,1116),
		 new Rectangle(487,1058,528,1116),
		 new Rectangle(536,1058,577,1116),
		 new Rectangle(584,1058,625,1119),
		 new Rectangle(633,1059,674,1117),
	}; 
	
	private static final Rectangle[] walkUp = { 
		 new Rectangle(379,1199,406,1253),
		 new Rectangle(411,1195,439,1253),
		 new Rectangle(448,1195,476,1254),
		 new Rectangle(484,1203,512,1254),
		 new Rectangle(520,1200,548,1254),
		 new Rectangle(554,1196,582,1253),
		 new Rectangle(588,1195,614,1254),
		 new Rectangle(346,1202,372,1253),
	}; 
	
	private static final Rectangle[] walkDown = { 
		 new Rectangle(84,1197,111,1251),
		 new Rectangle(118,1197,146,1256),
		 new Rectangle(152,1197,183,1255),
		 new Rectangle(188,1197,218,1253),
		 new Rectangle(223,1197,252,1251),
		 new Rectangle(257,1196,285,1256),
		 new Rectangle(11,1198,40,1256),
		 new Rectangle(48,1197,76,1253),
	}; 
	
	public CharBeat() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].setFlip(true);
		super.setRefFrame((int)(walkLeft[3].topRight.x - walkLeft[3].bottomLeft.x), 
				(int)(walkLeft[3].topRight.y - walkLeft[3].bottomLeft.y));
		super.setResource(R.drawable.sprite_beat);
	}

}
