package org.unbiquitous.games.uSect.environment;

import java.awt.Point;

import org.unbiquitous.games.uSect.Sect;

class MovementManager {
	private Environment env;
	private RandomGenerator random;
	
	public MovementManager(Environment env, RandomGenerator random) {
		super();
		this.env = env;
		this.random = random;
	}

	public void moveTo(Sect sect, Point dir) {
		adjustDirection(dir);
		env.stats(sect.id).position = determineFinalPosition(sect, dir); 
	}

	private void adjustDirection(Point dir) {
		double lottery = random.v();
		if(lottery > 0.5 && dir.x != 0){
			dir.y = 0;
		}else if (lottery <= 0.5 && dir.y != 0){
			dir.x = 0;
		}
	}
	
	private Point determineFinalPosition(Sect sect, Point dir) {
		Point forwardPosition = new Point(sect.center().x + dir.x, sect.center().y + dir.y);
		if(!hasColided(sect, forwardPosition)){
			return forwardPosition;
		}else if (random.v() > 0.5){
			Point backwardsPosition = new Point(sect.center().x - dir.x, sect.center().y - dir.y);
			return backwardsPosition;
		}
		return sect.center();
	}


	private boolean hasColided(Sect sect, Point newPos) {
		boolean hasColided = false;
		for(Sect s: env.sects()){
			if(!sect.equals(s) && distanceOf(s.center(), newPos) < sect.radius()){
				hasColided = true;
			}
		}
		return hasColided;
	}

	private int distanceOf(Point origin, Point desttination) {
		return Math.abs(origin.x-desttination.x) + Math.abs(origin.y-desttination.y);
	}
}