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
	
	//TODO: must notify only when its in Sect's view range
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
			protected void enteredSight(EnvironmentObject n) {
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
	
	@Test public void ifASectStaysMoreThan5turnsOnTopOfANutrientItEatsIt(){
		Nutrient n = e.addNutrient(new Point(10,10));
		final int[] count = new int[]{0};
		Sect s = new Sect(){
			protected void leftSight(EnvironmentObject n) {
				count[0]++;
			}
		};
		s.center(new Point(9,10));
		e.addSect(s);
		
		for(int i = 0; i < 5; i++){
			e.update();
		}
		assertThat(count[0]).isEqualTo(0);
		assertThat(e.nutrients).contains(n);
		
		e.update();
		assertThat(count[0]).isEqualTo(1);
		assertThat(e.nutrients).doesNotContain(n);
		
		e.update();
		assertThat(count[0]).isEqualTo(1);
	}
	
	@Test public void notifiesAllSectsInSightAboutTheNutrientThatHasBeenEaten(){
		Nutrient n = e.addNutrient(new Point(10,10));
		final int[] count = new int[]{0,0};
		e.addSect(new Sect(new Point(9,10)){
			protected void leftSight(EnvironmentObject n) {
				count[0]++;
			}
		});
		e.addSect(new Sect(new Point(20,20)){
			protected void leftSight(EnvironmentObject n) {
				count[1]++;
			}
		});
		
		
		for(int i = 0; i <= 5; i++){
			e.update();
		}
		assertThat(count[0]).isEqualTo(1);
		assertThat(count[1]).isEqualTo(1);
		assertThat(e.nutrients).doesNotContain(n);
	}
	
	
	//TODO: check line of sight
	//TODO: check collision (movement must not allow collisions between sects)
	//TODO: notifications must be on every change (like, a sect on sight moved away)
}
