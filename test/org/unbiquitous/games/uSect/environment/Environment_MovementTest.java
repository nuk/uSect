package org.unbiquitous.games.uSect.environment;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.setUpEnvironment;

import org.junit.Test;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.util.math.Point;

public class Environment_MovementTest {
	
	private Environment e;
	private Sect sect ;
	private Point direction;
	
	private void setUp(int speed){
		GameSettings settings = new GameSettings();
		settings.put("usect.speed.value", speed);
		e = setUpEnvironment(settings);
		sect = new Sect(){
			@Override
			public void update() {
				env.moveTo(this, direction);
			}
		};
		e.addSect(sect,new Point(10,10));
	}
	
	@Test public void sectMustNotMoveFasterThanTheSpeedLimit(){
		setUp(1);
		direction = new Point(10,10);
		Random.setvalue(0.49);
		e.update();
		assertThat(sect.position()).isEqualTo(new Point(10,11));
	}
	
	@Test public void speedLimitIsEquallyDistributed(){
		setUp(2);
		Random.setvalue(0.49);
		direction = new Point(10,10);
		e.update();
		assertThat(sect.position()).isEqualTo(new Point(11,11));
	}
	
	@Test public void speedLimitIsEquallyDistributedOdd(){
		setUp(3);
		Random.setvalue(0.49);
		direction = new Point(10,15);
		
		e.update();
		assertThat(sect.position()).isEqualTo(new Point(11,12));
	}
	
	@Test public void speedLimitIsDistributedOddWithRemainder(){
		setUp(5);
		Random.setvalue(0.49);
		direction = new Point(10,10);
		
		e.update();
		assertThat(sect.position()).isEqualTo(new Point(12,13));
	}
	
	@Test public void dontGoesFurtherThanRequestedBySect(){
		setUp(6);
		direction = new Point(1,1);
		
		e.update();
		assertThat(sect.position()).isEqualTo(new Point(11,11));
	}
}
