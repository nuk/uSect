package uPrism;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.core.GameScene;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.engine.time.DeltaTime;
import org.unbiquitous.uImpala.jse.impl.core.Game;
import org.unbiquitous.uImpala.jse.util.shapes.Circle;
import org.unbiquitous.uImpala.jse.util.shapes.Rectangle;
import org.unbiquitous.uImpala.jse.util.shapes.SimetricShape;
import org.unbiquitous.uImpala.util.math.Point;

public class SpikeCanvas extends GameScene {

	private Screen screen;
	private Rectangle control;
	private SimetricShape control2;
	private List<SpikeSect> sects = new ArrayList<SpikeSect>();
	
	public SpikeCanvas() {
		DeltaTime deltaTime = GameComponents.get(DeltaTime.class);
		deltaTime.setUPS(60);
		
		
		screen = GameComponents.get(ScreenManager.class).create();
		screen.open("uSect", 800, 600, false, null);

		GameComponents.put(Screen.class, screen);
		
		control = new Rectangle(new Point(50,50),Color.WHITE, 100, 100);
		control2 = new SimetricShape(new Point(50,50), Color.DARK_GRAY, 100, 4);
		
		sects.add(new SpikeSect(new Point(100,100), screen));
		sects.add(new SpikeSect(new Point(200,200), screen));
		sects.add(new SpikeSect(new Point(300,300), screen));
		sects.add(new SpikeSect(new Point(400,400), screen));
		sects.add(new SpikeSect(new Point(500,500), screen));
		sects.add(new SpikeSect(new Point(600,600), screen));
		sects.add(new SpikeSect(new Point(700,700), screen));
	}
	
	int i = 0;
	int j = 0;
	@Override
	protected void update() {
		control.rotate(i++);
		control2.rotate(j--);
		for(SpikeSect sect : sects){
			sect.update();
		}
		if (screen.isCloseRequested()) {
			GameComponents.get(org.unbiquitous.uImpala.engine.core.Game.class).quit();
			System.exit(0);
		}
	}

	@Override
	public void render() {
		control.render();
		control2.render();
		for(SpikeSect sect : sects){
			sect.render(null);
		}
	}

	protected void wakeup(Object... args) {}

	protected void destroy() {}

	@SuppressWarnings({ "unchecked", "serial" })
	public static void main(String[] args) {
		Game.run(new GameSettings() {
			{ 
				put("first_scene", SpikeCanvas.class);
				put("input_managers", Arrays.asList(MouseManager.class));
				put("output_managers", Arrays.asList(ScreenManager.class));
			}
		});
	}
}

class SpikeSect extends GameObject {

	SimetricShape head;
	Rectangle body;
	SimetricShape tail;
	
	int componentSize = 20;
	private int triangleRadius;
	
	Point center;
	float angle = 0;
	
	private Point headPosition;
	private Point tailPosition;
	private Screen screen;
	
	public SpikeSect(Point center, Screen screen) {
		this.center = center;
		this.screen = screen;
		triangleRadius = (int) Math.ceil(Math.sqrt((4*componentSize*componentSize)/3));
		updatePositions();
		head = new SimetricShape(headPosition , Color.WHITE, triangleRadius,3);
		body = new Rectangle(center, Color.RED, componentSize, componentSize);
		tail = new Circle(tailPosition, Color.BLUE, componentSize);
		tail.rotate(45);
	}

	private void updatePositions() {
		headPosition = new Point(center.x, center.y-componentSize/2);
		tailPosition = new Point(center.x, center.y+componentSize);
	}
	
	@Override
	protected void update() {
		center.y-=1;
		if(center.y < -length()/2){
			center.y = screen.getHeight();
		}
		if(Math.random()*100 > 90){
			center.x += 1;
		}
		
		if(center.x > screen.getWidth()){
			center.x = componentSize/2;
		}
		angle++;
		updatePositions();
		head.center(headPosition);
		head.rotate(angle);
		body.center(center);
		tail.center(tailPosition);
	}

	public int length(){
		int triangleRadius = (int) Math.ceil(Math.sqrt((4*componentSize*componentSize)/3));
		return componentSize*2 + triangleRadius;
	}
	
	@Override
	protected void render(GameRenderers renderers) {
		body.render();
		tail.render();
		head.render();
	}

	@Override
	protected void wakeup(Object... args) {}

	@Override
	protected void destroy() {}
	
}
