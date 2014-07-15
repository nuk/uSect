package org.unbiquitous.games.uSect;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.jse.impl.io.Screen;

public class TestUtils {

	public static void executeThisManyTurns(Environment e, int numberOfTurns) {
		for (int i = 0; i < numberOfTurns; i++){
			e.update();
		}
	}
	
	
	public static Environment setUpEnvironment(){
		GameComponents.put(GameSettings.class, new GameSettings());
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen());
		Random.setvalue(0);
		return new Environment();
	}
}
