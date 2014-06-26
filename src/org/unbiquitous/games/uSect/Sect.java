package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Point;

import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.jse.util.shapes.SimetricShape;

public class Sect extends GameObject {
	protected Point center;
	protected Nutrient nutrient;
	
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
	
	private void moveTo(Point dir) {
		center.x += dir.x;
		center.y += dir.y;
	}


	protected void onNutrientOnSight(Nutrient n){
		this.nutrient = n;
	}
	
	protected void render(GameRenderers renderers) {
		new SimetricShape(center, Color.RED, 10,3).render();
	}

	protected void wakeup(Object... args) {}
	protected void destroy() {}
}
