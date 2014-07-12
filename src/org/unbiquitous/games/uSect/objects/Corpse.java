package org.unbiquitous.games.uSect.objects;

import java.awt.Color;

import org.unbiquitous.uImpala.jse.util.shapes.Circle;
import org.unbiquitous.uImpala.util.math.Point;

public class Corpse extends Nutrient{

	public Corpse() {
		radius = 30;
		shape = new Circle(new Point(), Color.GRAY, radius);
		type = Something.Type.CORPSE;
		energy *= 5;
	}
}
