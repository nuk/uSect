package org.unbiquitous.games.usect;

import java.util.Arrays;

import org.unbiquitous.games.uSect.Starter;
import org.unbiquitous.uImpala.dalvik.GameActivity;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.ScreenManager;

import android.os.Bundle;

public class LaunchActivity extends GameActivity{
	@Override
	@SuppressWarnings({ "serial", "unchecked" })
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        run(new GameSettings() {
			{ // TODO: Game Settings could have helper methods
				put("main_activity", LaunchActivity.this);
				put("first_scene", Starter.class);
				put("input_managers", Arrays.asList(MouseManager.class));
				put("output_managers", Arrays.asList(ScreenManager.class));
			}
		});
	}
}
