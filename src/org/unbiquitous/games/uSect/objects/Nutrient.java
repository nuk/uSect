package org.unbiquitous.games.uSect.objects;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.games.uSect.objects.Something.Type;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.jse.util.shapes.Circle;
import org.unbiquitous.uImpala.util.math.Point;

public class Nutrient extends EnvironmentObject{
	private Set<Sect> targetOf;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map<Sect, Integer> absortionTable = new HashMap();
	
	protected int radius = 10;
	protected Environment env;
	protected Circle shape = new Circle(new Point(), Color.GREEN.darker(), radius);
	protected Something.Type type = Type.NUTRIENT;
	protected long energy = 30*60;
	
	private Sect hasBeenConsumedBy;

	public Nutrient() {
		targetOf = new HashSet<Sect>();
	}

	public void setEnv(Environment env) {
		this.env = env;
	}
	
	public Point position() {
		return env.position(id);
	}
	
	public Sect hasBeenConsumedBy() {
		return hasBeenConsumedBy;
	}
	
	public long energy() {
		return energy;
	}
	
	public void inContactWith(Sect s) {
		absortionTable.put(s, 1+absortionTable.get(s));
		if(absortionTable.get(s) >= 5){
			notifyAbsortionToAll();
			hasBeenConsumedBy = s;
		}
	}

	private void notifyAbsortionToAll() {
		for(Sect s1: targetOf){
			s1.leftSight(new Something(id, env, type));
		}
	}

	public void insightOf(Sect s) {
		if(! targetOf.contains(s)){
			s.enteredSight(new Something(id, env, type)); 
			targetOf.add(s);
			absortionTable.put(s, 0);
		};
	}

	public void render(GameRenderers renderers) {
		shape.center(position());
		shape.render();
	}
}