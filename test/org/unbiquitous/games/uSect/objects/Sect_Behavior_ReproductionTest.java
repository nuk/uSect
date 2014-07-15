package org.unbiquitous.games.uSect.objects;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;
import static org.unbiquitous.games.uSect.TestUtils.setUpEnvironment;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Environment.Stats;
import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.games.uSect.objects.Sect.Behavior;
import org.unbiquitous.games.uSect.objects.behavior.Carnivore;
import org.unbiquitous.games.uSect.objects.behavior.Herbivore;
import org.unbiquitous.uImpala.util.math.Point;

public class Sect_Behavior_ReproductionTest {
	// TODO: These variables are making me crazy
	private static final int ATTACK_ENERGY = 30 * 60;
	private static final int INITIAL_ENERGY = (int) (ATTACK_ENERGY * 10);

	private Environment e;

	@Before
	public void setUp() {
		e = setUpEnvironment();
		e.disableNutrientsCreation();
	}

	@Test
	public void matingTakes50turns() {
		EnvironmentObject male = e.add(new Sect(new Carnivore()), new Stats(
				new Point(20, 20), 2 * INITIAL_ENERGY+1 ));
		EnvironmentObject female = e.add(new Sect(new Carnivore()), new Stats(
				new Point(60, 20), 2 * INITIAL_ENERGY+1 ));

		Random.setvalue(1);
		executeThisManyTurns(e, 5);

		assertThat(e.stats(male.id()).energy).isEqualTo(2 * INITIAL_ENERGY - 5);
		assertThat(e.stats(female.id()).energy).isEqualTo(2 * INITIAL_ENERGY - 5);

		executeThisManyTurns(e, 44);
		assertThat(e.stats(male.id()).energy).isEqualTo(2 * INITIAL_ENERGY - 49);
		assertThat(e.stats(female.id()).energy).isEqualTo(2 * INITIAL_ENERGY - 49);

		assertThat(e.sects()).hasSize(2);
		executeThisManyTurns(e, 1);

		assertThat(e.sects()).hasSize(3);
	}

	@Test
	public void herbivoresAlsoMate() {
		e.add(new Sect(new Herbivore()), new Stats(new Point(20, 20),
				2 * INITIAL_ENERGY + 50));
		e.add(new Sect(new Herbivore()), new Stats(new Point(60, 20),
				2 * INITIAL_ENERGY + 50));

		Random.setvalue(1);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(3);
	}

	private Sect addMatingSect(Behavior behavior, Point position, Long energy) {
		Stats initialStats = new Stats(position, energy);
		return (Sect) e.add(new Sect(behavior), initialStats);
	}
	
	private Sect addMatingSect(Behavior behavior, Point position) {
		return addMatingSect(behavior, position, 3l * INITIAL_ENERGY);
	}
	
	@Test
	public void matingConsumesTheEquivalentOfAnAttack() {
		EnvironmentObject male = e.add(new Sect(new Carnivore()), new Stats(
				new Point(20, 20), 2 * INITIAL_ENERGY + 51));
		EnvironmentObject female = e.add(new Sect(new Carnivore()), new Stats(
				new Point(60, 20), 2 * INITIAL_ENERGY + 51));

		Random.setvalue(1);
		executeThisManyTurns(e, 50);

		assertThat(e.stats(male.id()).energy).isEqualTo(
				2 * INITIAL_ENERGY - ATTACK_ENERGY);
		assertThat(e.stats(female.id()).energy).isEqualTo(
				2 * INITIAL_ENERGY - ATTACK_ENERGY);

		assertThat(e.sects()).hasSize(3);
	}

	@Test
	public void whileMatingCantWalk() {
		Sect male = addMatingSect(new Carnivore(), new Point(20, 20));
		Sect female = addMatingSect(new Carnivore(), new Point(60, 20));

		Random.setvalue(1);
		executeThisManyTurns(e, 15);

		assertThat(male.position()).isEqualTo(new Point(21, 20));
		assertThat(female.position()).isEqualTo(new Point(59, 20));
	}

	@Test
	public void matingSameSpeciesHasA50percentChance_positive() {
		addMatingSect(new Carnivore(), new Point(20, 20));
		addMatingSect(new Carnivore(), new Point(60, 20));

		Random.setvalue(0.51);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(3);
	}

	@Test
	public void matingSameSpeciesHasA50percentChance_negative() {
		addMatingSect(new Carnivore(), new Point(20, 20));
		addMatingSect(new Carnivore(), new Point(60, 20));

		Random.setvalue(0.49);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(2);
	}

