package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.asset.Text;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.jse.util.shapes.SimetricShape;
import org.unbiquitous.uImpala.util.Corner;

public class Sect extends EnvironmentObject {
	private Behaviour behaviour;
	private Point currentDir;
	private Environment env;
	
	private int radius = 30;
	SimetricShape shape;
	private Text text;
	
	public interface Behaviour {
		public void init(Sect s);
		public void update();
		public void enteredViewRange(Something o);
		public void leftViewRange(Something o);
	}
	
	public Sect() {
		this(new Herbivore());
	}
	
	public Sect(Behaviour behaviour) {
		Font font = new Font("Verdana", Font.BOLD, 12);
		AssetManager assets = GameComponents.get(AssetManager.class);
		if(assets != null){
			text = assets.newText(font, "");
		}
		if(behaviour instanceof Carnivore){
			shape = new SimetricShape(new Point(), new Color(211, 84, 0,200), radius,5);
		}else{
			shape = new SimetricShape(new Point(), new Color(41, 128, 185,200), radius,7);
		}
		
		this.behaviour = behaviour;
		behaviour.init(this);
	}
	
	public Point center() {
		return env.position(id);
	}
	
	public Long energy() {
		return env.energy(id);
	}

	public void setEnv(Environment env) {
		this.env = env;
	}
	
	public int radius() {
		return radius;
	}
	
	public void update() {
		behaviour.update();
	}
	
	public void enteredSight(Something o){
		behaviour.enteredViewRange(o);
	}
	
	public void leftSight(Something o) {
		behaviour.leftViewRange(o);
	}

	protected void moveTo(Point dir) {
		currentDir = dir;
		env.moveTo(this,dir);
	}

	public void render(GameRenderers renderers) {
		shape.center(center());
		shape.rotate(rotationAngle());
		shape.render();
		
		Screen screen = GameComponents.get(Screen.class);
		text.setText(energy().toString());
		text.render(screen, (float)center().x, (float)center().y, Corner.TOP_LEFT, 1f, 0f, 1f, 1f, new org.unbiquitous.uImpala.util.Color(0, 0, 0));
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

abstract class TargetFocused  implements Sect.Behaviour{
	protected LinkedList<Something> targetsInSight;
	protected Sect sect;
	
	public void init(Sect sect) {
		this.sect = sect;
		targetsInSight = new LinkedList<Something>();
	}

	public void update() {
		if (hasATarget() && !onTopOfTarget()){
			sect.moveTo(targetDirection());
		}
	}

	private boolean hasATarget() {
		return target() != null;
	}
	
	private boolean onTopOfTarget() {
		return sect.center().equals(target().center());
	}

	private Point targetDirection() {
		Point dir = new Point();
		dir.x = dimensionDirection(sect.center().x,target().center().x);
		dir.y = dimensionDirection(sect.center().y,target().center().y);
		return dir;
	}

	private int dimensionDirection(int oringin, int destination) {
		int direction = oringin > destination  ? -1 : +1;
		return oringin == destination ? 0 : direction;
	}

	protected void sortTargets() {
		Collections.sort(targetsInSight, new Comparator<Something>() {
			public int compare(Something o1, Something o2) {
				return distanceTo(o1) - distanceTo(o2);
			}
		});
	}

	protected Something target(){
		if(targetsInSight.isEmpty()){
			return null;
		}
		return targetsInSight.getFirst();
	}
	
	protected int distanceTo(Something n) {
		return Math.abs(n.center().x-sect.center().x) + Math.abs(n.center().y-sect.center().y);
	}

	public void leftViewRange(Something n) {
		targetsInSight.remove(n);
	}
}

class Herbivore extends TargetFocused{
	
	public void enteredViewRange(Something o){
		if(o.type() == Something.Type.NUTRIENT){
			targetsInSight.add(o);
		}
		sortTargets();
	}
}

class Carnivore extends TargetFocused{

	public void enteredViewRange(Something o){
		if(o.type() == Something.Type.SECT){
			targetsInSight.add(o);
		}
		sortTargets();
	}
	
	@Override
	public void update() {
		super.update();
	}
}