package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.jse.util.shapes.SimetricShape;

public class Sect extends GameObject {
	protected Point center;
	protected RandomGenerator random =  new RandomGenerator();
	private Behaviour behaviour;
	private Point currentDir;
	private int radius = 30;
	
	public interface Behaviour {
		public void init(Sect s, RandomGenerator random);
		public void update();
		public void enteredViewRange(EnvironmentObject o);
		public void leftViewRange(EnvironmentObject o);
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
	
	protected void enteredSight(EnvironmentObject o){
		behaviour.enteredViewRange(o);
	}
	
	protected void leftSight(EnvironmentObject o) {
		behaviour.leftViewRange(o);
	}

	protected void moveTo(Point dir) {
		currentDir = dir;
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
		SimetricShape triangle = new SimetricShape(center, Color.RED, radius,3);
		triangle.rotate(rotationAngle());
		triangle.render();
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

	protected void wakeup(Object... args) {}
	protected void destroy() {}
}

class Herbivore implements Sect.Behaviour{
	protected LinkedList<EnvironmentObject> nutrientsInSight;
	private Sect sect;
	
	public void init(Sect sect, RandomGenerator random) {
		this.sect = sect;
		nutrientsInSight = new LinkedList<EnvironmentObject>();
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
	
	public void enteredViewRange(EnvironmentObject n){
		nutrientsInSight.add(n);
		Collections.sort(nutrientsInSight, new Comparator<EnvironmentObject>() {
			public int compare(EnvironmentObject o1, EnvironmentObject o2) {
				return distanceTo(o1) - distanceTo(o2);
			}
		});
	}

	private EnvironmentObject target(){
		if(nutrientsInSight.isEmpty()){
			return null;
		}
		return nutrientsInSight.getFirst();
	}
	
	private int distanceTo(EnvironmentObject n) {
		return Math.abs(n.center().x-sect.center.x) + Math.abs(n.center().y-sect.center.y);
	}

	public void leftViewRange(EnvironmentObject n) {
		nutrientsInSight.remove(n);
	}

}
