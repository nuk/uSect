package org.unbiquitous.games.uSect;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.jse.util.shapes.Circle;

public class Nutrient extends EnvironmentObject{
	private Set<Sect> targetOf;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map<Sect, Integer> absortionTable = new HashMap();
	
	protected int radius = 10;
	protected Environment env;
	protected Circle shape = new Circle(new Point(), Color.GREEN.darker(), radius);
	
	private Sect hasBeenConsumedBy;

	public Nutrient() {
		targetOf = new HashSet<Sect>();
	}

	public void setEnv(Environment env) {
		this.env = env;
	}
	
	public Point center() {
		return env.position(id);
	}
	
	public Sect hasBeenConsumedBy() {
		return hasBeenConsumedBy;
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
			s1.leftSight(new Something(id, env, Something.Type.NUTRIENT));
		}
	}

	public void insightOf(Sect s) {
		if(! targetOf.contains(s)){
			s.enteredSight(new Something(id, env, Something.Type.NUTRIENT)); 
			targetOf.add(s);
			absortionTable.put(s, 0);
		};
	}

	public void render(GameRenderers renderers) {
		shape.center(center());
		shape.render();
	}
}