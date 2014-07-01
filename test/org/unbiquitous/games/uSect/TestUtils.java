package org.unbiquitous.games.uSect;

import org.unbiquitous.games.uSect.environment.Environment;

public class TestUtils {

	public static void executeThisManyTurns(Environment e, int numberOfTurns) {
		for (int i = 0; i < numberOfTurns; i++){
			e.update();
		}
	}
	
}
