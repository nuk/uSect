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
	private static final Color CALL_PAINT = new Color(26, 188, 156,128);

	enum Influence {ATTACK,CALL}
	private Rectangle square ;
	private SimetricShape inlfuence ;
	private int influenceRadius = 0;
	private Influence currentAction = Influence.ATTACK;
	
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
		if(influenceRadius != 0){
			influenceRadius+=5;
			int praticalRadius = (int) (influenceRadius*Math.sin(Math.toRadians(influenceRadius)));
			inlfuence.radius(praticalRadius);
			inlfuence.render();
		}
		if(currentAction.equals(Influence.CALL)){
			inlfuence.color(CALL_PAINT);
		}else{
			inlfuence.color(ATTACK_PAINT);
		}
		if(influenceRadius > 720){
			influenceRadius = 0;
		}
		square.center(position());
		square.render();
	}

	public void attack(){
		if (influenceRadius == 0){
			influenceRadius = 40;
			currentAction = Influence.ATTACK;
		}
	}
	
	public void call(){
		if (influenceRadius == 0){
			influenceRadius = 40;
			currentAction = Influence.CALL;
		}
	}
	
}
