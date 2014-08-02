package org.unbiquitous.games.uSect;

import java.util.Arrays;
import java.util.logging.Level;

import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.KeyboardManager;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.jse.impl.core.Game;
import org.unbiquitous.uos.core.UOSLogging;

public class StarterJSE extends Starter {

	@SuppressWarnings({ "unchecked", "serial" })
	public static void main(String[] args) {
		if(args.length > 0 && "--debug".equalsIgnoreCase(args[0])){
			UOSLogging.setLevel(Level.ALL);
		}
		Game.run(new GameSettings() {
			{ 
				put("first_scene", Starter.class);
				put("input_managers", Arrays.asList(MouseManager.class, KeyboardManager.class));
				put("output_managers", Arrays.asList(ScreenManager.class));
				put("usect.speed.value", 5);
			}
		});
	}
	
}
