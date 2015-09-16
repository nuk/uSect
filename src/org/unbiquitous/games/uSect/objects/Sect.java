package org.unbiquitous.games.uSect.objects;

import static java.lang.String.format;

import java.util.UUID;

import org.unbiquitous.driver.execution.executionUnity.ExecutionUnity;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.environment.Environment.Stats;
import org.unbiquitous.games.uSect.environment.EnvironmentObject;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.games.uSect.objects.Something.Feeding;
import org.unbiquitous.games.uSect.objects.behavior.Artificial;
import org.unbiquitous.games.uSect.objects.behavior.Carnivore;
import org.unbiquitous.games.uSect.objects.behavior.Herbivore;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.asset.SimetricShape;
import org.unbiquitous.uImpala.engine.asset.Text;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.core.GameSingletons;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.math.Point;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sect extends EnvironmentObject {
	private static final Color HERBIVORE_COLOR = new Color(41, 128, 185, 200);

	private static final Color CARNIVOROUS_COLOR = new Color(211, 84, 0, 200);

	private static final Color ATTACK_PAINT = new Color(192, 57, 43, 128);

	private static final ObjectMapper mapper = new ObjectMapper();

	private Behavior behavior;
	protected Point currentDir;
	private long angle = 0;

	private int radius = 30;
	private SimetricShape shape;
	private SimetricShape influence;
	private SimetricShape mating;
	protected Text text;
	private int influenceRadius = 50;

	public interface Behavior extends Cloneable {
		public Something.Feeding feeding();

		public void init(Sect s);

		public void update();

		public void enteredViewRange(Something o);

		public void leftViewRange(Something o);

		public Behavior clone();
	}

	public Sect() {
		this(new Herbivore());
	}

	public Sect(Behavior behavior) {
		this(behavior, UUID.randomUUID());
	}

	public Sect(Behavior behavior, UUID id) {
		super(id);
		//		Font font = new Font("Verdana", Font.BOLD, 12);
		AssetManager assets = GameSingletons.get(AssetManager.class);
		if (assets != null) {
			//			text = assets.newText(font, "");
		}
		if (behavior instanceof Carnivore) {
			shape = assets.newSimetricShape(new Point(), CARNIVOROUS_COLOR, radius, 3);
		} else if (behavior instanceof Artificial) {
			Color color = CARNIVOROUS_COLOR;
			if (behavior.feeding().equals(Feeding.HERBIVORE)) {
				color = HERBIVORE_COLOR;
			}
			shape = assets.newSimetricShape(new Point(), color, radius, 4);
		} else {
			shape = assets.newSimetricShape(new Point(), HERBIVORE_COLOR, radius, 7);
		}
		influence = assets.newCircle(new Point(), ATTACK_PAINT, influenceRadius);
		mating = assets.newSimetricShape(new Point(), ATTACK_PAINT, influenceRadius, 13);

		this.behavior = behavior;
		behavior.init(this);
	}

	public int radius() {
		return radius;
	}

	public int influenceRadius() {
		return influenceRadius;
	}

	public Behavior behavior() {
		return behavior;
	}

	public void update() {
		behavior.update();
	}

	public void enteredSight(Something o) {
		behavior.enteredViewRange(o);
	}

	public void leftSight(Something o) {
		behavior.leftViewRange(o);
	}

	public void moveTo(Point dir) {
		currentDir = dir;
		env.moveTo(this, dir);
	}

	public void attack() {
		env.attack(this);
	}

	public void mate() {
		env.mate(this);
	}

	public Point positionOf(UUID id) {
		return env.stats(id).position;
	}

	public void render(GameRenderers renderers) {
		if (env.stats(id()).attackCoolDown > 0) {
			influence.radius(influenceRadius * env.stats(id()).attackCoolDown / 5);
			influence.center(position());
			influence.render();
		}

		if (env.stats(id()).busyCoolDown > 0) {
			mating.radius(influenceRadius * env.stats(id()).busyCoolDown / 50 + radius);
			mating.center(position());
			mating.render();
		}

		shape.center(position());
		shape.radius(radius + (int) ((Random.v() * 6) - 3));
		shape.rotate(rotationAngle());
		shape.render();

		//		Screen screen = GameSingletons.get(Screen.class);
		//		text.setText(energy().toString());
		//		text.render(screen, (float)position().x, (float)position().y, Corner.TOP_LEFT, 1f, 0f, 1f, 1f, new org.unbiquitous.uImpala.util.Color(0, 0, 0));
	}

	private float rotationAngle() {
		angle += ((Random.v() * 3) - 2) * 45;
		return angle %= 360;
	}

	public String toString() {
		return format("Sect:%s@%s[e=%s]", behavior.feeding(), position(), energy());
	}

	public static Sect fromJSON(Environment e, JsonNode json) {
		return fromJSON(e, json, null);
	}

	public static Sect fromJSON(Environment e, JsonNode json, Point position) {
		UUID id = UUID.fromString(optString(json, "id"));
		Sect s = new Sect(deserializeBehavior(json), id);
		if (position == null) {
			Screen screen = GameSingletons.get(Screen.class);
			int x = (int) (Math.random() * screen.getWidth());
			int y = (int) (Math.random() * screen.getHeight());
			position = new Point(x, y);
		}
		Stats stats = new Stats(position, optLong(json, "energy"));
		return (Sect) e.add(s, stats);
	}

	private static Behavior deserializeBehavior(JsonNode json) {
		Behavior behavior;
		if ("Carnivore".equalsIgnoreCase(optString(json, "behavior"))) {
			behavior = new Carnivore();
		} else if ("Artificial".equalsIgnoreCase(optString(json, "behavior"))) {
			behavior = deserializeArtificialBehavior(json);
		} else {
			behavior = new Herbivore();
		}
		return behavior;
	}

	private static Behavior deserializeArtificialBehavior(JsonNode json) {
		Behavior behavior;
		Feeding feeding = Feeding.valueOf(optString(json, "feeding"));
		ExecutionUnity unity = ExecutionUnity.fromJSON(json.get("execution"));
		behavior = new Artificial(unity, feeding);
		return behavior;
	}

	public ObjectNode toJSON() {
		ObjectNode json = mapper.createObjectNode();
		json.put("id", id().toString());
		json.put("behavior", behavior().getClass().getSimpleName());
		if (behavior instanceof Artificial) {
			json.put("feeding", behavior().feeding().toString());
			json.set("execution", ((Artificial) behavior()).unity().toJSON());
		}
		json.put("energy", energy());
		return json;
	};

	private static String optString(JsonNode json, String key) {
		JsonNode n = json.get(key);
		if (n != null)
			return n.asText();
		return null;
	}

	private static long optLong(JsonNode json, String key) {
		JsonNode n = json.get(key);
		if (n != null)
			return n.asLong();
		return 0;
	}
}
