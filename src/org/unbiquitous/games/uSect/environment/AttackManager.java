package org.unbiquitous.games.uSect.environment;

import java.util.HashSet;
import java.util.Set;

import org.unbiquitous.games.uSect.objects.Sect;

public class AttackManager {
	private Set<Sect> attackersDuringThisTurn = new HashSet<Sect>();
	private Set<Sect> busyAttackers = new HashSet<Sect>();
	private Environment env;
	
	public AttackManager(Environment env) {
		this.env = env;
	}

	public void add(Sect attacker){
		if(!busyAttackers.contains(attacker) && !env.isBusyThisTurn(attacker)){
			attackersDuringThisTurn.add(attacker);
		}
	}
	
	public void update() {
		processAttacks();
		updateAttackCoolDown();
	}

	private void processAttacks() {
		for(Sect attacker: attackersDuringThisTurn){
			for(Sect deffendant : env.sects()){
				checkAttack(attacker, deffendant);
			}
		}
		attackersDuringThisTurn.clear();
	}

	private void checkAttack(Sect attacker, Sect deffendant) {
		if (attacker.id != deffendant.id 
				&& attacker.position().distanceTo(deffendant.position()) <= attacker.influenceRadius()
				&& env.stats(attacker.id).attackCoolDown <= 0){
			busyAttackers.add(attacker);
			env.changeStats(deffendant, Environment.Stats.change().energy(-30*60));
			env.changeStats(attacker, Environment.Stats.change().attackCoolDown(5));
		}
	}

	private void updateAttackCoolDown() {
		Set<Sect> remove = new HashSet<Sect>();
		for(Sect coller: busyAttackers){
			if(env.stats(coller.id).attackCoolDown > 0){
				env.changeStats(coller, Environment.Stats.change().attackCoolDown(-1));
			}
			if(env.stats(coller.id).attackCoolDown <= 0){
				remove.add(coller);
			}
		}
		busyAttackers.removeAll(remove);
	}
}