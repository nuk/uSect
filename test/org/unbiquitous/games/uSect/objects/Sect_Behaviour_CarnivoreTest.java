package org.unbiquitous.games.uSect.objects;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.objects.behaviour.Carnivore;
import org.unbiquitous.games.uSect.objects.behaviour.Herbivore;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;
import org.unbiquitous.uos.core.InitialProperties;

public class Sect_Behaviour_CarnivoreTest {
	private static final int INITIAL_ENERGY = (int) (30*60*10);
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
	
	@Test public void preferCorpsesThanLiveSects(){
		e.addCorpse(new Point(100,10));
		e.random.setvalue(-1);
		e.addSect(new Sect(new Herbivore()),new Point(0,10));
		Sect c = e.addSect(new Sect(new Carnivore()),new Point(50,10));
		
		executeThisManyTurns(e, 1);
		
		assertThat(c.center()).isEqualTo(new Point(51,10));
	}
	
	@Test public void sectGainsEnergyFromCorpse(){
		Sect s = e.addSect(new Sect(), new Point(10,10));
		e.addCorpse(new Point(10,10));
		
		int NUTRIENT_INCREMENT = 5*30*60;
		executeThisManyTurns(e, INITIAL_ENERGY+NUTRIENT_INCREMENT);
		assertThat(e.sects()).containsOnly(s);
		
		e.update();
		assertThat(e.sects()).isEmpty();
	}
}
