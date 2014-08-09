package org.unbiquitous.games.uSect.objects;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.uImpala.util.math.Point;

public class PlayerTest {
	private Environment e;

	@Before
	public void setUp() {
		e = setUpEnvironment();
	}

	@Test
	public void playerDoesNothingWhileNotAskedForIt() {
		 e.addPlayer(new Player(), new Point(600, 0));
		Sect s = e.addSect(movingSect(e,new Point(0, +1)), new Point(600, 100));

		executeThisManyTurns(e, 60);
		assertThat(s.position()).isEqualTo(new Point(600, 100 + 60));
	}

	@Test
	public void playerAttackStopsSectWhenInsideRange() {
		Player p = e.addPlayer(new Player(), new Point(600, 0));
		Sect s = e.addSect(movingSect(e,new Point(0, +1)), new Point(600, 100));

		p.call();

		executeThisManyTurns(e, 60);
		// 20 turns to reach 100 + 4 turns to reach the other 20
		assertThat(s.position()).isEqualTo(new Point(600, 100 + 20 + 4));
	}
	
	@Test
	public void playerInfluenceIsOverAllSectsinRange() {
		Player p = e.addPlayer(new Player(), new Point(600, 0));
		Sect s1 = e.addSect(movingSect(e,new Point(0, +1)), new Point(600, 100));
		Sect s2= e.addSect(movingSect(e,new Point(0, +1)), new Point(550, 50));
		Sect s3= e.addSect(movingSect(e,new Point(0, +1)), new Point(650, 50));

		p.call();

		executeThisManyTurns(e, 60);
		// 20 turns to reach 100 + 4 turns to reach the other 20
		assertThat(s1.position()).isEqualTo(new Point(600, 100 + 20 + 4));
		assertThat(s2.position()).isEqualTo(new Point(550, 50 + 20 + 4));
		assertThat(s3.position()).isEqualTo(new Point(650, 50 + 20 + 4));
	}

	@Test
	public void playerAttackbringsSectCloserToPlayerAsRangeShrinks() {
		Player p = e.addPlayer(new Player(), new Point(600, 0));
		Sect s = e.addSect(movingSect(e,new Point(0, +1)), new Point(600, 100));

		p.call();

		executeThisManyTurns(e, 2 * 60);
		assertThat(s.position()).isEqualTo(new Point(600, 98));
	}

	@Test
	public void ifASectIsNotCapturedItCanMoveAround() {
		Player p = e.addPlayer(new Player(), new Point(600, 0));
		Sect s = e.addSect(movingSect(e,new Point(0, +1)), new Point(600, 100));

		p.call();

		executeThisManyTurns(e, 2 * 60 + 10);
		assertThat(s.position()).isEqualTo(new Point(600, 108));
	}
	
	
	@Test
	public void ASectIsAttractedToThePlayerInRange() {
		Player p1 = e.addPlayer(new Player(), new Point(600, 0));
		Player p2 = e.addPlayer(new Player(), new Point(600, 1200));
		Sect s = e.addSect(movingSect(e,new Point(0, -1)), new Point(600, 1100));

		p1.call();
		p2.call();

		executeThisManyTurns(e, 60);
		assertThat(s.position()).isEqualTo(new Point(600, 1100-20 -4));
		
		executeThisManyTurns(e, 60);
		assertThat(s.position()).isEqualTo(new Point(600, 1100+2));
	}
	
	//TODO: this behavior must be related with sending it away
	@Test
	public void whenASectComesToCloseToThePlayerItsCaptured() {
		final Sect[] captured = new Sect[]{null};
		Player p = e.addPlayer(new Player(){
			public void onCapture(Sect s){
				captured[0] = s;
			}
		}, new Point(600, 0));
		Sect s = e.addSect(movingSect(e,new Point(0, +1)), new Point(600, 50));

		p.call();

		executeThisManyTurns(e, 2 * 60);
		assertThat(s.position()).isEqualTo(new Point(600, 20));
		assertThat(captured[0]).isEqualTo(s);
	}

}
