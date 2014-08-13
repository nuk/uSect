package org.unbiquitous.games.uSect.objects.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.unbiquitous.driver.execution.executionUnity.ExecutionError;
import org.unbiquitous.driver.execution.executionUnity.ExecutionUnity;
import org.unbiquitous.games.uSect.objects.Sect.Behavior;
import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.games.uSect.objects.Something.Feeding;
import org.unbiquitous.uImpala.util.math.Point;

public class Artificial extends TargetFocused {
	private ExecutionUnity unity;
	private Feeding feeding;

	public Artificial(ExecutionUnity unity, Feeding feeding) {
		this.unity = unity;
		this.feeding = feeding;
		unity.addHelper(new MoveHelper());
		unity.addHelper(new AttackHelper());
		unity.addHelper(new MateHelper());
		unity.addHelper(new PositionHelper());
	}

	@Override
	public Feeding feeding() {
		return feeding;
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

	public Behavior clone(){
		ExecutionUnity copy = ExecutionUnity.fromJSON(unity.toJSON());
		return new Artificial(copy, feeding); 
	}
	
	private final class MateHelper implements ExecutionUnity.ExecutionHelper {
		public String name() {	return "mate";	}

		public Object invoke(String... args) {
			sect.mate();
			return null;
		}
	}

	private final class AttackHelper implements ExecutionUnity.ExecutionHelper {
		public String name() {	return "attack";	}

		public Object invoke(String... args) {
			sect.attack();
			return null;
		}
	}

	private final class MoveHelper implements ExecutionUnity.ExecutionHelper {
		public String name() {	return "move";	}

		public Object invoke(String... args) {
			sect.moveTo(toPoint(args));
			return null;
		}

		private Point toPoint(String... args) {
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			return new Point(x,y);
		}
	}
	
	private final class PositionHelper implements ExecutionUnity.ExecutionHelper {
		public String name() {	return "positionOf";	}
		
		@SuppressWarnings("serial")
		public Object invoke(final String... args) {
			Map<String, Object> something = new HashMap<String, Object>(){{
				Point current = sect.positionOf(UUID.fromString(args[0]));
				put("x",current.x);
				put("y",current.y);
			}};
			return something;
		}
	}
	
	public ExecutionUnity unity(){
		return unity;
	}
}
