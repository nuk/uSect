package org.unbiquitous.games.uSect.environment;

import java.util.ArrayList;
import java.util.List;

import org.unbiquitous.games.uSect.objects.Player;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.uImpala.util.math.Point;

public class PlayerManager implements EnvironemtObjectManager {
	private List<Player> players = new ArrayList<Player>();
	private Environment env;
	
	public PlayerManager(Environment env) {
		this.env = env;
	}
	
	@Override
	public EnvironmentObject add(EnvironmentObject o) {
		if(o instanceof Player){
			players.add((Player) o);
		}
		return o;
	}

	@Override
	public void update() {
		env.frozen().clear();
		for(Player p : players){
			update(p);
		}
	}

	private void update(Player p) {
		p.update();
		checkInfuence(p);
	}

	private void checkInfuence(Player p) {
		for(Sect s : env.sects()){
			checkInfluence(p, s);
		}
	}

	private void checkInfluence(Player p, Sect s) {
		if(isUnderInfluence(p, s)){
			env.freeze(s);
			if(isReturning(p)){
				moveTowardPlayer(p, s);
			}
		}
	}

	private boolean isUnderInfluence(Player p, Sect s) {
		int distance = p.position().distanceTo(s.position());
		return  distance < p.influenceRadius();
	}

	private boolean isReturning(Player p) {
		return !p.growingInfluence();
	}
	
	private void moveTowardPlayer(Player p, Sect s) {
		env.unfreeze(s);
		env.moveTo(s,playerDirection(p, s) );
		env.freeze(s);
	}

	private Point playerDirection(Player p, Sect s) {
		return new Point(
				p.position() .x -  s.position().x,
				p.position() .y-  s.position().y
				);
	}

	public List<Player> players() {
		return players;
	}
}
