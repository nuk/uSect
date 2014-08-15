package org.unbiquitous.games.uSect.environment;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;
import static org.unbiquitous.games.uSect.TestUtils.setUpEnvironment;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.objects.Nutrient;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.games.uSect.objects.behavior.Carnivore;
import org.unbiquitous.uImpala.util.math.Point;

public class Environment_EventsControlTest {

	private Environment e;

	@Before public void setUp(){
		e = setUpEnvironment();
	}
	
	@Test public void notifiesSectOfNutrientPresence(){
		e.addNutrient();
		Sect s = e.addSect(new Sect(),new Point(10,10));
		
		e.update();
		
		assertThat(s.position()).isNotEqualTo(new Point(10,10));
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
				if(n.id() != this.id())	count[0]++;
			}
		},new Point(9,10));
		e.addSect(new Sect(){
			public void enteredSight(Something n) {
				if(n.id() != this.id())	count[1]++;
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
	
	@Test public void whenAsectDiesNotifiesOthers(){
		final int[] count = new int[]{0,0};
		final Sect h = e.addSect(new Sect(){
			public void leftSight(Something n) {
				count[0]++;
			}
		},new Point(10,10));
		e.addSect(new Sect(new Carnivore()){
			public void leftSight(Something n) {
				if(n.id() == h.id()){
					count[1]++;
				}
			}
		},new Point(60,10));
		executeThisManyTurns(e, 100);
		assertThat(count[0]).isEqualTo(0);
		assertThat(count[1]).isEqualTo(1);
	}
	
	@Test public void whenTwoSectsHasHalfChanceOfNotMoving(){
		e.addNutrient(new Point(10,10));
		Random.setvalue(0.49);
		Sect s1 = e.addSect(new Sect(),new Point(10,10));
		Sect s2 = e.addSect(new Sect(),new Point(10+(s1.radius()),10));
		
		e.update();
		
		assertThat(s2.position()).isEqualTo(new Point(10+s1.radius(),10));
	}
	
	@Test public void whenTwoSectsHasHalfChanceOfBackingOf(){
		e.addNutrient(new Point(10,10));
		Random.setvalue(0.51);
		Sect s1 = e.addSect(new Sect(),new Point(10,10));
		Sect s2 = e.addSect(new Sect(),new Point(10+(s1.radius()),10));
		
		e.update();
		
		assertThat(s2.position()).isEqualTo(new Point(10+s1.radius()+1,10));
	}
	
}
