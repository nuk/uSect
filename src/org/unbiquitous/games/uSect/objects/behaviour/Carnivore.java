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
	
	//REMOVE this
	private static final int ATTACK_ENERGY = 30*60;
	private static final int INITIAL_ENERGY = (int) (ATTACK_ENERGY * 10);
	
	@Override
	public void update() {
		super.update();
		
		if (target() != null && distanceTo(target()) < sect.influenceRadius()){
			if(sect.energy() >= 2*INITIAL_ENERGY){
				sect.mate();
			}else{
				sect.attack();
			}
		}
	}
}