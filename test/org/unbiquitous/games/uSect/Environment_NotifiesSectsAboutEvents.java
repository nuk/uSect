package org.unbiquitous.games.uSect;

import static org.fest.assertions.api.Assertions.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;

public class Environment_NotifiesSectsAboutEvents {

	private Environment e;

	@Before public void setUp(){
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
		e = new Environment();
		e.random.setvalue(0);
	}
	
	@Test public void notifiesSectOfNutrientPresence(){
		e.addNutrient();
		Sect s = new Sect();
		s.center(new Point(10,10));
		e.addSect(s);
		
		e.update();
		
		assertThat(s.center).isNotEqualTo(new Point(10,10));
	}
	
	@Test public void notifiesSectOfNutrientPresenceOnlyOnce(){
		e.addNutrient();
		final int[] count = new int[]{0};
		Sect s = new Sect(){
			protected void onNutrientOnSight(Nutrient n) {
				count[0]++;
			}
		};
		s.center(new Point(10,10));
		e.addSect(s);
		
		for(int i = 0; i < 10; i++){
			e.update();
		}
		
		assertThat(count[0]).isEqualTo(1);
	}

}
