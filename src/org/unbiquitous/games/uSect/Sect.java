package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Point;

import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.jse.util.shapes.SimetricShape;

public class Sect extends GameObject {
	protected Point center;
	protected Nutrient nutrient;
	protected RandomGenerator random =  new RandomGenerator();
	
	public Sect() {
		this(new Point());
	}
	
	public Sect(Point center) {
		center(center);
	}
	
	
	public void center(Point center) {
		this.center = (Point) center.clone();
	}

	protected void update() {
		if (seesNutrient()){
			moveTo(nutrientDirection());
		}
	}

	private boolean seesNutrient() {
		return nutrient != null;
	}

	private Point nutrientDirection() {
		Point dir = new Point();
		dir.x = dimensionDirection(center.x,nutrient.center.x);
		dir.y = dimensionDirection(center.y,nutrient.center.y);
		return dir;
	}

	private int dimensionDirection(int oringin, int destination) {
		int direction = oringin > destination  ? -1 : +1;
		return oringin == destination ? 0 : direction;
	}
	
	protected void onNutrientOnSight(Nutrient n){
		if(nutrient == null || distanceTo(n) < distanceTo(nutrient)){
			this.nutrient = n;
		}
	}

	private int distanceTo(Nutrient n) {
		return Math.abs(n.center.x-center.x) + Math.abs(n.center.y-center.y);
	}
	
	//TODO: Move to environemt, he's the one who decides this, he's also the one who decides about the Sects Stats
	private void moveTo(Point dir) {
		adjustDirection(dir);
		center.x += dir.x;
		center.y += dir.y;
	}

	private void adjustDirection(Point dir) {
		if(random.v() > 0.5 && dir.x != 0){
			dir.y = 0;
		}else if (random.v() <= 0.5 && dir.y != 0){
			dir.x = 0;
		}
	}


	
	protected void render(GameRenderers renderers) {
		new SimetricShape(center, Color.RED, 10,3).render();
	}

	protected void wakeup(Object... args) {}
	protected void destroy() {}
}
