package org.unbiquitous.games.uSect.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.unbiquitous.games.uSect.Nutrient;
import org.unbiquitous.games.uSect.Sect;
import org.unbiquitous.games.uSect.Something;

class SectManager {
	private static final int NUTRIENT_INCREMENT = 30*60;
	private static final int INITIAL_ENERGY = NUTRIENT_INCREMENT * 10;
	private Environment env;
	private List<Sect> sects;
	private List<Sect> sectsAddedThisTurn;
	private List<Sect> sectsThatDiedThisTurn;

	public SectManager(Environment env) {
		this.env = env;
		this.sects = new ArrayList<Sect>();
		this.sectsAddedThisTurn = new ArrayList<Sect>();
		this.sectsThatDiedThisTurn = new ArrayList<Sect>();
	}

	Sect addSect(Sect s, Point position){
		s.setEnv(env);
		env.stats(s.id).position = position;
		env.stats(s.id).energy = (long) INITIAL_ENERGY;
		sects.add(s);
		sectsAddedThisTurn.add(s);
		return s;
	}

	List<Sect> sects(){
		return sects;
	}
	
	void update() {
		for(Sect s : sects){
			updateSect(s);
		}
		//TODO: clear stats
		sects.removeAll(sectsThatDiedThisTurn);
		sectsThatDiedThisTurn.clear(); //TODO: remove from other places
		sectsAddedThisTurn.clear(); //TODO: remove from other places 
	}

	private void updateSect(Sect s) {
		checkNutrients(s);
		checkForNewSects(s);
		s.update();
		env.stats(s.id).energy -= 1;
		if(env.stats(s.id).energy <= 0){
			env.addCorpse(s.center());
			sectsThatDiedThisTurn.add(s);
		}
	}

	private void checkNutrients(Sect s) {
		Set<Nutrient> forRemoval = new HashSet<Nutrient>();
		for(Nutrient n : env.nutrients()){
			n.insightOf(s);
			checkEating(s, n);
			checkConsumtion(forRemoval, n);
		}
		//TODO: clear stats
		env.nutrients().removeAll(forRemoval);
	}

	private void checkEating(Sect s, Nutrient n) {
		if(n.center().equals(s.center())){
			n.inContactWith(s);
		}
	}

	private void checkConsumtion(Set<Nutrient> forRemoval, Nutrient n) {
		Sect eater = n.hasBeenConsumedBy();
		if(eater != null){
			env.stats(eater.id).energy+=NUTRIENT_INCREMENT+1; // Increment + this turn 
			forRemoval.add(n); 
		}
	}

	private void checkForNewSects(Sect s) {
		for(Sect s2 : sectsAddedThisTurn){
			if(!s.equals(s2)){
				s.enteredSight(new Something(s2.id, env, Something.Type.SECT));
			}
		}
	}
}