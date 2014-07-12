package org.unbiquitous.games.uSect.objects.behaviour;

import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.games.uSect.objects.Something.Feeding;

public class Carnivore extends TargetFocused{

	@Override
	public Feeding feeding() {
		return Feeding.CARNIVORE;
	}
	
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
	public void update() {
		super.update();
		Something target = target();
		if (target != null && insideInfluenceRadius(target)){
			if(wantToMate(target) && hasMatingEnergy()){
				sect.mate();
			}else{
				sect.attack();
			}
		}
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
	
	
}