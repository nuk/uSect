package org.unbiquitous.games.uSect.environment;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private Rectangle background;
	
	private Map<UUID,Stats> dataMap = new HashMap<UUID,Stats>();
	private NutrientManager nutrients;
	private SectManager sects;
	private List<EnvironemtObjectManager> managers;
	
	private MovementManager mover;
	private List<Player> players = new ArrayList<Player>();

	public Environment(InitialProperties props) {
		this(new DeviceStats(),props);
	}
	
	public Environment(DeviceStats deviceStats,InitialProperties props) {
		nutrients = new NutrientManager(this, deviceStats);
		sects = new SectManager(this);
		mover = new MovementManager(this);
		managers = Arrays.asList(nutrients, sects);
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
		for(EnvironemtObjectManager mng : managers){
			mng.update();
		}
		updateAttack();
		
		for(Sect male: matingDuringThisTurn){
			for(Sect female : matingDuringThisTurn){
				System.out.println(matingDuringThisTurn);
				if (male.id != female.id 
						&& male.position().distanceTo(female.position()) <= male.influenceRadius()
						&& stats(male.id).busyCoolDown <= 0){
					dataMap.get(male.id).busyCoolDown = 50;
					busyThisTurn.add(male);
				}
			}
		}
		matingDuringThisTurn.clear();
		
		Set<Sect> parents = new HashSet<Sect>();
		for(Sect coller: busyThisTurn){
			dataMap.get(coller.id).busyCoolDown --;
			if(stats(coller.id).busyCoolDown <= 0){
				changeStats(coller, Stats.change().energy(-30*60));
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
						add(new Sect(),new Stats(position,INITIAL_ENERGY));
					}
				}
				
			}
		}
		busyThisTurn.removeAll(parents);
		
		
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
				&& attacker.position().distanceTo(deffendant.position()) <= attacker.influenceRadius()
				&& stats(attacker.id).attackCoolDown <= 0){
			dataMap.get(attacker.id).attackCoolDown = 5;
			busyAttackers.add(attacker);
			changeStats(deffendant, Stats.change().energy(-30*60));
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
	
	public Stats stats(UUID objectId){
		if (!dataMap.containsKey(objectId)){
			return null;
		}
		return dataMap.get(objectId).clone();
	}
	
	protected Stats moveTo(UUID objectId, Point position){
		Stats stats = dataMap.get(objectId);
		stats.position = position;
		return stats;
	}
	
	protected Stats changeStats(EnvironmentObject object, Stats diff){
		Stats stats = dataMap.get(object.id());
		stats.energy += diff.energy;
		return stats;
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
	
	public EnvironmentObject add(EnvironmentObject object, Stats initialStats){
		object.setEnv(this);
		dataMap.put(object.id(), initialStats.clone());
		for(EnvironemtObjectManager mng: managers){
			mng.add(object);
		}
		return object;
	}
	
	public Nutrient addNutrient() {
		int x = (int) (Math.random()*screen.getWidth());
		int y = (int) (Math.random()*screen.getHeight());
		return addNutrient(new Point(x, y));
	}
	
	public Nutrient addNutrient(Point position) {
		Nutrient n = new Nutrient();
		add(n, new Stats(position,ATTACK_ENERGY)); 
		return n;
	}
	
	public Corpse addCorpse(Point position) {
		Corpse c = new Corpse();
		this.add(c, new Stats(position,5*ATTACK_ENERGY)); 
		return c;
	}
	
	private static final int ATTACK_ENERGY = 30*60;
	private static final int INITIAL_ENERGY = (int) (ATTACK_ENERGY * 10);
	
	public Sect addSect(Sect s, Point position) {
		add(s, new Stats(position, INITIAL_ENERGY));
		return s;
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

	@SuppressWarnings("serial")
	public static class Stats implements Serializable, Cloneable{
		public Point position;
		public long energy;
		public int attackCoolDown = 0;
		public int busyCoolDown;
		
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
			try {
				return (Stats) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		
		public static Stats change(){
			return new Stats();
		}
		
		public Stats energy(long energy) {
			this.energy = energy;
			return this;
		}
	}
}

interface EnvironemtObjectManager{
	public EnvironmentObject add(EnvironmentObject o);
	public void update();
}

