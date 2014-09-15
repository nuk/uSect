package org.unbiquitous.games.uSect.objects;

import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.core.GameSingletons;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.math.Point;

public class Corpse extends Nutrient{

	public Corpse() {
		radius = 30;
		AssetManager assets = GameSingletons.get(AssetManager.class);
		shape = assets.newCircle(new Point(), Color.GRAY.darker(), radius);
		type = Something.Type.CORPSE;
	}
}
