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
		
		assertThat(s.center()).isNotEqualTo(new Point(10,10));
	}
	
	@Test public void notifiesSectOfNutrientPresenceOnlyOnce(){
		e.addNutrient();
		final int[] count = new int[]{0};
		Sect s = new Sect(){
			protected void enteredSight(Something n) {
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
			protected void leftSight(Something n) {
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
		final int[] count = new int[]{0,0,0};
		e.addSect(new Sect(new Point(9,10), new Herbivore()){
			protected void leftSight(Something n) {
				count[0]++;
			}
		});
		e.addSect(new Sect(new Point(40,40), new Herbivore()){
			protected void leftSight(Something n) {
				count[1]++;
			}
		});
		e.addSect(new Sect(new Point(100,100), new Herbivore()){
			protected void leftSight(Something n) {
				count[2]++;
			}
		});
		
		
		for(int i = 0; i <= 5; i++){
			e.update();
		}
		assertThat(count[0]).isEqualTo(1);
		assertThat(count[1]).isEqualTo(1);
		assertThat(count[2]).isEqualTo(1);
		assertThat(e.nutrients).doesNotContain(n);
	}
	
	@Test public void whenTheresAsectNotifiesOthers(){
		final int[] count = new int[]{0,0};
		Sect s1 = new Sect(){
			protected void enteredSight(Something n) {
				count[0]++;
			}
		};
		s1.center(new Point(9,10));
		Sect s2 = new Sect(){
			protected void enteredSight(Something n) {
				count[1]++;
			}
		};
		s2.center(new Point(90,100));
		e.addSect(s1);
		e.addSect(s2);
		e.addSect(new Sect(new Point(0,0), new Herbivore()));
		
		e.update();
		assertThat(count[0]).isEqualTo(2);
		assertThat(count[1]).isEqualTo(2);
		
		e.update();
		assertThat(count[0]).isEqualTo(2);
		assertThat(count[1]).isEqualTo(2);
	}
	
	@Test public void whenTwoSectsHasHalfChanceOfNotMoving(){
		e.addNutrient(new Point(10,10));
		e.random.setvalue(0.49);
		Sect s1 = e.addSect(new Sect(new Point(10,10), new Herbivore()));
		Sect s2 = e.addSect(new Sect(new Point(10+(s1.radius()),10), new Herbivore()));
		
		e.update();
		
		assertThat(s2.center()).isEqualTo(new Point(10+s1.radius(),10));
	}
	
	@Test public void whenTwoSectsHasHalfChanceOfBackingOf(){
		e.addNutrient(new Point(10,10));
		e.random.setvalue(0.51);
		Sect s1 = e.addSect(new Sect(new Point(10,10), new Herbivore()));
		Sect s2 = e.addSect(new Sect(new Point(10+(s1.radius()),10), new Herbivore()));
		
		e.update();
		
		assertThat(s2.center()).isEqualTo(new Point(10+s1.radius()+1,10));
	}
	
	//TODO: notifies that a sect has left
}
