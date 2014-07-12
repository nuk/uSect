package org.unbiquitous.games.uSect.objects;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Environment.Stats;
import org.unbiquitous.games.uSect.objects.behaviour.Carnivore;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;
import org.unbiquitous.uImpala.util.math.Point;
import org.unbiquitous.uos.core.InitialProperties;

public class Sect_Behavior_ReproductionTest {
	//TODO: These variables are making me crazy
	private static final int ATTACK_ENERGY = 30*60;
	private static final int INITIAL_ENERGY = (int) (ATTACK_ENERGY * 10);
	
	private Environment e;
	
	@Before public void setUp(){
		//TODO: too much repetition
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
		e = new Environment(new InitialProperties());
		e.random.setvalue(0);
		e.disableNutrientsCreation();
	}
	
	@Test public void matingTakes50turns(){
		Sect male = e.addSect(new Sect(new Carnivore()),new Point(20,20));
		e.changeStats(male, Stats.n().energy(2*INITIAL_ENERGY+50));
		Sect female = e.addSect(new Sect(new Carnivore()),new Point(60,20));
		e.changeStats(female, Stats.n().energy(2*INITIAL_ENERGY+50));
		
		e.random.setvalue(1);
		executeThisManyTurns(e, 5);
		
		assertThat(e.energy(male.id())).isEqualTo(3*INITIAL_ENERGY+44);
		assertThat(e.energy(female.id())).isEqualTo(3*INITIAL_ENERGY+44);
		
		executeThisManyTurns(e, 44);
		assertThat(e.energy(male.id())).isEqualTo(3*INITIAL_ENERGY);
		assertThat(e.energy(female.id())).isEqualTo(3*INITIAL_ENERGY);
		
		
		assertThat(e.sects()).hasSize(2);
		executeThisManyTurns(e, 1);
		
		assertThat(e.sects()).hasSize(3);
	}
	
	@Test public void matingConsumesTheEquivalentOfAnAttack(){
		Sect male = e.addSect(new Sect(new Carnivore()),new Point(20,20));
		e.changeStats(male, Stats.n().energy(2*INITIAL_ENERGY+51));
		Sect female = e.addSect(new Sect(new Carnivore()),new Point(60,20));
		e.changeStats(female, Stats.n().energy(2*INITIAL_ENERGY+51));
		
		e.random.setvalue(1);
		executeThisManyTurns(e, 50);
		
		assertThat(e.energy(male.id())).isEqualTo(3*INITIAL_ENERGY-ATTACK_ENERGY);
		assertThat(e.energy(female.id())).isEqualTo(3*INITIAL_ENERGY-ATTACK_ENERGY);
		
		assertThat(e.sects()).hasSize(3);
	}
	
	@Test public void whileMatingCantWalk(){
		Sect male = e.addSect(new Sect(new Carnivore()),new Point(20,20));
		e.changeStats(male, Stats.n().energy(2*INITIAL_ENERGY));
		Sect female = e.addSect(new Sect(new Carnivore()),new Point(60,20));
		e.changeStats(female, Stats.n().energy(2*INITIAL_ENERGY));
		
		e.random.setvalue(1);
		executeThisManyTurns(e, 15);
		
		assertThat(male.position()).isEqualTo(new Point(21,20));
		assertThat(female.position()).isEqualTo(new Point(59,20));
	}
	
	//TODO: Mating must be an agreement of both parties (50% * 50% for same species) 
	//TODO: After one successful mating needs to wait some time to mate again
	//TODO: son must appear near father and mother and carry their characteristics
}

