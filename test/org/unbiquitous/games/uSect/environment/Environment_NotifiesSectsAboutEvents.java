package org.unbiquitous.games.uSect.environment;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.Nutrient;
import org.unbiquitous.games.uSect.Sect;
import org.unbiquitous.games.uSect.Something;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;
import org.unbiquitous.uos.core.InitialProperties;

public class Environment_NotifiesSectsAboutEvents {

	private Environment e;

	@Before public void setUp(){
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
		e = new Environment(new InitialProperties());
		e.random.setvalue(0);
	}
	
	@Test public void notifiesSectOfNutrientPresence(){
		e.addNutrient();
		Sect s = e.addSect(new Sect(),new Point(10,10));
		
		e.update();
		
		assertThat(s.center()).isNotEqualTo(new Point(10,10));
	}
	
	@Test public void notifiesSectOfNutrientPresenceOnlyOnce(){
		e.addNutrient();
		final int[] count = new int[]{0};
		e.addSect(new Sect(){
			public void enteredSight(Something n) {
				count[0]++;
			}
		},new Point(10,10));
		
		executeThisManyTurns(e, 10);
		assertThat(count[0]).isEqualTo(1);
	}
	
	@Test public void ifASectStaysMoreThan5turnsOnTopOfANutrientItEatsIt(){
		Nutrient n = e.addNutrient(new Point(10,10));
		final int[] count = new int[]{0};
		e.addSect(new Sect(){
			public void leftSight(Something n) {
				count[0]++;
			}
		},new Point(9,10));
		
		executeThisManyTurns(e, 5);
		assertThat(count[0]).isEqualTo(0);
		assertThat(e.nutrients()).contains(n);
		
		e.update();
		assertThat(count[0]).isEqualTo(1);
		assertThat(e.nutrients()).doesNotContain(n);
		
		e.update();
		assertThat(count[0]).isEqualTo(1);
	}
	
	@Test public void notifiesAllSectsInSightAboutTheNutrientThatHasBeenEaten(){
		Nutrient n = e.addNutrient(new Point(10,10));
		final int[] count = new int[]{0,0,0};
		e.addSect(new Sect(){
			public void leftSight(Something n) {
				count[0]++;
			}
		},new Point(9,10));
		e.addSect(new Sect(){
			public void leftSight(Something n) {
				count[1]++;
			}
		},new Point(40,40));
		e.addSect(new Sect(){
			public void leftSight(Something n) {
				count[2]++;
			}
		},new Point(100,100));
		
		
		executeThisManyTurns(e, 6);
		assertThat(count[0]).isEqualTo(1);
		assertThat(count[1]).isEqualTo(1);
		assertThat(count[2]).isEqualTo(1);
		assertThat(e.nutrients()).doesNotContain(n);
	}
	
	@Test public void whenTheresAsectNotifiesOthers(){
		final int[] count = new int[]{0,0};
		e.addSect(new Sect(){
			public void enteredSight(Something n) {
				count[0]++;
			}
		},new Point(9,10));
		e.addSect(new Sect(){
			public void enteredSight(Something n) {
				count[1]++;
			}
		},new Point(90,100));
		e.addSect(new Sect(),new Point(0,0));
		
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
		Sect s1 = e.addSect(new Sect(),new Point(10,10));
		Sect s2 = e.addSect(new Sect(),new Point(10+(s1.radius()),10));
		
		e.update();
		
		assertThat(s2.center()).isEqualTo(new Point(10+s1.radius(),10));
	}
	
	@Test public void whenTwoSectsHasHalfChanceOfBackingOf(){
		e.addNutrient(new Point(10,10));
		e.random.setvalue(0.51);
		Sect s1 = e.addSect(new Sect(),new Point(10,10));
		Sect s2 = e.addSect(new Sect(),new Point(10+(s1.radius()),10));
		
		e.update();
		
		assertThat(s2.center()).isEqualTo(new Point(10+s1.radius()+1,10));
	}
	
	//TODO: notifies that a sect has left
}
