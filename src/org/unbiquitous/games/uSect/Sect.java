package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.jse.util.shapes.SimetricShape;

public class Sect extends GameObject {
	protected Point center;
	protected RandomGenerator random =  new RandomGenerator();
	private Behaviour behaviour;
	
	public interface Behaviour {
		public void init(Sect s, RandomGenerator random);
		public void update();
		public void onNutrientInSight(Nutrient n);
		public void onNutrientAbsorved(Nutrient n);
	}
	
	public Sect() {
		this(new Point());
	}
	
	public Sect(Point center) {
		center(center);
		behaviour = new Herbivore();
		behaviour.init(this, random);
	}
	
	
	public void center(Point center) {
		this.center = (Point) center.clone();
	}

	protected void update() {
		behaviour.update();
	}
	
	protected void onNutrientInSight(Nutrient n){
		behaviour.onNutrientInSight(n);
	}
	
	protected void onNutrientAbsorved(Nutrient n) {
		behaviour.onNutrientAbsorved(n);
	}

	protected void moveTo(Point dir) {
		adjustDirection(dir);
		center.x += dir.x;
		center.y += dir.y;
	}

	private void adjustDirection(Point dir) {
		double lottery = random.v();
		if(lottery > 0.5 && dir.x != 0){
			dir.y = 0;
		}else if (lottery <= 0.5 && dir.y != 0){
			dir.x = 0;
		}
	}
	
	protected void render(GameRenderers renderers) {
		new SimetricShape(center, Color.RED, 30,3).render();
	}

	protected void wakeup(Object... args) {}
	protected void destroy() {}
}

class Herbivore implements Sect.Behaviour{
	protected Nutrient targetedNutrient;
	protected Set<Nutrient> nutrientsInSight;
	private Sect sect;
	
	public void init(Sect sect, RandomGenerator random) {
		this.sect = sect;
		nutrientsInSight = new HashSet<Nutrient>();
	}

	public void update() {
		if (seesNutrient()){
			sect.moveTo(nutrientDirection());
		}
	}

	private boolean seesNutrient() {
		return targetedNutrient != null;
	}

	private Point nutrientDirection() {
		Point dir = new Point();
		dir.x = dimensionDirection(sect.center.x,targetedNutrient.center.x);
		dir.y = dimensionDirection(sect.center.y,targetedNutrient.center.y);
		return dir;
	}

	private int dimensionDirection(int oringin, int destination) {
		int direction = oringin > destination  ? -1 : +1;
		return oringin == destination ? 0 : direction;
	}
	
	public void onNutrientInSight(Nutrient n){
		if(targetedNutrient == null){
			targetedNutrient = n;
		}else if(distanceTo(n) < distanceTo(targetedNutrient)){
			nutrientsInSight.add(targetedNutrient);
			targetedNutrient = n;
		}else{
			nutrientsInSight.add(n);
		}
	}

	private int distanceTo(Nutrient n) {
		return Math.abs(n.center.x-sect.center.x) + Math.abs(n.center.y-sect.center.y);
	}

	public void onNutrientAbsorved(Nutrient n) {
		if(n.equals(targetedNutrient)){
			targetedNutrient = null;
		}
		nutrientsInSight.remove(n);
		if(!nutrientsInSight.isEmpty()){
			updateToNearestNutrient();
		}
	}

	private void updateToNearestNutrient() {
		targetedNutrient = nutrientsInSight.iterator().next();
		for(Nutrient n1: nutrientsInSight){
			if(distanceTo(n1) < distanceTo(targetedNutrient)){
				targetedNutrient = n1;
			}
		}
	}
	
}
