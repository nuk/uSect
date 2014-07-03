package org.unbiquitous.games.uSect.objects.behaviour;

import org.unbiquitous.games.uSect.objects.Something;

public class Herbivore extends TargetFocused{
	
	public void enteredViewRange(Something o){
		if(o.type() == Something.Type.NUTRIENT){
			targetsInSight.add(o);
		}
		sortTargets();
	}
}