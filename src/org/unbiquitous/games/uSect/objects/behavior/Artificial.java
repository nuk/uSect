package org.unbiquitous.games.uSect.objects.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.unbiquitous.driver.execution.executionUnity.ExecutionError;
import org.unbiquitous.driver.execution.executionUnity.ExecutionUnity;
import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.games.uSect.objects.Something.Feeding;
import org.unbiquitous.uImpala.util.math.Point;

public class Artificial extends TargetFocused {
	private ExecutionUnity unity;

	public Artificial(ExecutionUnity unity, Feeding feeding) {
		this.unity = unity;
		unity.addHelper(new MoveHelper());
		unity.addHelper(new AttackHelper());
		unity.addHelper(new MateHelper());
		unity.addHelper(new PositionHelper());
	}

	@Override
	public Feeding feeding() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void update() {
		setState();
		unity.call("update");
	}

	@SuppressWarnings("serial")
	private void setState() {
		unity.setState("energy", sect.energy());
		Map<String, Integer> position = new HashMap<String, Integer>(){{
			Point current = sect.position();
			put("x",current.x);
			put("y",current.y);
		}};
		unity.setState("position", position);
	}
	
	@Override
	public void enteredViewRange(final Something o) {
		try {
			unity.call("onEntered", somethingToMap(o));
		} catch (ExecutionError e) {
			// If you don't want to receive, that's not my problem 
		}
	}

	@Override
	public void leftViewRange(Something n) {
		try {
			unity.call("onLeft", somethingToMap(n));
		} catch (ExecutionError e) {
			// If you don't want to receive, that's not my problem 
		}
	}
	
	@SuppressWarnings("serial")
	private Map<String, Object> somethingToMap(final Something o) {
		Map<String, Object> something = new HashMap<String, Object>(){{
			put("id",o.id());
			put("type",o.type().toString());
			put("feeding",o.feeding().toString());
		}};
		return something;
	}

	private final class MateHelper implements ExecutionUnity.ExecutionHelper {
		public String name() {	return "mate";	}

		public String invoke(String... args) {
			sect.mate();
			return null;
		}
	}

	private final class AttackHelper implements ExecutionUnity.ExecutionHelper {
		public String name() {	return "attack";	}

		public String invoke(String... args) {
			sect.attack();
			return null;
		}
	}

	private final class MoveHelper implements ExecutionUnity.ExecutionHelper {
		public String name() {	return "move";	}

		public String invoke(String... args) {
			sect.moveTo(toPoint(args));
			return null;
		}

		private Point toPoint(String... args) {
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			Point poi9nt = new Point(x,y);
			return poi9nt;
		}
	}
	
	private final class PositionHelper implements ExecutionUnity.ExecutionHelper {
		public String name() {	return "positionOf";	}
		
		public Object invoke(final String... args) {
			Map<String, Object> something = new HashMap<String, Object>(){{
				Point current = sect.positionOf(UUID.fromString(args[0]));
				put("x",current.x);
				put("y",current.y);
			}};
			return something;
//			System.out.println(args[0]);
//			Point current = sect.positionOf(UUID.fromString(args[0]));
//			System.out.println(current);
//			return "{'x' = "+current.x+", 'y' = "+current.y+"}";
////			System.out.println(args);
////			return "100";
		}
	}
}
