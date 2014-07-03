package org.unbiquitous.games.uSect.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.unbiquitous.games.uSect.Sect;
import org.unbiquitous.games.uSect.Something;

class SectManager {
	private static final int INITIAL_ENERGY = 30*60 * 10;
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
		env.add(s.id, position, (long) INITIAL_ENERGY);
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
		checkForNewSects(s);
		s.update();
		env.addEnergy(s.id, -1);
		if(env.stats(s.id).energy <= 0){
			env.addCorpse(s.center());
			sectsThatDiedThisTurn.add(s);
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