package org.unbiquitous.games.uSect.environment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.uImpala.util.math.Point;

public class MatingManager {
	private static final int ATTACK_ENERGY = 30*60;
	private static final int INITIAL_ENERGY = (int) (ATTACK_ENERGY * 10);
	
	private Set<Sect> matingDuringThisTurn = new HashSet<Sect>();
	private Environment env;
	
	public MatingManager(Environment env) {
		this.env = env;
	}

	public void add(Sect s){
		matingDuringThisTurn.add(s);
	}
	
	public void update() {
		for(Sect male: matingDuringThisTurn){
			for(Sect female : matingDuringThisTurn){
				if (male.id != female.id 
						&& male.position().distanceTo(female.position()) <= male.influenceRadius()
						&& env.stats(male.id).busyCoolDown <= 0){
//					dataMap.get(male.id).busyCoolDown = 50;
					env.changeStats(male, Environment.Stats.change().busyCoolDown(50));
					env.markAsBusy(male);
				}
			}
		}
		matingDuringThisTurn.clear();
		
		Set<Sect> parents = new HashSet<Sect>();
		for(Sect coller: env.busy()){
//			dataMap.get(coller.id).busyCoolDown --;
			if(env.stats(coller.id).busyCoolDown > 0){
				env.changeStats(coller, Environment.Stats.change().busyCoolDown(-1));
			}
			if(env.stats(coller.id).busyCoolDown <= 0){
				env.changeStats(coller, Environment.Stats.change().energy(-30*60));
				parents.add(coller);
			}
		}
		
		if(!parents.isEmpty()){
			Iterator<Sect> it = parents.iterator();
			while(parents.size() > 1){
				Sect father = it.next();
				it.remove();
				for (Sect mother : parents){
					if(father.position().distanceTo(mother.position()) <= father.influenceRadius()){
						Point position = father.position().clone();
						position.add(mother.position());
						position.x /= 2;
						position.y /= 2;
						env.add(new Sect(),new Environment.Stats(position,INITIAL_ENERGY));					}
				}
				
			}
		}
		env.busy().removeAll(parents);
	}
}