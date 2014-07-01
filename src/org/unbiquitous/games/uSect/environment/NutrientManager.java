package org.unbiquitous.games.uSect.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.unbiquitous.games.uSect.Corpse;
import org.unbiquitous.games.uSect.DeviceStats;
import org.unbiquitous.games.uSect.Nutrient;

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
		nutrients.add(n);
		env.stats(n.id).position = position;
		return n;
	}
	
	Corpse addCorpse(Point position) {
		Corpse n = new Corpse();
		n.setEnv(env);
		corpses.add(n);
		env.stats(n.id).position = position;
		return n;
	}
	
	public void update() {
		if(random.v() >= chancesOfNutrients()){
			env.addNutrient();
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