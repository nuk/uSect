package org.unbiquitous.games.uSect;

import java.awt.Point;
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

	private Screen screen;

	public Starter() {
		DeltaTime deltaTime = GameComponents.get(DeltaTime.class);
		deltaTime.setUPS(30);
		
		screen = GameComponents.get(ScreenManager.class).create();
		screen.open("uSect", 800, 600, false, null);

		GameComponents.put(Screen.class, screen);
		
		Environment e = new Environment(new DeviceStats());
		e.addSect(new Sect(new Point()));
		e.addSect(new Sect(new Point(screen.getWidth(),0)));
		e.addSect(new Sect(new Point(0,screen.getHeight())));
		e.addSect(new Sect(new Point(screen.getWidth(),screen.getHeight())));
		add(e);
	}
	
	@Override
	public void update() {
		super.update();
		if (screen.isCloseRequested()) {
			GameComponents.get(org.unbiquitous.uImpala.engine.core.Game.class).quit();
		}
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	public static void main(String[] args) {
		Game.run(new GameSettings() {
			{ 
				put("first_scene", Starter.class);
				put("input_managers", Arrays.asList(MouseManager.class));
				put("output_managers", Arrays.asList(ScreenManager.class));
			}
		});
	}
}
