package org.unbiquitous.games.uSect.objects.behaviour;

import org.unbiquitous.games.uSect.objects.Something;

public class Carnivore extends TargetFocused{

	public void enteredViewRange(Something o){
		if(isSect(o) || isCorpse(o)){
			targetsInSight.add(o);
		}
		sortTargets();
	}

	private boolean isCorpse(Something o) {
		return o.type() == Something.Type.CORPSE;
	}

	private boolean isSect(Something o) {
		return o.type() == Something.Type.SECT;
	}
	
	@Override
	protected Something target() {
		for(Something s : targetsInSight){
			if(isCorpse(s)){
				return s;
			}
		}
		return super.target();
	}
	
	@Override
	public void update() {
		super.update();
		if (target() != null && distanceTo(target()) < sect.influenceRadius()){
			sect.attack();
		}
	}
}