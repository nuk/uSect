package org.unbiquitous.games.uSect;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.jse.impl.io.Screen;

public class Environment_StatsTest {

	Environment e;
	
	@Before public void setUp(){
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
	}
	
	@Test public void environmentIsAGameObject(){
		assertThat(new Environment(null)).isInstanceOf(GameObject.class);
	}
	
	@Test public void minimunChanceOfApearingNutrientsIs10percent(){
		e = new Environment(createStastWithMemory(0));
		testNutrientsProbability(0.01);
		
		e = new Environment(createStastWithMemory(512));
		testNutrientsProbability(0.01);
	}

	@Test public void maximunChanceOfApearingNutrientsIs50percent(){
		e = new Environment(createStastWithMemory(Integer.MAX_VALUE));
		testNutrientsProbability(0.05);
		
		e = new Environment(createStastWithMemory(16*1024));
		testNutrientsProbability(0.05);
	}
	
	@Test public void chanceOfApearingNutrientsIsProportionalToMemory(){
		e = new Environment(createStastWithMemory(1024));
		testNutrientsProbability(0.0125);
		e = new Environment(createStastWithMemory(2*1024));
		testNutrientsProbability(0.015);
	}
	
	
	private void testNutrientsProbability(double chances) {
		double value = 1-chances;
		e.random.setvalue(value-0.0001);
		e.update();
		assertThat(e.nutrients()).isEmpty();;
		e.random.setvalue(value);
		e.update();
		assertThat(e.nutrients()).hasSize(1);
	}
	
	private DeviceStats createStastWithMemory(final int memoryInMB) {
		return new DeviceStats(){
			public long totalMemory(){
				return memoryInMB;
			}
		};
	}
	
}
