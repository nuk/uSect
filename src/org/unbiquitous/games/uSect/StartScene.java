package org.unbiquitous.games.uSect;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.unbiquitous.driver.execution.executionUnity.ExecutionUnity;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Environment.Stats;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.games.uSect.objects.Player;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.games.uSect.objects.Something.Feeding;
import org.unbiquitous.games.uSect.objects.behavior.Artificial;
import org.unbiquitous.games.uSect.objects.behavior.Carnivore;
import org.unbiquitous.games.uSect.objects.behavior.Herbivore;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObjectTreeScene;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.engine.time.DeltaTime;
import org.unbiquitous.uImpala.util.math.Point;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;

public class StartScene extends GameObjectTreeScene {

	private Screen screen;
	private Environment env;

	public StartScene() {
		DeltaTime deltaTime = GameComponents.get(DeltaTime.class);
		deltaTime.setUPS(30);
		
		screen = GameComponents.get(ScreenManager.class).create();
		GameSettings settings = GameComponents.get(GameSettings.class);
		
		if(settings.containsKey("usect.width") && settings.containsKey("usect.height")){
			screen.open("uSect", 
								settings.getInt("usect.width"), settings.getInt("usect.height"), 
								false, null);
		}else{
			screen.open();
		}
		
		GameComponents.put(Screen.class, screen);
		GameComponents.put(AssetManager.class,assets());
		setUpEnvironment(settings);
		
		Gateway gateway = GameComponents.get(Gateway.class);
		gateway.addDriver(new USectDriver(settings,env));
		
//		populateEnvironment(e);
//		
//		e.addPlayer(new Player(), new Point(screen.getWidth()/2,screen.getHeight()));
//		e.addPlayer(new Player(), new Point(screen.getWidth()/2,0));
		
	}

	private void setUpEnvironment(GameSettings settings) {
		env = new Environment((DeviceStats) settings.get("usect.devicestats"));
		populateSects(settings, env);
		populatePlayer(settings, env);
		add(env);
	}

	@SuppressWarnings("unchecked")
	private void populateSects(GameSettings settings, Environment e) {
		int numberOfHerbivores = (int) (Random.v()*10)+5;
		for(int i = 0 ; i < numberOfHerbivores; i++){
			Point position = new Point(
					(int)(Random.v()*screen.getWidth()),
					(int)(Random.v()*screen.getHeight())
					);
			e.addSect(new Sect(new Herbivore()), position);
		}
		int numberOfCarnivores = 1;
		for(int i = 0 ; i < numberOfCarnivores; i++){
			Point position = new Point(
					(int)(Random.v()*screen.getWidth()),
					(int)(Random.v()*screen.getHeight())
					);
			e.add(new Sect(new Carnivore()), new Stats(position,settings.getInt("usect.initial.energy",30*60*10)*4));
		}
		if(settings.containsKey("usect.artificials")){
			Object value = settings.get("usect.artificials");
			List<String> scripts;
			if(value instanceof List){
				scripts = (List<String>) value;
			}else{
				scripts = Arrays.asList(value.toString());
			}
			for(String script: scripts){
				Point position = new Point(
						(int)(Random.v()*screen.getWidth()),
						(int)(Random.v()*screen.getHeight())
						);
				ExecutionUnity behavior = new ExecutionUnity(script);
				Feeding type = Feeding.CARNIVORE;
				if(Random.v() > 0.5){
					type = Feeding.HERBIVORE;
				}
				e.add(new Sect(new Artificial(behavior, type)), new Stats(position,settings.getInt("usect.initial.energy",30*60*10)*4));
			}
		}
	}

	private void populatePlayer(GameSettings settings, Environment e) {
		if(settings.containsKey("usect.player.id")){
			UUID id = UUID.fromString(settings.getString("usect.player.id"));
			e.addPlayer(new Player(id));
		}
	}
	
	private void populateEnvironment(Environment e) {
		populateHerbivores(e);
		populateCarnivores(e);
		StringBuilder crazyScript = new StringBuilder()
		.append("targetPosition = nil\n")
		.append("knownPeople = {}\n")
		.append("attackMode = false\n")
		.append("function update()\n")
		.append("	changeTarget()\n")
		.append("	if targetPosition == nil then\n")
		.append("		targetPosition = position\n")
		.append("	end\n")
		.append("	if(	targetPosition['x'] == position['x'] and \n")
		.append("		targetPosition['y'] == position['y']  )then\n")
		.append("		targetPosition['x'] = position['x'] + math.random(-20,20)\n")
		.append("		targetPosition['y'] = position['y'] + math.random(-20,20)\n")
		.append("		attackMode = false\n")
		.append("	end\n")			
		.append("	x = targetPosition['x'] - position['x']\n")
		.append("	y = targetPosition['y'] - position['y']\n")
		.append("	move(x,y)\n")
		.append("	if attackMode then\n")
		.append("		attack()\n")
		.append("	end \n")
		.append("end\n")
		.append("function onEntered(data)\n")
		.append("	table.insert(knownPeople, data['id'])\n")
		.append("end\n")
		.append("function onLeft(data)\n")
		.append("	for index, id in pairs(knownPeople) do\n")
		.append("		if id == data['id'] then\n")
		.append("			table.remove(knownPeople, index)\n")
		.append("		end\n")
		.append("	end\n")
		.append("end\n")
		.append("function distance(id)\n")
		.append("	p = positionOf(id)\n")
		.append("	return math.abs(position['x']-p['x'])+math.abs(position['y']-p['y'])")
		.append("end\n")
		.append("function changeTarget()\n")
		.append("	for index, id in pairs(knownPeople) do\n")
		.append("		if distance(id) < 100 then\n")
		.append("			targetPosition = positionOf(id)\n")
		.append("			attackMode = true\n")
		.append("		end\n")
		.append("	end\n")
		.append("end\n");
		Artificial crazyBehavior = new Artificial(new ExecutionUnity(crazyScript.toString()), Feeding.CARNIVORE);
		e.addSect(new Sect(crazyBehavior), new Point(screen.getWidth()/2,screen.getHeight()/2));
	}

	private void populateHerbivores(Environment e) {
		e.add(new Sect(new Herbivore()), new Stats(new Point(),100*1000));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth(),0));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth()/2,0));
		e.addSect(new Sect(new Herbivore()),new Point(0,screen.getHeight()));
		e.addSect(new Sect(new Herbivore()),new Point(0,screen.getHeight()/2));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth(),screen.getHeight()));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth()/2,screen.getHeight()));
		e.addSect(new Sect(new Herbivore()),new Point(screen.getWidth(),screen.getHeight()/2));
	}
	
	private void populateCarnivores(Environment e) {
		e.add(new Sect(new Carnivore()),new Stats(new Point(screen.getWidth()/4,screen.getHeight()/4),100*1000));
		e.addSect(new Sect(new Carnivore()),new Point(3*screen.getWidth()/4,3*screen.getHeight()/4));
	}
	
	@Override
	public void update() {
		super.update();
		if (screen.isCloseRequested()) {
			GameComponents.get(org.unbiquitous.uImpala.engine.core.Game.class).quit();
		}
	}
}
