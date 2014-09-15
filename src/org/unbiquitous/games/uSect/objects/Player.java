package org.unbiquitous.games.uSect.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.asset.Rectangle;
import org.unbiquitous.uImpala.engine.asset.SimetricShape;
import org.unbiquitous.uImpala.engine.core.GameSingletons;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.math.Point;
import org.unbiquitous.uos.core.UOSLogging;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.adaptabitilyEngine.ServiceCallException;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDevice;
import org.unbiquitous.uos.core.messageEngine.messages.Call;

public class Player extends EnvironmentObject{
	private static final Logger LOGGER = UOSLogging.getLogger();
	private static final Color PLAYER_PAINT = new Color(142, 68, 173);
	private static final Color ATTACK_PAINT = new Color(192, 57, 43,128);

	private Rectangle square ;
	private SimetricShape inlfuence ;
	private int influenceRadius = 0;
	private BaseAction currentAction = new BaseAction();
	private boolean growingInfluence;
	
	private int maxInfluenceRadius ;
	private int influenceGrowthSpeed;
	
	private GameSettings settings;
	private Gateway gateway;
	private Set<UpDevice> connectedDevices = new HashSet<UpDevice>();
	private Set<Sect> toMigrate = new HashSet<Sect>();
	
	public Player() {
		this(UUID.randomUUID());
	}
	
	public Player(UUID id){
		super(id);
		AssetManager assets = GameSingletons.get(AssetManager.class);
		square = assets.newRectangle(new Point(0,0), PLAYER_PAINT, 40, 40);
		inlfuence = assets.newCircle(new Point(0,0), ATTACK_PAINT, 40);
		settings = GameSingletons.get(GameSettings.class);
		maxInfluenceRadius = settings.getInt("usect.player.influence.radius",300);
		influenceGrowthSpeed = settings.getInt("usect.player.influence.speed",5);
		gateway = GameSingletons.get(Gateway.class);
	}
	
	public void setEnv(Environment env) {
		this.env = env;
	}
	
	@Override
	public void render(GameRenderers renderers) {
		inlfuence.center(position());
		inlfuence.radius(influenceRadius);
		inlfuence.color(ATTACK_PAINT);
		inlfuence.render();
		
		square.center(position());
		square.render();
	}
	
	@Override
	public  void update() {
		currentAction.update();
		if(!toMigrate.isEmpty()){
			for(Sect s : toMigrate){
				if(!connectedDevices.isEmpty()){
					UpDevice target = connectedDevices.iterator().next();
					try {
						env.markRemoval(s);
						Call c = new Call("usect.driver","migrate")
										.addParameter("sect", s.toJSON());
						gateway.callService(target, c);
					} catch (ServiceCallException e) {
						LOGGER.log(Level.WARNING, "Not possible to migrate sect "+s+" to device "+target, e);
					}
				}
			}
			toMigrate.clear();
		}
	}

	public void call(){
		currentAction = new CallAction();
		Call call = new Call("usect.driver","call")
								.addParameter("id", id().toString());
		if(settings.containsKey("usect.player.id")){
			for(UpDevice d : connectedDevices){
				try {
					gateway.callService(d, call);
				} catch (ServiceCallException e) {
					LOGGER.log(Level.SEVERE, "Could not send call.", e);
				}
			}
		}
	}
	
	public void onCapture(Sect s){
		toMigrate.add(s);
	}
	
	public int influenceRadius(){
		return influenceRadius;
	}

	public boolean growingInfluence() {
		return growingInfluence;
	}
	
	
	class BaseAction{
		void update(){}
	}
	
	class CallAction extends BaseAction{
		public CallAction() {
			growingInfluence = true;
		}
		
		@Override
		void update() {
			if(growingInfluence && influenceRadius < maxInfluenceRadius){
				influenceRadius += influenceGrowthSpeed;
			}else if (influenceRadius >= 0){
				growingInfluence = false;
				influenceRadius -= influenceGrowthSpeed;
			}else{
				currentAction  = new BaseAction();
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("Player[%s@%s]", id().getLeastSignificantBits(),position());
	}

	public void connect(UpDevice device) {
		connectedDevices.add(device);
	}
	
	public Set<UpDevice> connectedDevices(){
		return connectedDevices;
	}
}