	@Test
	public void matingDifferentSpeciesHasA50percentChance_positive() {
		addMatingSect(new Carnivore(), new Point(20, 20));
		addMatingSect(new Carnivore(), new Point(60, 20));

		Random.setvalue(0.76);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(3);
	}

	@Test
	public void matingDifferentSpeciesHasA50percentChance_negative() {
		addMatingSect(new Herbivore(), new Point(20, 20));
		addMatingSect(new Carnivore(), new Point(60, 20));

		Random.setvalue(0.74);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(2);
	}

	@Test
	public void matingMixFatherMotherAndFatherInformation() {
		addMatingSect(new Carnivore(), new Point(20, 20));
		addMatingSect(new Carnivore(), new Point(60, 20));

		Random.setvalue(0.51);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(3);
		Sect son = e.sects().get(2);
		assertThat(son.position()).isEqualTo(new Point(40, 20));
	}
	
	@Test
	public void multipleSimultaneousMatingsConsiderOnlyParentsInRangeOfMatingCall() {
		Sect s1 = addMatingSect(new Carnivore(), new Point(20, 20));
		Sect s2 = addMatingSect(new Carnivore(), new Point(60, 20));
		Sect s3 = addMatingSect(new Carnivore(), new Point(20, 60));

		Random.setvalue(1);
		executeThisManyTurns(e, 50);

		List<Sect> sons = e.sects();
		sons.removeAll(Arrays.asList(s1,s2,s3));
		assertThat(sons).hasSize(2);
		assertThat(Arrays.asList(sons.get(0).position(),sons.get(1).position()))
			.containsOnly(new Point(40, 20),new Point(21, 40));
	}
	
	@Test
	public void multipleSimultaneousMatingsConsiderOnlyParentsInRangeOrEachOther() {
		Sect s1 = addMatingSect(new Carnivore(), new Point(20, 20));
		Sect s2 = addMatingSect(new Carnivore(), new Point(60, 20));
		Sect s3 = addMatingSect(new Carnivore(), new Point(200, 220));
		Sect s4 = addMatingSect(new Carnivore(), new Point(200, 270));

		Random.setvalue(1);
		executeThisManyTurns(e, 50);

		List<Sect> sons = e.sects();
		sons.removeAll(Arrays.asList(s1,s2,s3,s4));
		assertThat(sons).hasSize(2);
		assertThat(Arrays.asList(sons.get(0).position(),sons.get(1).position()))
			.containsOnly(new Point(40, 20),new Point(200, 245));
	}
	
	@Test
	public void matingProcessDontStartIfTheresNoMatch() {
		Sect male = addMatingSect(new Carnivore(), new Point(20, 20));
		Sect female = addMatingSect(new Carnivore(){
			public void update() {
				//Do nothing
			}
		}, new Point(70, 20));

		Random.setvalue(1);
		executeThisManyTurns(e, 50);

		assertThat(male.position().distanceTo(new Point(20,20))).isGreaterThan(10);
		
		List<Sect> sons = e.sects();
		sons.removeAll(Arrays.asList(female,male));
		assertThat(sons).isEmpty();
	}
	
	@Test
	public void carnivoresAfterMatingWaits10TimesTheMatingTimeToMateAgain() {
		Sect male = addMatingSect(new Carnivore(), new Point(20, 20),Long.MAX_VALUE);
		Sect female = addMatingSect(new Carnivore(), new Point(60, 20),Long.MAX_VALUE);
		
		Random.setvalue(1);
		executeThisManyTurns(e, 10*50);
		
		assertThat(male.position()).isNotEqualTo(new Point(20,20));
		assertThat(female.position()).isNotEqualTo(new Point(60,20));
		assertThat(e.sects()).hasSize(2);
	}
	
	@Test
	public void herbivoresAfterMatingWaits20TimesTheMatingTimeToMateAgain() {
		e.addNutrient(new Point(400,400));
		Sect male = addMatingSect(new Herbivore(), new Point(20, 20),Long.MAX_VALUE);
		Sect female = addMatingSect(new Herbivore(), new Point(60, 20),Long.MAX_VALUE);
		
		Random.setvalue(1);
		executeThisManyTurns(e, 20*50);
		
		assertThat(male.position()).isNotEqualTo(new Point(20,20));
		assertThat(female.position()).isNotEqualTo(new Point(60,20));
		assertThat(e.sects()).hasSize(3);
	}

	// TODO: Should avoid attacking the son for a while.
	// TODO: son must appear near father and mother and carry their
	// characteristics
	// TODO: probabilities are too high (since they are sorted every turn, maybe
	// run once for each "partner")
	// TODO: variables must parametrized (chances, energy, etc)
	// TODO: CHanges must be queued as events

}
