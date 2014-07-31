package org.unbiquitous.games.uSect.objects.behavior;

import java.util.HashSet;
import java.util.Set;

import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.games.uSect.objects.Something.Feeding;
import org.unbiquitous.games.uSect.objects.Something.Type;

public class Herbivore extends TargetFocused{
	
	protected Set<Something> otherSects = new HashSet<Something>();
	protected long waitToMateAgain;
	
	public Feeding feeding() {
		return Feeding.HERBIVORE;
	}
	
	public void enteredViewRange(Something o){
		if(o.type() == Type.NUTRIENT){
			targetsInSight.add(o);
		}else if(o.type() == Type.SECT){
			otherSects.add(o);
		}
		sortTargets();
	}
	
	@Override
	public void leftViewRange(Something n) {
		super.leftViewRange(n);
		otherSects.remove(n);
	}
	
	@Override
	public void update() {
		if(goingToMate() && waitToMateAgain <= 0){
			waitToMateAgain = 20*50;
			sect.mate();
		}else{
			waitToMateAgain --;
		}
		super.update();
	}

	private boolean goingToMate() {
		boolean goingToMate = false;
		if(hasMatingEnergy()){
			for(Something date : otherSects){
				if(wantToMate(date) && insideInfluenceRadius(date)){
					goingToMate = true;
				}
			}
		}
		return goingToMate;
	}
	
}