package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.jse.util.shapes.Circle;
import org.unbiquitous.uImpala.jse.util.shapes.Rectangle;

public class Environment extends GameObject {

	private DeviceStats deviceStats;
	private Screen screen;
	protected RandomGenerator random =  new RandomGenerator();
	Rectangle background;
	List<Nutrient> nutrients = new ArrayList<Nutrient>();
	List<Sect> sects = new ArrayList<Sect>();
	List<Sect> newSects = new ArrayList<Sect>();

	public Environment() {
		this(new DeviceStats());
	}
	
	
	public Environment(DeviceStats deviceStats) {
		this.deviceStats = deviceStats;
		createBackground();
	}

	private void createBackground() {
		screen = GameComponents.get(Screen.class);
		Point center = new Point(screen.getWidth()/2, screen.getHeight()/2);
		background = new Rectangle(center, Color.WHITE, screen.getWidth(), screen.getHeight());
	}

	public void update() {
		if(random.v() >= chancesOfNutrients()){
			addNutrient();
		}
		for(Sect s : sects){
			Set<Nutrient> forRemoval = new HashSet<Nutrient>();
			for(Nutrient n : nutrients){
				n.insightOf(s);
				if(n.center.equals(s.center)){
					n.inContactWith(s);
				}
				if(n.hasBeenConsumed){
					forRemoval.add(n);
				}
			}
			nutrients.removeAll(forRemoval); // FIXME: what about other Sects?
			
			for(Sect s2 : newSects){
				if(!s.equals(s2)){
					s.enteredSight(new Something(s2.id, s2.center, Something.Type.SECT));
				}
			}
			newSects.clear(); // FIXME: what about other Sects?
			
			s.update();
		}
	}

	protected Nutrient addNutrient() {
		int x = (int) (Math.random()*screen.getWidth());
		int y = (int) (Math.random()*screen.getHeight());
		Point center = new Point(x, y);
		return addNutrient(center);
	}


	protected Nutrient addNutrient(Point center) {
		Nutrient n = new Nutrient(center);
		nutrients.add(n);
		return n;
	}
	
	protected Sect addSect(Sect s) {
		s.setEnv(this);
		sects.add(s);
		newSects.add(s);
		return s;
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

	@Override
	protected void render(GameRenderers renderers) {
		background.render();
		for(Nutrient n : nutrients){
			n.render(null);
		}
		for(Sect s : sects){
			s.render(null);
		}
	}

	protected void wakeup(Object... args) {}
	protected void destroy() {}


	public void moveTo(Sect sect, Point dir) {
		adjustDirection(dir);
		sect.center = determineFinalPosition(sect, dir);
	}

	private void adjustDirection(Point dir) {
		double lottery = random.v();
		if(lottery > 0.5 && dir.x != 0){
			dir.y = 0;
		}else if (lottery <= 0.5 && dir.y != 0){
			dir.x = 0;
		}
	}
	
	private Point determineFinalPosition(Sect sect, Point dir) {
		Point forwardPosition = new Point(sect.center.x + dir.x, sect.center.y + dir.y);
		if(!hasColided(sect, forwardPosition)){
			return forwardPosition;
		}else if (random.v() > 0.5){
			Point backwardsPosition = new Point(sect.center.x - dir.x, sect.center.y - dir.y);
			return backwardsPosition;
		}
		return sect.center;
	}


	private boolean hasColided(Sect sect, Point newPos) {
		boolean hasColided = false;
		for(Sect s: sects){
			if(!sect.equals(s) && distanceOf(s.center, newPos) < sect.radius()){
				hasColided = true;
			}
		}
		return hasColided;
	}

	private int distanceOf(Point origin, Point desttination) {
		return Math.abs(origin.x-desttination.x) + Math.abs(origin.y-desttination.y);
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

abstract class EnvironmentObject extends GameObject {
	protected UUID id = UUID.randomUUID();
	protected void update() {}
	protected void wakeup(Object... args) {}
	protected void destroy() {}
	
	public boolean equals(Object obj) {
		if(obj instanceof EnvironmentObject){
			return ((EnvironmentObject)obj).id.equals(this.id) ;
		}
		return false;
	}
	
	public int hashCode() {
		return id.hashCode();
	}
	
}

class Nutrient extends EnvironmentObject{
	Point center;
	Set<Sect> targetOf;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	Map<Sect, Integer> absortionTable = new HashMap();
	private int radius = 10;
	
	boolean hasBeenConsumed = false;
	
	public Nutrient(Point center) {
		this.center = center;
		targetOf = new HashSet<Sect>();
	}

	public void inContactWith(Sect s) {
		absortionTable.put(s, 1+absortionTable.get(s));
		if(absortionTable.get(s) >= 5){
			notifyAbsortionToAll();
			hasBeenConsumed = true;
		}
	}

	private void notifyAbsortionToAll() {
		for(Sect s1: targetOf){
			s1.leftSight(new Something(id, center, Something.Type.NUTRIENT));
		}
	}

	public void insightOf(Sect s) {
		if(! targetOf.contains(s)){
			s.enteredSight(new Something(id, center, Something.Type.NUTRIENT)); 
			targetOf.add(s);
			absortionTable.put(s, 0);
		};
	}

	protected void render(GameRenderers renderers) {
		new Circle(center, Color.GREEN.darker(), radius).render();
	}
}