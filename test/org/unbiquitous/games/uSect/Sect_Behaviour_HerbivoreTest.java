package org.unbiquitous.games.uSect;

import static org.fest.assertions.api.Assertions.assertThat;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;

public class Sect_Behaviour_HerbivoreTest {

	private Environment e;

	@Before public void setUp(){
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
		e = new Environment();
		e.random.setvalue(0);
	}
	
	@Test public void goesAfterANutrientAfterEachStep(){
		Nutrient nutrient = e.addNutrient(new Point(20,20));
		
		Sect s = e.addSect(new Sect(new Point(21,21)));
		e.update();
		e.update(); 
		
		assertThat(s.center.x).isEqualTo(nutrient.center.x);
		assertThat(s.center.y).isEqualTo(nutrient.center.y);
	}
	
	@Test public void goesMovementIsAlwaysOnePixelAtAtimeRandomly(){
		e.addNutrient(new Point(20,20));
		
		Sect s = e.addSect(new Sect(new Point(10, 10)));
		
		s.random.setvalue(0.51);
		e.update(); 
		
		assertThat(s.center).isEqualTo(new Point(11,10));

		s.random.setvalue(0.49);
		e.update(); 
		
		assertThat(s.center).isEqualTo(new Point(11,11));
	}
	
	@Test public void ifTheresOnlyOneDirectionGoesThatWay(){
		e.addNutrient(new Point(20,20));
		
		Sect s1 = e.addSect(new Sect(new Point(20, 10)));
		s1.random.setvalue(0.51);
		e.update(); 
		assertThat(s1.center).isEqualTo(new Point(20,11));

		Sect s2 = e.addSect(new Sect(new Point(10, 20)));
		s2.random.setvalue(0.49);
		e.update(); 
		assertThat(s2.center).isEqualTo(new Point(11,20));
	}
	
	@Test public void stopsWalkingAfterEatingNutrient(){
		Nutrient nutrient = e.addNutrient(new Point(20,20));
		
		Sect s = e.addSect(new Sect(new Point(30,15)));
		for (int i = 0; i < 30; i++){
			e.update();
		}
		
		assertThat(s.center.x).isEqualTo(nutrient.center.x);
		assertThat(s.center.y).isEqualTo(nutrient.center.y);
	}
	
	@Test public void goesAfterTheNearestNutrient(){
		e.addNutrient(new Point(20,20));
		e.addNutrient(new Point(40,40));
		Nutrient n1 = e.addNutrient(new Point(5,5));
		Sect s =  e.addSect(new Sect(new Point(10,10)));
		
		for (int i = 0; i < 10; i++){
			e.update();
		}
		
		assertThat(s.center).isEqualTo(n1.center);
	}
	
	@Test public void afterAnutrientIsGoneMustChaseTheClosesInSight(){
		Nutrient n1 = e.addNutrient(new Point(20,20));
		e.addNutrient(new Point(40,40));
		e.addNutrient(new Point(5,5));
		Sect s =  e.addSect(new Sect(new Point(10,10)));
		
		for (int i = 0; i < 10+4+30; i++){
			e.update();
		}
		
		assertThat(s.center).isEqualTo(n1.center);
	}
	
}
