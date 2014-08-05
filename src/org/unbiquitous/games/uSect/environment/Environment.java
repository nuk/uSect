package org.unbiquitous.games.uSect.environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.unbiquitous.games.uSect.DeviceStats;
import org.unbiquitous.games.uSect.objects.Corpse;
import org.unbiquitous.games.uSect.objects.Nutrient;
import org.unbiquitous.games.uSect.objects.Player;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.asset.Rectangle;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.math.Point;

public class Environment extends GameObject {

	private Screen screen;
	private Rectangle background;
	
	private Map<UUID,Stats> dataMap = new HashMap<UUID,Stats>();
	private NutrientManager nutrients;
	private SectManager sects;
	private PlayerManager players;
	private List<EnvironemtObjectManager> managers;
	
	private MovementManager mover;
	private AttackManager attack;
	private MatingManager mate;
	private Set<Sect> busyThisTurn = new HashSet<Sect>();
	private Set<Sect> frozenThisTurn = new HashSet<Sect>();

	private int initialEnergy, nutrientEnergy, corpseEnergy;
	private long turnNumber;
	
	public Environment() {
		this(new DeviceStats());
	}
	
	public Environment(DeviceStats deviceStats) {
		nutrients = new NutrientManager(this, deviceStats);
		sects = new SectManager(this);
		players = new PlayerManager(this);
		managers = Arrays.asList(players, nutrients, sects);
		mover = new MovementManager(this);
		attack = new AttackManager(this);
		mate = new MatingManager(this);
		createBackground();
		
		GameSettings settings = GameComponents.get(GameSettings.class);
		initialEnergy = settings.getInt("usect.initial.energy",30*60*10);
		nutrientEnergy = settings.getInt("usect.nutrient.energy",30*60);
		corpseEnergy = settings.getInt("usect.corpse.energy",5*30*60);
	}

	private void createBackground() {
		screen = GameComponents.get(Screen.class);
		AssetManager assets = GameComponents.get(AssetManager.class);
		Point center = new Point(screen.getWidth()/2, screen.getHeight()/2);
		background = assets.newRectangle(center, Color.WHITE, screen.getWidth(), screen.getHeight());
	}
	
	public void update() {
		turnNumber ++;
		for(EnvironemtObjectManager mng : managers){
			mng.update();
		}
		attack.update();
		mate.update();
		
		
		//TODO: untested
		/*if (screen.getKeyboard() != null){
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
		}*/
		if (screen.getKeyboard() != null){
		if(screen.getKeyboard().getKey(0x1E)){
			System.out.println("Attack");
			for(Player p: players.players()){
				p.attack();
			}
//		}else if(screen.getKeyboard().getKey(Keyboard.KEY_C)){
//			System.out.println("Call");
//			for(Player p: players){
//				p.call();
//			}
		} 
	}
	}

	public Stats stats(UUID objectId){
		if (!dataMap.containsKey(objectId)){
			return null;
		}
		return dataMap.get(objectId).clone();
	}
	
	public boolean isBusyThisTurn(Sect s){
		return busyThisTurn.contains(s) || frozenThisTurn.contains(s);
	}
	
	protected Stats moveTo(UUID objectId, Point position){
		Stats stats = dataMap.get(objectId);
		stats.position = position;
		return stats;
	}
	
	protected Stats changeStats(EnvironmentObject object, Stats diff){
		Stats stats = dataMap.get(object.id());
		stats.energy += diff.energy;
		stats.attackCoolDown += diff.attackCoolDown;
		stats.busyCoolDown += diff.busyCoolDown;
		return stats;
	}
	
	public void moveTo(Sect sect, Point dir) {
		if(!isBusyThisTurn(sect)){
			mover.moveTo(sect, dir);
		}
	}
	
	public void attack(Sect sect) {
		attack.add(sect);
	}
	
	public void mate(Sect sect) {
		mate.add(sect);
	}
	
	public void markAsBusy(Sect s){
		busyThisTurn.add(s);
	}
	
	protected Set<Sect> busy(){
		return busyThisTurn;
	}
	
	protected Set<Sect> frozen(){
		return frozenThisTurn;
	}
	
	protected void freeze(Sect sect) {
		frozenThisTurn.add(sect);
	}
	
	protected void unfreeze(Sect sect) {
		frozenThisTurn.remove(sect);
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
		add(n, new Stats(position,nutrientEnergy)); 
		return n;
	}
	
	public Corpse addCorpse(Point position) {
		Corpse c = new Corpse();
		this.add(c, new Stats(position,corpseEnergy)); 
		return c;
	}
	
	public Sect addSect(Sect s, Point position) {
		add(s, new Stats(position, initialEnergy));
		return s;
	}
	
	public Player addPlayer(Player p, Point position) {
		p.setEnv(this);
		this.add(p, new Stats(position, 0));
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
		for(Player p :players.players()){
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
		
		public Stats attackCoolDown(int attackCoolDown) {
			this.attackCoolDown = attackCoolDown;
			return this;
		}
		
		public Stats busyCoolDown(int busyCoolDown) {
			this.busyCoolDown = busyCoolDown;
			return this;
		}
	}
}

interface EnvironemtObjectManager{
	public EnvironmentObject add(EnvironmentObject o);
	public void update();
}