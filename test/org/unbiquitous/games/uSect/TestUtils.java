package org.unbiquitous.games.uSect;

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
import org.unbiquitous.uImpala.jse.impl.io.Screen;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.math.Point;

public class TestUtils {
	public static void executeThisManyTurns(Environment e, int numberOfTurns) {
		for (int i = 0; i < numberOfTurns; i++){
			e.update();
		}
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
		});
		Random.setvalue(0);
		return new Environment();
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
		return new org.unbiquitous.uImpala.jse.util.shapes.SimetricShape(center, paint, radius, numberOfSides);
	}
	
	@Override
	public SimetricShape newCircle(Point center, Color paint, float radius) {
		return new org.unbiquitous.uImpala.jse.util.shapes.SimetricShape(center, paint, radius, 360);
	}
	
	@Override
	public Rectangle newRectangle(Point center, Color paint, float width, float height) {
		return new org.unbiquitous.uImpala.jse.util.shapes.Rectangle(center, paint, width, height);
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