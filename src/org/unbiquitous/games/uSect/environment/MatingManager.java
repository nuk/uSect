package org.unbiquitous.games.uSect.environment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.games.uSect.objects.Sect.Behavior;
import org.unbiquitous.uImpala.engine.core.GameSingletons;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.util.math.Point;

public class MatingManager {
	private Set<Sect> matingDuringThisTurn = new HashSet<Sect>();
	private Environment env;
	private int matingEnergy;
	private int initialEnergy;
	private Integer matingCooldown;
	
	public MatingManager(Environment env) {
		this.env = env;
		GameSettings settings = GameSingletons.get(GameSettings.class);
		matingEnergy = settings.getInt("usect.mating.energy",30*60);
		initialEnergy = settings.getInt("usect.initial.energy",30*60*10);
		matingCooldown = settings.getInt("usect.mating.cooldown",50);
	}

	public void add(Sect s){
		matingDuringThisTurn.add(s);
	}
	
	public void update() {
		for(Sect male: matingDuringThisTurn){
			for(Sect female : matingDuringThisTurn){
				if (male.id() != female.id() 
						&& male.position().distanceTo(female.position()) <= male.influenceRadius()
						&& env.stats(male.id()).busyCoolDown <= 0){
					env.changeStats(male, Environment.Stats.change().busyCoolDown(matingCooldown));
					env.markAsBusy(male);
				}
			}
		}
		matingDuringThisTurn.clear();
		
		Set<Sect> parents = new HashSet<Sect>();
		for(Sect coller: env.busy()){
//			dataMap.get(coller.id).busyCoolDown --;
			if(env.stats(coller.id()).busyCoolDown > 0){
				env.changeStats(coller, Environment.Stats.change().busyCoolDown(-1));
			}
			if(env.stats(coller.id()).busyCoolDown <= 0){
				env.changeStats(coller, Environment.Stats.change().energy(-matingEnergy));
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
						Behavior b = father.behavior().clone();
						env.add(new Sect(b),new Environment.Stats(position,initialEnergy));					}
				}
				
			}
		}
		env.busy().removeAll(parents);
	}
}