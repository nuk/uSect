package org.unbiquitous.games.uSect.environment;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;
import static org.unbiquitous.games.uSect.TestUtils.movingSect;
import static org.unbiquitous.games.uSect.TestUtils.setUpEnvironment;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.objects.Player;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.uImpala.engine.core.GameSingletons;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.IOResource;
import org.unbiquitous.uImpala.engine.io.MouseEvent;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.MouseSource;
import org.unbiquitous.uImpala.util.math.Point;

public class Environment_PlayerInteraction {
	private Environment e;
	private MouseManager manager;
	
	@Before public void setUpMouseEnvironment() {
		
		GameSettings settings = new GameSettings();
		settings.put("usect.player.id", UUID.randomUUID().toString());
		manager = new MouseManager();
		manager.add(new MouseSource(1));
		GameSingletons.put(MouseManager.class, manager);
		
		e = setUpEnvironment(settings);
	}
	
	@Test public void callPlayerOnAMouseClick(){
		e.addPlayer(new Player(), new Point(50,0));
		Sect s = e.addSect(movingSect(e,new Point(+1,0)),new Point(50,50));
		
		fireMouseClick(manager);
		
		executeThisManyTurns(e, 60);
		
		assertThat(s.position()).isNotEqualTo(new Point(50+60,50));
	}
	
	@Test public void doesNothingIfItsNotAPlayerEnvironment(){
		e = setUpEnvironment();
		e.addPlayer(new Player(), new Point(50,0));
		e.addPlayer(new Player(), new Point(50,0));
		Sect s = e.addSect(movingSect(e,new Point(+1,0)),new Point(50,50));
		
		fireMouseClick(manager);
		
		executeThisManyTurns(e, 60);
		
		assertThat(s.position()).isEqualTo(new Point(50+60,50));
	}

	private void fireMouseClick(MouseManager manager) {
		List<IOResource> mousesAvailable = manager.list();
		MouseSource aMouse = (MouseSource) mousesAvailable.get(0);
		aMouse.add(new MouseEvent(MouseSource.EVENT_BUTTON_DOWN, 0, 0, 0));
		manager.update();
	}

}
