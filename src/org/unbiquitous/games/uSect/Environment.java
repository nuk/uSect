package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.jse.util.shapes.Circle;
import org.unbiquitous.uImpala.jse.util.shapes.Rectangle;

public class Environment extends GameObject {

	private DeviceStats deviceStats;
	protected RandomGenerator random =  new RandomGenerator();
	Rectangle background;
	List<Nutrient> nurtients = new ArrayList<Nutrient>();

	public Environment(DeviceStats deviceStats) {
		this.deviceStats = deviceStats;
		createBackground();
	}

	private void createBackground() {
		Screen screen = GameComponents.get(Screen.class);
		Point center = new Point(screen.getWidth()/2, screen.getHeight()/2);
		background = new Rectangle(center, Color.WHITE, screen.getWidth(), screen.getHeight());
	}

	public void update() {
		if(random.v() >= chancesOfNutrients()){
			nutrients().add(new Nutrient());
		}
	}

	private double chancesOfNutrients() {
		long totalMemory = deviceStats.totalMemory();
		int maxMemory = 16*1024;
		if(totalMemory >= maxMemory ){
			return 0.95;
		}else if(totalMemory > 512 ){
			double memoryRatio = ((double)totalMemory)/maxMemory;
			return 1-(0.01+0.04*memoryRatio);
		}
		return 0.99;
	}

	public List<Nutrient> nutrients() {
		return nurtients;
	}

	@Override
	protected void render(GameRenderers renderers) {
		background.render();
		for(Nutrient n : nutrients()){
			n.render(null);
		}
	}

	@Override
	protected void wakeup(Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void destroy() {
		// TODO Auto-generated method stub
		
	}

}

class RandomGenerator{
	private double value = -1;

	public void setvalue(double value) {
		this.value = value;
	}
	
	public double v(){
		if(value >=0 ){
			return value;
		}
		return Math.random();
	}
}

class Nutrient extends GameObject{

	Point center;
	
	public Nutrient() {
		int x = (int) (Math.random()*400);
		int y = (int) (Math.random()*400);
		center = new Point(x, y);
	}
	
	@Override
	protected void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void render(GameRenderers renderers) {
		new Circle(center, Color.GREEN, 20).render();;
	}

	@Override
	protected void wakeup(Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void destroy() {
		// TODO Auto-generated method stub
		
	}
	
}