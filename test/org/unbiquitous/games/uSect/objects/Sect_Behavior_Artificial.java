package org.unbiquitous.games.uSect.objects;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.setUpEnvironment;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.driver.execution.executionUnity.ExecutionUnity;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.games.uSect.objects.Something.Feeding;
import org.unbiquitous.games.uSect.objects.behavior.Artificial;
import org.unbiquitous.games.uSect.objects.behavior.Herbivore;
import org.unbiquitous.uImpala.util.math.Point;

public class Sect_Behavior_Artificial {

	private Environment e;

	@Before public void setUp(){
		e = setUpEnvironment();
		Random.setvalue(0);
	}
	
	@Test public void allowsSectToMoveArround(){
		StringBuilder script = new StringBuilder()
			.append("function update()\n")
			.append("	move(1,0)\n")
			.append("end\n")
			;
		
		ExecutionUnity unity = new ExecutionUnity(script.toString());
		Sect s = e.addSect(new Sect(new Artificial(unity, Feeding.CARNIVORE)),new Point(20,20));
		
		e.update();
		assertThat(s.position()).isEqualTo(new Point(21,20));
	}
	
	@Test public void allowsSectToAttack(){
		StringBuilder script = new StringBuilder()
			.append("function update()\n")
			.append("	attack()\n")
			.append("end\n")
			;
		
		ExecutionUnity unity = new ExecutionUnity(script.toString());
		Sect s = e.addSect(new Sect(new Artificial(unity, Feeding.CARNIVORE)),new Point(20,20));
		Sect h = e.addSect(new Sect(new Herbivore()),new Point(50,20));
		
		e.update();
		assertThat(h.energy()).isLessThan(s.energy());
	}
	
	@Test public void allowsToMate(){
		StringBuilder script = new StringBuilder()
			.append("function update()\n")
			.append("	mate()\n")
			.append("end\n")
			;
		
		ExecutionUnity unity = new ExecutionUnity(script.toString());
		e.addSect(new Sect(new Artificial(unity, Feeding.CARNIVORE)),new Point(20,20));
		e.addSect(new Sect(new Herbivore(){
			public void update() {
				sect.mate();
			}
		}),new Point(50,20));
		
		executeThisManyTurns(e, 50);
		assertThat(e.sects()).hasSize(3);
	}
	
}
