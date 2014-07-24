package org.unbiquitous.games.uSect.objects.behavior;

import org.unbiquitous.driver.execution.executionUnity.ExecutionUnity;
import org.unbiquitous.games.uSect.objects.Something;
import org.unbiquitous.games.uSect.objects.Something.Feeding;
import org.unbiquitous.uImpala.util.math.Point;

public class Artificial extends TargetFocused {

	private ExecutionUnity unity;

	public Artificial(ExecutionUnity unity, Feeding feeding) {
		this.unity = unity;
		unity.addHelper(new ExecutionUnity.ExecutionHelper() {
			public String name() {	return "move";	}
			public String invoke(String... args) {
				int x = Integer.parseInt(args[0]);
				int y = Integer.parseInt(args[1]);
				sect.moveTo(new Point(x,y));
				return null;
			}
		});
		unity.addHelper(new ExecutionUnity.ExecutionHelper() {
			public String name() {	return "attack";	}
			public String invoke(String... args) {
				sect.attack();
				return null;
			}
		});
		unity.addHelper(new ExecutionUnity.ExecutionHelper() {
			public String name() {	return "mate";	}
			public String invoke(String... args) {
				sect.mate();
				return null;
			}
		});
	}

	@Override
	public void update() {
		unity.call("update");
	}
	
	@Override
	public Feeding feeding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enteredViewRange(Something o) {
		// TODO Auto-generated method stub
		
	}


}
