package org.unbiquitous.games.uSect;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;
import org.unbiquitous.uos.core.InitialProperties;

public class Sect_Behaviour_CarnivoreTest {
	private Environment e;

	@Before public void setUp(){
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
		e = new Environment(new InitialProperties());
		e.random.setvalue(0);
	}
	
	@Test public void goesAfterAHerbivoreAfterEachStep(){
		Sect h = e.addSect(new Sect(new Herbivore()),new Point(20,20));
		Sect c = e.addSect(new Sect(new Carnivore()),new Point(51,20));
		
		e.update();
		
		assertThat(h.center()).isEqualTo(new Point(20,20));
		assertThat(c.center()).isEqualTo(new Point(50,20));
	}
	
	@Test public void goesAfterAHerbivoreAfterEachStepEvenWhenHesMoving(){
		e.addNutrient(new Point(50,20));
		e.random.setvalue(-1);
		Sect h = e.addSect(new Sect(new Herbivore()),new Point(10,20));
		Sect c = e.addSect(new Sect(new Carnivore()),new Point(20,60));
		
		executeThisManyTurns(e, 40);
		
		assertThat(h.center()).isEqualTo(new Point(50,20));
		assertThat(c.center()).isNotEqualTo(new Point(10,20));
		assertThat(c.center().x).isGreaterThan(20);
	}
}
