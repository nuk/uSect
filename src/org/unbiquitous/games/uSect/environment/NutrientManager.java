package org.unbiquitous.games.uSect.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.unbiquitous.games.uSect.DeviceStats;
import org.unbiquitous.games.uSect.environment.Environment.Stats;
import org.unbiquitous.games.uSect.objects.Corpse;
import org.unbiquitous.games.uSect.objects.Nutrient;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameSettings;

class NutrientManager implements EnvironemtObjectManager{
	private Environment env;
	private DeviceStats deviceStats;
	private boolean createNutrients = true; 
	private List<Nutrient> nutrients = new ArrayList<Nutrient>();
	private List<Corpse> corpses = new ArrayList<Corpse>();
	
	int maxMemory = 16*1024;
	int minMemory = 512;
	
	public NutrientManager(Environment env, DeviceStats deviceStats) {
		super();
		this.env = env;
		this.deviceStats = deviceStats;
		
		GameComponents.get(GameSettings.class); // TODO:Use it
	}

	List<Nutrient> nutrients(){
		return nutrients;
	}

	@Override
	public EnvironmentObject add(EnvironmentObject o) {
		if(o instanceof Corpse){
			return add((Corpse) o);
		}else if (o instanceof Nutrient){
			return  add((Nutrient) o);
		}
		return null;
	}
	
	Nutrient add(Nutrient n) {
		nutrients.add(n);
		return n;
	}
	
	Corpse add(Corpse c) {
		corpses.add(c);
		return c;
	}
	
	public void update() {
		if(createNutrients && Random.v() >= chancesOfNutrients()){
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
		if(n.position().equals(s.position())){
			n.inContactWith(s);
		}
	}

	private void checkConsumtion(Set<Nutrient> forRemoval, Nutrient n) {
		Sect eater = n.hasBeenConsumedBy();
		if(eater != null){
			env.changeStats(eater, Stats.change().energy(n.energy()+1));
			forRemoval.add(n); 
		}
	}
	
	private double chancesOfNutrients() {
		long totalMemory = deviceStats.totalMemory();
		if(totalMemory >= maxMemory ){
			return 1-0.05;
		} else {
			if(totalMemory > minMemory ){
				double memoryRatio = ((double)totalMemory)/maxMemory;
				return 1-(0.01+0.04*memoryRatio);
			}
		}
		return 1-0.01;
	}

	public List<Corpse> corpses() {
		return corpses;
	}

	public void disableCreation() {
		createNutrients = false;
	}
}