package org.unbiquitous.games.uSect.objects.behavior;

import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.games.uSect.objects.Something.Feeding;

public class Carnivore extends TargetFocused{

	protected long waitToMateAgain; 
	
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
			if(wantToMate(target) && hasMatingEnergy() &&
					waitToMateAgain <= 0){
				System.out.println("mate");
				sect.mate();
				waitToMateAgain = 10*50;
			}else{
				sect.attack();
			}
		}
		waitToMateAgain --;
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