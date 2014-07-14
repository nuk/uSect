package org.unbiquitous.games.uSect.objects.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.uImpala.util.math.Point;

public abstract class TargetFocused  implements Sect.Behavior{
	//REMOVE this
	protected static final int ATTACK_ENERGY = 30*60;
	protected static final int INITIAL_ENERGY = (int) (ATTACK_ENERGY * 10);
	
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
		return sect.position().equals(target().position());
	}

	private Point targetDirection() {
		Point dir = new Point();
		dir.x = dimensionDirection(sect.position().x,target().position().x);
		dir.y = dimensionDirection(sect.position().y,target().position().y);
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

	protected int distanceTo(Something o1) {
		return sect.position().distanceTo(o1.position());
	}
	
	protected Something target(){
		if(targetsInSight.isEmpty()){
			return null;
		}
		return targetsInSight.getFirst();
	}
	
	public void leftViewRange(Something n) {
		targetsInSight.remove(n);
	}
	
	protected boolean insideInfluenceRadius(Something target) {
		return distanceTo(target) < sect.influenceRadius();
	}
	
	protected boolean wantToMate(Something mate) {
		return Random.v() > 1-matingChance(mate);
	}

	protected double matingChance(Something mate) {
		double chance = 0.50;
			if(mate.feeding() != this.feeding()){
				chance = 0.25;
			}
		return chance;
	}
	protected boolean hasMatingEnergy() {
		return sect.energy() >= 2*INITIAL_ENERGY;
	}
}