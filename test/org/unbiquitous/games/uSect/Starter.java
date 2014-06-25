package org.unbiquitous.games.uSect;

import java.util.Arrays;

import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObjectTreeScene;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.engine.time.DeltaTime;
import org.unbiquitous.uImpala.jse.impl.core.Game;

public class Starter extends GameObjectTreeScene {

	public Starter() {
		DeltaTime deltaTime = GameComponents.get(DeltaTime.class);
		deltaTime.setUPS(30);
		
		
		Screen screen = GameComponents.get(ScreenManager.class).create();
		screen.open("uSect", 800, 600, false, null);

		GameComponents.put(Screen.class, screen);
		
		add(new Environment(new DeviceStats()));
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	public static void main(String[] args) {
		Game.run(new GameSettings() {
			{ // TODO: Game Settings could have helper methods
				put("first_scene", Starter.class);
				put("input_managers", Arrays.asList(MouseManager.class));
				put("output_managers", Arrays.asList(ScreenManager.class));
			}
		});
	}
}
