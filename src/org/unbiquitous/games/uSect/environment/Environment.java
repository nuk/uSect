package org.unbiquitous.games.uSect.environment;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.lwjgl.input.Keyboard;
import org.unbiquitous.games.uSect.DeviceStats;
import org.unbiquitous.games.uSect.objects.Corpse;
import org.unbiquitous.games.uSect.objects.Nutrient;
import org.unbiquitous.games.uSect.objects.Player;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.jse.util.shapes.Rectangle;
import org.unbiquitous.uos.core.InitialProperties;

public class Environment extends GameObject {

	private Screen screen;
	//TODO: fix this
	public RandomGenerator random =  new RandomGenerator();
	private Rectangle background;
	
	private Map<UUID,Stats> dataMap = new HashMap<UUID,Stats>();
	private NutrientManager nutrients;
	private SectManager sects;
	private MovementManager mover;
	private List<Player> players = new ArrayList<Player>();

	private Set<Sect> matingDuringThisTurn = new HashSet<Sect>();

	static class Stats implements Cloneable{
		Point position;
		long energy;
		int attackCoolDown = 0;
		private int busyCoolDown;
		public Stats(Point position, long energy) {
			this(position, energy, 0,0);
		}
		
		private Stats(Point position, long energy, int attackCoolDown, int busyCoolDown) {
			this.position = position;
			this.energy = energy;
			this.attackCoolDown = attackCoolDown;
			this.busyCoolDown = busyCoolDown;
		}
		
		public Stats clone() {
			return new Stats((Point)position.clone(), energy, attackCoolDown,0);
		}
	}
	
	public Environment(InitialProperties props) {
		this(new DeviceStats(),props);
	}
	
	public Environment(DeviceStats deviceStats,InitialProperties props) {
		nutrients = new NutrientManager(this, random, deviceStats);
		sects = new SectManager(this);
		mover = new MovementManager(this, random);
		createBackground();
	}

	private void createBackground() {
		screen = GameComponents.get(Screen.class);
		Point center = new Point(screen.getWidth()/2, screen.getHeight()/2);
		background = new Rectangle(center, Color.WHITE, screen.getWidth(), screen.getHeight());
	}
	
	
	
	public void update() {
		nutrients.update();
		sects.update();
		updateAttack();
		
//		Set<Sect> remove = new HashSet<Sect>();
//		for(Sect male: matingDuringThisTurn){
//			for(Sect female : sects.sects()){
//				if (male.id != female.id 
//						&& distanceOf(male.center(), female.center()) <= male.influenceRadius()
//						&& stats(male.id).busyCoolDown <= 0){
//					dataMap.get(male.id).busyCoolDown = 20;
////					addEnergy(deffendant.id, -30*60);
//				}
//			}
//			dataMap.get(male.id).busyCoolDown --;
//			if(stats(male.id).busyCoolDown <= 0){
//				remove.add(male);
//			}
//		}
//		matingDuringThisTurn.removeAll(remove);
//		matingDuringThisTurn.clear();
		
		
		
		//TODO: untested
		if (screen.getKeyboard() != null){
			if(screen.getKeyboard().getKey(Keyboard.KEY_A)){
				System.out.println("Attack");
				for(Player p: players){
					p.attack();
				}
			}else if(screen.getKeyboard().getKey(Keyboard.KEY_C)){
				System.out.println("Call");
				for(Player p: players){
					p.call();
				}
			} 
		}
	}

	private Set<Sect> attackersDuringThisTurn = new HashSet<Sect>();
	private Set<Sect> busyAttackers = new HashSet<Sect>();
	
	private void updateAttack() {
		processAttacks();
		updateAttackCoolDown();
	}

	private void processAttacks() {
		for(Sect attacker: attackersDuringThisTurn){
			for(Sect deffendant : sects.sects()){
				checkAttack(attacker, deffendant);
			}
		}
		attackersDuringThisTurn.clear();
	}

