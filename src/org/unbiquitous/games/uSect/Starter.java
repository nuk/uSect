package org.unbiquitous.games.uSect;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.logging.Level;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Environment.Stats;
import org.unbiquitous.games.uSect.objects.Player;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.games.uSect.objects.behavior.Carnivore;
import org.unbiquitous.games.uSect.objects.behavior.Herbivore;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObjectTreeScene;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.KeyboardManager;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.engine.time.DeltaTime;
import org.unbiquitous.uImpala.jse.impl.core.Game;
import org.unbiquitous.uImpala.util.math.Point;
import org.unbiquitous.uos.core.UOSLogging;

public class Starter extends GameObjectTreeScene {

	private Screen screen;

	public Starter() {
		DeltaTime deltaTime = GameComponents.get(DeltaTime.class);
		deltaTime.setUPS(30);
		
		screen = GameComponents.get(ScreenManager.class).create();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		screen.open("uSect", width, height-60, false, null);

		GameComponents.put(Screen.class, screen);
		GameComponents.put(AssetManager.class,assets);
		//TODO: How to get Props from uImpala?
//		props.put("debug", false);
		Environment e = new Environment(new DeviceStats());
		e.add(new Sect(new Herbivore()), new Stats(new Point(),100*1000));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth(),0));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth()/2,0));
		e.addSect(new Sect(new Herbivore()),new Point(0,screen.getHeight()));
		e.addSect(new Sect(new Herbivore()),new Point(0,screen.getHeight()/2));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth(),screen.getHeight()));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth()/2,screen.getHeight()));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth(),screen.getHeight()/2));
		
		e.add(new Sect(new Carnivore()),new Stats(new Point(screen.getWidth()/4,screen.getHeight()/4),200*1000));
		e.add(new Sect(new Carnivore()),new Stats(new Point(screen.getWidth()/4-100,screen.getHeight()/4-200),100*1000));
		e.addSect(new Sect(new Carnivore()),new Point(3*screen.getWidth()/4,3*screen.getHeight()/4));
		
		e.addPlayer(new Player(), new Point(screen.getWidth()/2,screen.getHeight()));
		
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
		if(args.length > 0 && "--debug".equalsIgnoreCase(args[0])){
			UOSLogging.setLevel(Level.ALL);
		}
		Game.run(new GameSettings() {
			{ 
				put("first_scene", Starter.class);
				put("input_managers", Arrays.asList(MouseManager.class, KeyboardManager.class));
				put("output_managers", Arrays.asList(ScreenManager.class));
			}
		});
	}
}
