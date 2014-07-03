package org.unbiquitous.games.uSect.objects.behaviour;

import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.games.uSect.objects.Something;

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