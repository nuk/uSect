package org.unbiquitous.games.uSect;

import static org.fest.assertions.api.Assertions.assertThat;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;
import org.unbiquitous.uos.core.InitialProperties;

public class Sect_Behaviour_HerbivoreTest {

	private Environment e;

	@Before public void setUp(){
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
		e = new Environment(new InitialProperties());
		e.random.setvalue(0);
	}
	
	@Test public void goesAfterANutrientAfterEachStep(){
		Nutrient nutrient = e.addNutrient(new Point(20,20));
		
		Sect s = e.addSect(new Sect(),new Point(21,21));
		e.update();
		e.update(); 
		
		assertThat(s.center().x).isEqualTo(nutrient.center().x);
		assertThat(s.center().y).isEqualTo(nutrient.center().y);
	}
	
	@Test public void goesMovementIsAlwaysOnePixelAtAtimeRandomly(){
		e.addNutrient(new Point(20,20));
		
		Sect s = e.addSect(new Sect(),new Point(10, 10));
		
		e.random.setvalue(0.51);
		e.update(); 
		
		assertThat(s.center()).isEqualTo(new Point(11,10));

		e.random.setvalue(0.49);
		e.update(); 
		
		assertThat(s.center()).isEqualTo(new Point(11,11));
	}
	
	@Test public void ifTheresOnlyOneDirectionGoesThatWay(){
		e.addNutrient(new Point(40,40));
		
		Sect s1 = e.addSect(new Sect(),new Point(40, 10));
		e.random.setvalue(0.51);
		e.update(); 
		assertThat(s1.center()).isEqualTo(new Point(40,11));

		Sect s2 = e.addSect(new Sect(),new Point(10, 40));
		e.random.setvalue(0.49);
		e.update(); 
		assertThat(s2.center()).isEqualTo(new Point(11,40));
	}
	
	@Test public void stopsWalkingAfterEatingNutrient(){
		Nutrient nutrient = e.addNutrient(new Point(20,20));
		
		Sect s = e.addSect(new Sect(),new Point(30,15));
		executeThisManyTurns(30);
		
		assertThat(s.center().x).isEqualTo(nutrient.center().x);
		assertThat(s.center().y).isEqualTo(nutrient.center().y);
	}
	
	@Test public void goesAfterTheNearestNutrient(){
		e.addNutrient(new Point(20,20));
		e.addNutrient(new Point(40,40));
		Nutrient n1 = e.addNutrient(new Point(5,5));
		Sect s =  e.addSect(new Sect(),new Point(10,10));
		
		executeThisManyTurns(10);
		
		assertThat(s.center()).isEqualTo(n1.center());
	}

	@Test public void dontChaseDeadNutrients(){
		e.addNutrient(new Point(5,5));
		Sect s =  e.addSect(new Sect(),new Point(10,10));
		executeThisManyTurns(14);
		Nutrient n2 = e.addNutrient(new Point(20,20));
		
		executeThisManyTurns(30);
		
		assertThat(s.center()).isEqualTo(n2.center());
	}
	
	@Test public void afterAnutrientIsGoneMustChaseTheClosesInSight(){
		e.addNutrient(new Point(100,100));
		e.addNutrient(new Point(20,20));
		Nutrient n1 = e.addNutrient(new Point(40,40));
		e.addNutrient(new Point(5,5));
		Sect s =  e.addSect(new Sect(),new Point(10,10));
		
		executeThisManyTurns(10+4+30+4+40);
		
		assertThat(s.center()).isEqualTo(n1.center());
	}
	
	@Test public void doesNotBotherByNutrientsEatenThatAreNotTheTarget(){
		Nutrient n1 = e.addNutrient(new Point(40,40));
		e.addNutrient(new Point(5,5));
		Sect s1 =  e.addSect(new Sect(),new Point(10,10));
		Sect s2 =  e.addSect(new Sect(),new Point(50,50));
		
		executeThisManyTurns(100);
		
		assertThat(s1.center()).isNotEqualTo(n1.center());
		assertThat(s2.center()).isEqualTo(n1.center());
	}

	@Test public void sectDiesAfter10minutesStandingStill(){
		Sect s = e.addSect(new Sect(), new Point(10,10));
		e.update();
		assertThat(e.sects()).containsOnly(s);
		
		e.random.setvalue(1);
		executeThisManyTurns((int) (30*60*10*0.05)-2);
		assertThat(e.sects()).containsOnly(s);
		
		e.update();
		assertThat(e.sects()).isEmpty();
	}
	
	private void executeThisManyTurns(int numberOfTurns) {
		for (int i = 0; i < numberOfTurns; i++){
			e.update();
		}
	}

	
}
