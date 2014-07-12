package org.unbiquitous.games.uSect.objects;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Environment.Stats;
import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.games.uSect.objects.behaviour.Carnivore;
import org.unbiquitous.games.uSect.objects.behaviour.Herbivore;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.jse.impl.io.Screen;
import org.unbiquitous.uImpala.util.math.Point;
import org.unbiquitous.uos.core.InitialProperties;

public class Sect_Behavior_ReproductionTest {
	// TODO: These variables are making me crazy
	private static final int ATTACK_ENERGY = 30 * 60;
	private static final int INITIAL_ENERGY = (int) (ATTACK_ENERGY * 10);

	private Environment e;

	@Before
	public void setUp() {
		// TODO: too much repetition
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class,
				new Screen());
		e = new Environment(new InitialProperties());
		Random.setvalue(0);
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
		EnvironmentObject male = e.add(new Sect(new Carnivore()), new Stats(
				new Point(20, 20), 3 * INITIAL_ENERGY));
		EnvironmentObject female = e.add(new Sect(new Carnivore()), new Stats(
				new Point(60, 20), 3 * INITIAL_ENERGY));

		Random.setvalue(1);
		executeThisManyTurns(e, 15);

		assertThat(male.position()).isEqualTo(new Point(21, 20));
		assertThat(female.position()).isEqualTo(new Point(59, 20));
	}

	@Test
	public void matingSameSpeciesHasA50percentChance_positive() {
		e.add(new Sect(new Carnivore()), new Stats(new Point(20, 20),
				3 * INITIAL_ENERGY));
		e.add(new Sect(new Carnivore()), new Stats(new Point(60, 20),
				3 * INITIAL_ENERGY));

		Random.setvalue(0.51);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(3);
	}

	@Test
	public void matingSameSpeciesHasA50percentChance_negative() {
		e.add(new Sect(new Carnivore()), new Stats(new Point(20, 20),
				3 * INITIAL_ENERGY));
		e.add(new Sect(new Carnivore()), new Stats(new Point(60, 20),
				3 * INITIAL_ENERGY));

		Random.setvalue(0.49);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(2);
	}

	@Test
	public void matingDifferentSpeciesHasA50percentChance_positive() {
		e.add(new Sect(new Herbivore()), new Stats(new Point(20, 20),
				3 * INITIAL_ENERGY));
		e.add(new Sect(new Carnivore()), new Stats(new Point(60, 20),
				3 * INITIAL_ENERGY));

		Random.setvalue(0.76);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(3);
	}

	@Test
	public void matingDifferentSpeciesHasA50percentChance_negative() {
		e.add(new Sect(new Herbivore()), new Stats(new Point(20, 20),
				3 * INITIAL_ENERGY));
		e.add(new Sect(new Carnivore()), new Stats(new Point(60, 20),
				3 * INITIAL_ENERGY));

		Random.setvalue(0.74);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(2);
	}

	@Test
	public void matingMixFatherMotherAndFatherInformation() {
		e.add(new Sect(new Carnivore()), new Stats(new Point(20, 20),
				3 * INITIAL_ENERGY));
		e.add(new Sect(new Carnivore()), new Stats(new Point(60, 20),
				3 * INITIAL_ENERGY));

		Random.setvalue(0.51);
		executeThisManyTurns(e, 50);

		assertThat(e.sects()).hasSize(3);
		Sect son = e.sects().get(2);
		assertThat(son.position()).isEqualTo(new Point(40, 20));
	}

	// TODO: what if we have multiple fathers?
	// TODO: Can't reproduce alone (somebody else must be part of its mating
	// process)
	// TODO: what if we have multiple birtths at the same time?
	// TODO: After one successful mating needs to wait some time to mate again
	// TODO: Should avoid attacking the son for a while.
	// TODO: son must appear near father and mother and carry their
	// characteristics
	// TODO: probabilities are too high (since they are sorted every turn, maybe
	// run once for each "partner")
	// TODO: variables must parametrized (chances, energy, etc)
	// TODO: CHanges must be queued

}
