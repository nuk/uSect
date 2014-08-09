package org.unbiquitous.games.uSect;

import static org.mockito.Mockito.mock;

import java.awt.Font;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Environment.Stats;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.games.uSect.objects.Sect.Behavior;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.asset.Audio;
import org.unbiquitous.uImpala.engine.asset.Rectangle;
import org.unbiquitous.uImpala.engine.asset.SimetricShape;
import org.unbiquitous.uImpala.engine.asset.Sprite;
import org.unbiquitous.uImpala.engine.asset.Text;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.KeyboardSource;
import org.unbiquitous.uImpala.engine.io.MouseSource;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.math.Point;

public class TestUtils {
	public static void executeThisManyTurns(Environment e, int numberOfTurns) {
		for (int i = 0; i < numberOfTurns; i++){
			e.update();
		}
	}
	
	public static Sect movingSect(final Environment e, final Point direction) {
		return new Sect() {
			public void update() {
				e.moveTo(this, direction);
			}
		};
	}
	
	public static Environment setUpEnvironment(){
		return setUpEnvironment(new GameSettings());
	}
	
	public static Environment setUpEnvironment(GameSettings settings){
		return setUpEnvironment(settings, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public static Environment setUpEnvironment(GameSettings settings, 
								final int width, final int height){
		GameComponents.put(GameSettings.class, settings);
		GameComponents.put(AssetManager.class, new DummyAssetManager());
		GameComponents.put(org.unbiquitous.uImpala.engine.io.Screen.class, new Screen(){
			public int getWidth() {		return width;	}
			public int getHeight() {	return height;	}
			public void open() {}
			public void open(String t, int w, int h, boolean f, String i,
					boolean gl) {}
			public String getTitle() { return null;}
			public void setTitle(String title) {}
			public void setSize(int width, int height) {}
			public boolean isFullscreen() {return false;}
			public void setFullscreen(boolean fullscreen) {}
			public String getIcon() { return null;}
			public void setIcon(String icon) {}
			public boolean isCloseRequested() {return false;}
			public MouseSource getMouse() { return null;}
			public KeyboardSource getKeyboard() { return null;}
			public void start() { }
			public void stop() {}
			protected void update() {}
			public void close() {}
			public boolean isUpdating() {return false;}
		});
		Random.setvalue(0);
		return new Environment(mock(DeviceStats.class));
	}
	
	public static Sect addSect(Environment e, Behavior behavior, Point position, Long energy) {
		Stats initialStats = new Stats(position, energy);
		return (Sect) e.add(new Sect(behavior), initialStats);
	}
	
	public static Sect addSect(Environment e, Behavior behavior, Point position) {
		return addSect(e, behavior, position, 3l * 10 * 30 * 60);
	}
}

class DummyAssetManager extends AssetManager {
	public DummyAssetManager() {
		factory = new Factory(){
			public AssetManager create() {
				return DummyAssetManager.this;
			}
		};
	}
	@Override
	public Text newText(Font font, String text) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Text newText(String fontPath, String text) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Sprite newSprite(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SimetricShape newSimetricShape(Point center, Color paint, float radius,
			int numberOfSides) {
		return new SimetricShape(center, paint, radius, numberOfSides){
			public void render() {}
			public void rotate(float angleInDegrees) {}
			};
	}
	
	@Override
	public SimetricShape newCircle(Point center, Color paint, float radius) {
		return new SimetricShape(center, paint, radius, 360){
			public void render() {}
			public void rotate(float angleInDegrees) {}
		};
	}
	
	@Override
	public Rectangle newRectangle(Point center, Color paint, float width, float height) {
		return new Rectangle(center, paint, width, height){
			public void render() {}
			public void rotate(float angleInDegrees) {}
		};
	}
	
	@Override
	public Audio newAudio(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}