package org.unbiquitous.games.uSect.objects;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;
import static org.unbiquitous.games.uSect.TestUtils.setUpEnvironment;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.uImpala.util.math.Point;

public class PlayerTest {
	private static final int PlayerInfluenceRadius = 300;
	private static final int PlayerInfluenceSpeed = 5;

	private Environment e;

	@Before
	public void setUp() {
		e = setUpEnvironment();
	}

	@Test
	public void playerDoesNothingWhileNotAskedForIt() {
		Player p = e.addPlayer(new Player(), new Point(600, 0));
		Sect s = e.addSect(new Sect() {
			public void update() {
				e.moveTo(this, new Point(0, +1));
			}
		}, new Point(600, 100));

		executeThisManyTurns(e, 60);
		assertThat(s.position()).isEqualTo(new Point(600, 100 + 60));
	}

	@Test
	public void playerAttackStopsSectWhenInsideRange() {
		Player p = e.addPlayer(new Player(), new Point(600, 0));
		Sect s = e.addSect(new Sect() {
			public void update() {
				e.moveTo(this, new Point(0, +1));
			}
		}, new Point(600, 100));

		p.call();

		executeThisManyTurns(e, 60);
		// 20 turns to reach 100 + 4 turns to reach the other 20
		assertThat(s.position()).isEqualTo(new Point(600, 100 + 20 + 4));
	}

	@Test
	public void playerAttackbringsSectCloserToPlayerAsRangeShrinks() {
		Player p = e.addPlayer(new Player(), new Point(600, 0));
		Sect s = e.addSect(new Sect() {
			public void update() {
				e.moveTo(this, new Point(0, +1));
			}
		}, new Point(600, 100));

		p.call();

		executeThisManyTurns(e, 2 * 60);
		assertThat(s.position()).isEqualTo(new Point(600, 98));
	}

	@Test
	public void ifASectIsNotCapturedItCanMoveAround() {
		Player p = e.addPlayer(new Player(), new Point(600, 0));
		Sect s = e.addSect(new Sect() {
			public void update() {
				e.moveTo(this, new Point(0, +1));
			}
		}, new Point(600, 100));

		p.call();

		executeThisManyTurns(e, 2 * 60 + 10);
		assertThat(s.position()).isEqualTo(new Point(600, 108));
	}

}
