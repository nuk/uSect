package org.unbiquitous.games.uSect.objects.behaviour;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.uImpala.util.math.Point;

//TODO: its Behavior
public abstract class TargetFocused  implements Sect.Behaviour{
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
}