	private void checkAttack(Sect attacker, Sect deffendant) {
		if (attacker.id != deffendant.id 
				&& distanceOf(attacker.center(), deffendant.center()) <= attacker.influenceRadius()
				&& stats(attacker.id).attackCoolDown <= 0){
			dataMap.get(attacker.id).attackCoolDown = 5;
			busyAttackers.add(attacker);
			addEnergy(deffendant.id, -30*60);
			System.out.println("a");
		}
	}

	private void updateAttackCoolDown() {
		Set<Sect> remove = new HashSet<Sect>();
		for(Sect coller: busyAttackers){
			dataMap.get(coller.id).attackCoolDown --;
			if(stats(coller.id).attackCoolDown <= 0){
				remove.add(coller);
			}
		}
		busyAttackers.removeAll(remove);
	}
	
	//TODO: duplicated
	private int distanceOf(Point origin, Point desttination) {
		return Math.abs(origin.x-desttination.x) + Math.abs(origin.y-desttination.y);
	}
	
	public Point position(UUID objectId){
		if(!dataMap.containsKey(objectId)){
			return null;
		}
		return (Point) dataMap.get(objectId).position.clone();
	}
	
	public Long energy(UUID objectId){
		if(!dataMap.containsKey(objectId)){
			return null;
		}
		return dataMap.get(objectId).energy;
	}
	
	public Integer cooldown(UUID objectId){
		if(!dataMap.containsKey(objectId)){
			return null;
		}
		return dataMap.get(objectId).attackCoolDown;
	}
	
	protected Stats stats(UUID objectId){
		if (!dataMap.containsKey(objectId)){
			return new Stats(new Point(),0);
		}
		return dataMap.get(objectId).clone();
	}
	
	protected Stats add(UUID objectId, Point position, long energy){
		Stats stats = new Stats(position,energy);
		dataMap.put(objectId, stats);
		return stats;
	}
	
	protected Stats moveTo(UUID objectId, Point position){
		Stats stats = dataMap.get(objectId);
		stats.position = position;
		return stats;
	}
	
	//FIXME: I don't like the idea of this to be public
	public Stats addEnergy(UUID objectId, long increment){
		Stats stats = dataMap.get(objectId);
		stats.energy += increment;
		return stats;
	}
	
	public Nutrient addNutrient() {
		int x = (int) (Math.random()*screen.getWidth());
		int y = (int) (Math.random()*screen.getHeight());
		return addNutrient(new Point(x, y));
	}

	public void attack(Sect sect) {
		if(!busyAttackers.contains(sect)){
			attackersDuringThisTurn.add(sect);
		}
	}
	
	public void mate(Sect sect) {
		matingDuringThisTurn.add(sect);
	}
	
	public Nutrient addNutrient(Point position) {
		return nutrients.addNutrient(position);
	}
	
	public Corpse addCorpse(Point position) {
		return nutrients.addCorpse(position);
	}
	
	public Sect addSect(Sect s, Point position) {
		return sects.addSect(s, position);
	}
	
	public Player addPlayer(Player p, Point position) {
		p.setEnv(this);
		this.add(p.id, position, 0);
		players.add(p);
		return p;
	}

	public void moveTo(Sect sect, Point dir) {
		mover.moveTo(sect, dir);
	}

	public List<Sect> sects() {
		return sects.sects();
	}
	
	public List<Nutrient> nutrients(){
		return nutrients.nutrients();
	}
	
	public List<Corpse> corpses(){
		return nutrients.corpses();
	}
	
	protected void render(GameRenderers renderers) {
		background.render();
		renderNutrients();
		renderSects();
		for(Player p :players){
			p.render(null);
		}
	}

	private void renderSects() {
		for(Sect s : sects()){
			s.render(null);
		}
	}

	private void renderNutrients() {
		for(Nutrient n : nutrients()){
			n.render(null);
		}
		for(Nutrient n : corpses()){
			n.render(null);
		}
	}

	protected void wakeup(Object... args) {}
	protected void destroy() {}

	public void disableNutrientsCreation() {
		nutrients.disableCreation();
	}

}

