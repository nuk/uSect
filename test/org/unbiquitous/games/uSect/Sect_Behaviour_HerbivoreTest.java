package org.unbiquitous.games.uSect;

import static org.fest.assertions.api.Assertions.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;

public class Sect_Behaviour_HerbivoreTest {

	@Before public void setUp(){
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
	}
	
	@Test public void goesAfterANutrientAfterEachStep(){
		Environment e = new Environment(new DeviceStats());
		Nutrient nutrient = createNutrient(e);
		
		Sect s = addSectAt(e, nutrient, new Point(-10,+5));
		e.update(); 
		
		assertThat(s.center.x).isEqualTo(nutrient.center.x-9);
		assertThat(s.center.y).isEqualTo(nutrient.center.y+4);
	}
	
	@Test public void stopsWalkingAfterEatingNutrient(){
		Environment e = new Environment(new DeviceStats());
		Nutrient nutrient = createNutrient(e);
		
		Sect s = addSectAt(e, nutrient, new Point(+10,-5));
		for (int i = 0; i < 15; i++){
			e.update();
		}
		
		assertThat(s.center.x).isEqualTo(nutrient.center.x);
		assertThat(s.center.y).isEqualTo(nutrient.center.y);
	}

	private Sect addSectAt(Environment e, Nutrient nutrient, Point shift) {
		Sect s = new Sect();
		
		Point pos = (Point)nutrient.center.clone();
		pos.x += shift.x;
		pos.y += shift.y;
		s.center(pos);
		s.onNutrientOnSight(nutrient);
		
		e.addSect(s);
		return s;
	}

	private Nutrient createNutrient(Environment e) {
		e.random.setvalue(1);
		e.update();
		e.random.setvalue(0);
		Nutrient nutrient = e.nutrients.get(0);
		return nutrient;
	}
	
}
