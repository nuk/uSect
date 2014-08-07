package org.unbiquitous.games.uSect.objects;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.asset.Rectangle;
import org.unbiquitous.uImpala.engine.asset.SimetricShape;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.math.Point;

public class Player extends EnvironmentObject{
	
	private static final Color PLAYER_PAINT = new Color(142, 68, 173);
	private static final Color ATTACK_PAINT = new Color(192, 57, 43,128);

	private Rectangle square ;
	private SimetricShape inlfuence ;
	private int influenceRadius = 0;
	private BaseAction currentAction = new BaseAction();
	private boolean growingInfluence;
	
	public Player() {
		AssetManager assets = GameComponents.get(AssetManager.class);
		square = assets.newRectangle(new Point(0,0), PLAYER_PAINT, 40, 40);
		inlfuence = assets.newCircle(new Point(0,0), ATTACK_PAINT, 40);
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
	}

	public void call(){
		currentAction = new AttackAction();
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
	
	class AttackAction extends BaseAction{
		public AttackAction() {
			growingInfluence = true;
		}
		
		@Override
		void update() {
			if(growingInfluence && influenceRadius <300){
				influenceRadius += 5;
			}else if (influenceRadius >= 0){
				growingInfluence = false;
				influenceRadius -= 5;
			}else{
				currentAction  =new BaseAction();
			}
		}
	}
}
