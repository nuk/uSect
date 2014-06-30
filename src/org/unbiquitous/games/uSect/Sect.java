package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.jse.util.shapes.SimetricShape;

public class Sect extends EnvironmentObject {
	private Behaviour behaviour;
	protected Point center;
	private Point currentDir;
	private Environment env;
	
	private int radius = 30;
	SimetricShape shape;
	
	public interface Behaviour {
		public void init(Sect s);
		public void update();
		public void enteredViewRange(Something o);
		public void leftViewRange(Something o);
	}
	
	public Sect() {
		this(new Point(), new Herbivore());
	}
	
	public Sect(Point center, Herbivore behaviour) {
		center(center);
		shape = new SimetricShape(center, new Color(41, 128, 185), radius,7);
		this.behaviour = behaviour;
		behaviour.init(this);
	}
	
	public void center(Point center) {
		this.center = (Point) center.clone();
	}

	public void setEnv(Environment env) {
		this.env = env;
	}
	
	public int radius() {
		return radius;
	}
	
	protected void update() {
		behaviour.update();
	}
	
	protected void enteredSight(Something o){
		if(o.type() == Something.Type.NUTRIENT){
			behaviour.enteredViewRange(o);
		}
	}
	
	protected void leftSight(Something o) {
		behaviour.leftViewRange(o);
	}

	protected void moveTo(Point dir) {
		currentDir = dir;
		env.moveTo(this,dir);
	}

	protected void render(GameRenderers renderers) {
		shape.center(center);
		shape.rotate(rotationAngle());
		shape.render();
	}

	private float rotationAngle() {
		if(      new Point(+1, 0).equals(currentDir)){
			return -45;
		}else if(new Point(+1,-1).equals(currentDir)){
			return -90;
		}else if(new Point( 0,-1).equals(currentDir)){
			return -135;
		}else if(new Point(-1,-1).equals(currentDir)){
			return -180;
		}else if(new Point(-1, 0).equals(currentDir)){
			return +135;
		}else if(new Point(-1,+1).equals(currentDir)){
			return +90;
		}else if(new Point( 0,+1).equals(currentDir)){
			return +45;
		}
		return 0;
	}

}

class Herbivore implements Sect.Behaviour{
	protected LinkedList<Something> nutrientsInSight;
	private Sect sect;
	
	public void init(Sect sect) {
		this.sect = sect;
		nutrientsInSight = new LinkedList<Something>();
	}

	public void update() {
		if (hasATarget()){
			sect.moveTo(nutrientDirection());
		}
	}

	private boolean hasATarget() {
		return target() != null;
	}

	private Point nutrientDirection() {
		Point dir = new Point();
		dir.x = dimensionDirection(sect.center.x,target().center().x);
		dir.y = dimensionDirection(sect.center.y,target().center().y);
		return dir;
	}

	private int dimensionDirection(int oringin, int destination) {
		int direction = oringin > destination  ? -1 : +1;
		return oringin == destination ? 0 : direction;
	}
	
	public void enteredViewRange(Something n){
		nutrientsInSight.add(n);
		Collections.sort(nutrientsInSight, new Comparator<Something>() {
			public int compare(Something o1, Something o2) {
				return distanceTo(o1) - distanceTo(o2);
			}
		});
	}

	private Something target(){
		if(nutrientsInSight.isEmpty()){
			return null;
		}
		return nutrientsInSight.getFirst();
	}
	
	private int distanceTo(Something n) {
		return Math.abs(n.center().x-sect.center.x) + Math.abs(n.center().y-sect.center.y);
	}

	public void leftViewRange(Something n) {
		nutrientsInSight.remove(n);
	}

}
