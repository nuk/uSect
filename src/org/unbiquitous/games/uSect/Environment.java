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
import org.unbiquitous.uImpala.jse.util.shapes.Rectangle;

public class Environment extends GameObject {

	private DeviceStats deviceStats;
	private Screen screen;
	protected RandomGenerator random =  new RandomGenerator();
	Rectangle background;
	
	List<Nutrient> nutrients = new ArrayList<Nutrient>();
	List<Sect> sects = new ArrayList<Sect>();
	List<Sect> newSects = new ArrayList<Sect>();
	Map<UUID,Point> positionMap = new HashMap<UUID,Point>();

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
			checkNutrients(s);
			checkForNewSects(s);
			s.update();
		}
		newSects.clear(); 
	}

	private void checkNutrients(Sect s) {
		Set<Nutrient> forRemoval = new HashSet<Nutrient>();
		for(Nutrient n : nutrients){
			n.insightOf(s);
			checkEating(s, n);
			checkConsumtion(forRemoval, n);
		}
		nutrients.removeAll(forRemoval);
	}

	private void checkEating(Sect s, Nutrient n) {
		if(n.center().equals(s.center())){
			n.inContactWith(s);
		}
	}

	private void checkConsumtion(Set<Nutrient> forRemoval, Nutrient n) {
		if(n.hasBeenConsumed){
			forRemoval.add(n);
		}
	}

	private void checkForNewSects(Sect s) {
		for(Sect s2 : newSects){
			if(!s.equals(s2)){
				s.enteredSight(new Something(s2.id, this, Something.Type.SECT));
			}
		}
	}

	protected Nutrient addNutrient() {
		int x = (int) (Math.random()*screen.getWidth());
		int y = (int) (Math.random()*screen.getHeight());
		Point center = new Point(x, y);
		return addNutrient(center);
	}


	protected Nutrient addNutrient(Point position) {
		Nutrient n = new Nutrient();
		n.setEnv(this);
		nutrients.add(n);
		positionMap.put(n.id, position);
		return n;
	}
	
	public Point position(UUID objectId){
		if(!positionMap.containsKey(objectId)){
			return null;
		}
		return (Point) positionMap.get(objectId).clone();
	}
	
	protected Sect addSect(Sect s, Point position) {
		s.setEnv(this);
//		s.center(position);
		positionMap.put(s.id, position);
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
		positionMap.put(sect.id, determineFinalPosition(sect, dir));
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
		Point forwardPosition = new Point(sect.center().x + dir.x, sect.center().y + dir.y);
		if(!hasColided(sect, forwardPosition)){
			return forwardPosition;
		}else if (random.v() > 0.5){
			Point backwardsPosition = new Point(sect.center().x - dir.x, sect.center().y - dir.y);
			return backwardsPosition;
		}
		return sect.center();
	}


	private boolean hasColided(Sect sect, Point newPos) {
		boolean hasColided = false;
		for(Sect s: sects){
			if(!sect.equals(s) && distanceOf(s.center(), newPos) < sect.radius()){
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