package org.ubc.tartarus.character;

import org.ubc.tartarus.R;
import org.ubc.tartarus.utils.Rectangle;

public class CharNeku extends Character {
	
	private static final Rectangle[] walkLeft = { 
		 new Rectangle(263,1316,298,1368),
		 new Rectangle(305,1316,348,1372),
		 new Rectangle(363,1318,415,1367),
		 new Rectangle(421,1317,473,1367),
		 new Rectangle(477,1316,514,1367),
		 new Rectangle(520,1316,562,1371),
		 new Rectangle(573,1317,625,1365),
	}; 
	
	private static final Rectangle[] walkRight = { // NEED TO FLIP
		 new Rectangle(263,1316,298,1368),
		 new Rectangle(305,1316,348,1372),
		 new Rectangle(363,1318,415,1367),
		 new Rectangle(421,1317,473,1367),
		 new Rectangle(477,1316,514,1367),
		 new Rectangle(520,1316,562,1371),
		 new Rectangle(573,1317,625,1365),
	}; 
	
	private static final Rectangle[] walkUp = { 
		 new Rectangle(216,1179,237,1229),
		 new Rectangle(7,1175,28,1228),
		 new Rectangle(37,1175,56,1231),
		 new Rectangle(67,1168,89,1228),
		 new Rectangle(96,1177,118,1227),
		 new Rectangle(126,1175,148,1228),
		 new Rectangle(156,1175,176,1231),
		 new Rectangle(185,1169,206,1229), 
	}; 
	
	private static final Rectangle[] walkDown = { 
		 new Rectangle(93,1244,113,1297),
		 new Rectangle(123,1238,145,1298),
		 new Rectangle(156,1242,177,1298),
		 new Rectangle(187,1241,208,1298),
		 new Rectangle(218,1244,238,1297),
		 new Rectangle(6,1238,29,1298),
		 new Rectangle(36,1242,57,1298),
		 new Rectangle(64,1241,85,1298),
	}; 
	
	public CharNeku() {
		super(walkLeft, walkRight, walkUp, walkDown);
		animList[AnimTypes.WALK_RIGHT.ordinal()].setFlip(true);
		super.setRefFrame((int)(walkLeft[3].topRight.x - walkLeft[3].bottomLeft.x), 
				(int)(walkLeft[3].topRight.y - walkLeft[3].bottomLeft.y));
		super.setResource(R.drawable.sprite_neku);
	}

}
