package org.unbiquitous.games.uSect.environment;

import java.awt.Color;
import java.io.Serializable;
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
import org.unbiquitous.uImpala.util.math.Point;
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
	
	
	private Set<Sect> matingDuringThisTurn = new HashSet<Sect>();
	private Set<Sect> busyThisTurn = new HashSet<Sect>();
	
	public void update() {
		nutrients.update();
		sects.update();
		updateAttack();
		
		for(Sect male: matingDuringThisTurn){
			for(Sect female : sects.sects()){
				if (male.id != female.id 
						&& male.center().distanceTo(female.center()) <= male.influenceRadius()
						&& stats(male.id).busyCoolDown <= 0){
					dataMap.get(male.id).busyCoolDown = 50;
					busyThisTurn.add(male);
//					addEnergy(deffendant.id, -30*60);
				}
			}
		}
		matingDuringThisTurn.clear();
		Set<Sect> remove = new HashSet<Sect>();
		for(Sect coller: busyThisTurn){
			dataMap.get(coller.id).busyCoolDown --;
			if(stats(coller.id).busyCoolDown <= 0){
				remove.add(coller);
			}
		}
		
		if(!remove.isEmpty()){
			this.addSect(new Sect(), new Point());
		}
		busyThisTurn.removeAll(remove);
		
		
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
				&& attacker.center().distanceTo(deffendant.center()) <= attacker.influenceRadius()
				&& stats(attacker.id).attackCoolDown <= 0){
			dataMap.get(attacker.id).attackCoolDown = 5;
			busyAttackers.add(attacker);
			changeStats(deffendant, Stats.n().energy(-30*60));
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
	
	public Integer attackCooldown(UUID objectId){
		if(!dataMap.containsKey(objectId)){
			return null;
		}
		return dataMap.get(objectId).attackCoolDown;
	}
	
	public Integer busyCooldown(UUID objectId){
		if(!dataMap.containsKey(objectId)){
			return null;
		}
		return dataMap.get(objectId).busyCoolDown;
	}
	
	protected Stats stats(UUID objectId){
		if (!dataMap.containsKey(objectId)){
			return new Stats(new Point(),0);
		}
		return dataMap.get(objectId).clone();
	}
	
	protected Stats add(EnvironmentObject object, Stats initialStats){
		dataMap.put(object.id(), initialStats);
		return initialStats;
	}
	
	protected Stats moveTo(UUID objectId, Point position){
		Stats stats = dataMap.get(objectId);
		stats.position = position;
		return stats;
	}
	
	//TODO: not to be public
	public Stats changeStats(EnvironmentObject object, Stats diff){
		Stats stats = dataMap.get(object.id());
		stats.energy += diff.energy;
		return stats;
	}
	
	public Nutrient addNutrient() {
		int x = (int) (Math.random()*screen.getWidth());
		int y = (int) (Math.random()*screen.getHeight());
		return addNutrient(new Point(x, y));
	}

	public void moveTo(Sect sect, Point dir) {
		if(!busyThisTurn.contains(sect)){
			mover.moveTo(sect, dir);
		}
	}
	
	public void attack(Sect sect) {
		if(!busyAttackers.contains(sect) && !busyThisTurn.contains(sect)){
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
		this.add(p, new Stats(position, 0));
		players.add(p);
		return p;
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

	public static class Stats implements Serializable, Cloneable{
		Point position;
		long energy;
		int attackCoolDown = 0;
		int busyCoolDown;
		
		public Stats() {
			this(new Point(), 0);
		}
		
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
			return new Stats(position.clone(), energy, attackCoolDown,busyCoolDown);
		}
		
		public static Stats n(){
			return new Stats();
		}
		
		public Stats energy(long energy) {
			this.energy = energy;
			return this;
		}
	}
}

