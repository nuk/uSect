package org.unbiquitous.games.uSect.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.unbiquitous.games.uSect.DeviceStats;
import org.unbiquitous.games.uSect.objects.Corpse;
import org.unbiquitous.games.uSect.objects.Nutrient;
import org.unbiquitous.games.uSect.objects.Sect;

class NutrientManager {
	Environment env;
	private RandomGenerator random;
	private DeviceStats deviceStats;
	private List<Nutrient> nutrients = new ArrayList<Nutrient>();
	private List<Corpse> corpses = new ArrayList<Corpse>();
	
	public NutrientManager(Environment env, RandomGenerator random,
			DeviceStats deviceStats) {
		super();
		this.env = env;
		this.random = random;
		this.deviceStats = deviceStats;
	}

	List<Nutrient> nutrients(){
		return nutrients;
	}

	Nutrient addNutrient(Point position) {
		Nutrient n = new Nutrient();
		n.setEnv(env);
		env.add(n.id, position,0);
		nutrients.add(n);
		return n;
	}
	
	Corpse addCorpse(Point position) {
		Corpse n = new Corpse();
		n.setEnv(env);
		env.add(n.id, position,0);
		corpses.add(n);
		return n;
	}
	
	public void update() {
		if(random.v() >= chancesOfNutrients()){
			env.addNutrient();
		}
		for(Sect s : env.sects()){
			checkNutrients(s);
			checkCorpses(s);
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

	private void checkCorpses(Sect s) {
		Set<Nutrient> forRemoval = new HashSet<Nutrient>();
		for(Nutrient n : env.corpses()){
			n.insightOf(s);
			checkEating(s, n);
			checkConsumtion(forRemoval, n);
		}
		//TODO: clear stats
		env.corpses().removeAll(forRemoval);
	}
	
	private void checkEating(Sect s, Nutrient n) {
		if(n.center().equals(s.center())){
			n.inContactWith(s);
		}
	}

	private void checkConsumtion(Set<Nutrient> forRemoval, Nutrient n) {
		Sect eater = n.hasBeenConsumedBy();
		if(eater != null){
			env.addEnergy(eater.id, n.energy()+1);
			forRemoval.add(n); 
		}
	}
	
	private double chancesOfNutrients() {
		long totalMemory = deviceStats.totalMemory();
		int maxMemory = 16*1024;
		if(totalMemory >= maxMemory ){
			return 1-0.05;
		}else if(totalMemory > 512 ){
			double memoryRatio = ((double)totalMemory)/maxMemory;
			return 1-(0.01+0.04*memoryRatio);
		}
		return 1-0.01;
	}

	public List<Corpse> corpses() {
		return corpses;
	}
}