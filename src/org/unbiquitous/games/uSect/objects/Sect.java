package org.unbiquitous.games.uSect.objects;

import static java.lang.String.format;

import java.util.UUID;

import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.games.uSect.objects.behavior.Carnivore;
import org.unbiquitous.games.uSect.objects.behavior.Herbivore;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.asset.SimetricShape;
import org.unbiquitous.uImpala.engine.asset.Text;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.math.Point;

public class Sect extends EnvironmentObject {
	private static final Color ATTACK_PAINT = new Color(192, 57, 43,128);
	
	private Behavior behavior;
	protected Point currentDir;
	private long angle = 0;
	
	private int radius = 30;
	private SimetricShape shape;
	private SimetricShape influence;
	private SimetricShape mating;
	protected Text text;
	private int influenceRadius = 50;

	public interface Behavior  extends Cloneable{
		public Something.Feeding feeding();
		public void init(Sect s);
		public void update();
		public void enteredViewRange(Something o);
		public void leftViewRange(Something o);
		public Behavior clone(); 
	}
	
	public Sect() {
		this(new Herbivore());
	}
	
	public Sect(Behavior behavior) {
//		Font font = new Font("Verdana", Font.BOLD, 12);
		AssetManager assets = GameComponents.get(AssetManager.class);
		if(assets != null){
//			text = assets.newText(font, "");
		}
		if(behavior instanceof Carnivore){
			shape = assets.newSimetricShape(new Point(), new Color(211, 84, 0,200), radius,5);
		}else{
			shape = assets.newSimetricShape(new Point(), new Color(41, 128, 185,200), radius,7);
		}
		influence = assets.newCircle(new Point(), ATTACK_PAINT, influenceRadius);
		mating = assets.newSimetricShape(new Point(), ATTACK_PAINT, influenceRadius,13);
		
		this.behavior = behavior;
		behavior.init(this);
	}
	
	public int radius() {
		return radius;
	}
	
	public int influenceRadius() {
		return influenceRadius;
	}
	
	public Behavior behavior() {
		return behavior;
	}
	
	public void update() {
		behavior.update();
	}
	
	public void enteredSight(Something o){
		behavior.enteredViewRange(o);
	}
	
	public void leftSight(Something o) {
		behavior.leftViewRange(o);
	}

	public void moveTo(Point dir) {
		currentDir = dir;
		env.moveTo(this,dir);
	}

	public void attack() {
		env.attack(this);
	}
	
	public void mate() {
		env.mate(this);
	}
	
	public Point positionOf(UUID id){
		return env.stats(id).position;
	}
	
	public void render(GameRenderers renderers) {
		if(env.stats(id).attackCoolDown > 0){
			influence.radius(influenceRadius*env.stats(id).attackCoolDown/5);
			influence.center(position());
			influence.render();
		}
		
		if(env.stats(id).busyCoolDown > 0){
			mating.radius(influenceRadius*env.stats(id).busyCoolDown/50+radius);
			mating.center(position());
			mating.render();
		}
		
		shape.center(position());
		shape.radius(radius+(int)((Random.v()*6)-3));
		shape.rotate(rotationAngle());
		shape.render();
		
//		Screen screen = GameComponents.get(Screen.class);
//		text.setText(energy().toString());
//		text.render(screen, (float)position().x, (float)position().y, Corner.TOP_LEFT, 1f, 0f, 1f, 1f, new org.unbiquitous.uImpala.util.Color(0, 0, 0));
	}

	private float rotationAngle() {
		angle += ((Random.v()*3)-2)*45;
		return angle %= 360;
	}
	
	public String toString() {
		return format("Sect:%s%s[%s]",behavior.feeding(), position(),energy());
	};
}