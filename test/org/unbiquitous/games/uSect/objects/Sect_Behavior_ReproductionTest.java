package org.unbiquitous.games.uSect.objects;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.objects.behaviour.Carnivore;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;
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
	
	@Test public void ifTwoSectsHaveMoreThanDoubleTheInitialEnergyTheyHaveFifityPercentChanceOfMating(){
		Sect male = e.addSect(new Sect(new Carnivore()),new Point(20,20));
		e.addEnergy(male.id(), 2*INITIAL_ENERGY+50);
		Sect female = e.addSect(new Sect(new Carnivore()),new Point(60,20));
		e.addEnergy(female.id(), 2*INITIAL_ENERGY+50);
		
		e.random.setvalue(1);
		executeThisManyTurns(e, 5);
		
		assertThat(e.energy(male.id())).isEqualTo(3*INITIAL_ENERGY+44);
		assertThat(e.energy(female.id())).isEqualTo(3*INITIAL_ENERGY+44);
		
		executeThisManyTurns(e, 44);
		//TODO: this energy is wrong
		//TODO: this position is wrong
		assertThat(e.energy(male.id())).isEqualTo(3*INITIAL_ENERGY);
		assertThat(e.energy(female.id())).isEqualTo(3*INITIAL_ENERGY);
		
		executeThisManyTurns(e, 1);
		
		assertThat(e.sects()).hasSize(3);
	}
	
	@Test public void whileMatingCantWalk(){
		Sect male = e.addSect(new Sect(new Carnivore()),new Point(20,20));
		e.addEnergy(male.id(), 2*INITIAL_ENERGY);
		Sect female = e.addSect(new Sect(new Carnivore()),new Point(60,20));
		e.addEnergy(female.id(), 2*INITIAL_ENERGY);
		
		e.random.setvalue(1);
		executeThisManyTurns(e, 15);
		
		assertThat(male.center()).isEqualTo(new Point(21,20));
		assertThat(female.center()).isEqualTo(new Point(59,20));
	}
	
	//TODO: Mating consumes the equivalent of an attack
	//TODO: Mating must be an agreement of both parties
	//TODO: After one successful mating needs to wait some time to mate again
	//TODO: son must appear near father and mother and carry their characteristics
}

