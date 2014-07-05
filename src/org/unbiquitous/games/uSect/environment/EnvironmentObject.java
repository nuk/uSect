package org.unbiquitous.games.uSect.environment;

import java.util.UUID;

import org.unbiquitous.uImpala.engine.core.GameObject;

public abstract class EnvironmentObject extends GameObject {
	protected UUID id = UUID.randomUUID();

	protected void update() {
	}

	protected void wakeup(Object... args) {
	}

	protected void destroy() {
	}

	public boolean equals(Object obj) {
		if (obj instanceof EnvironmentObject) {
			return ((EnvironmentObject) obj).id.equals(this.id);
		}
		return false;
	}

	public UUID id(){ return id;}

	public int hashCode() {
		return id.hashCode();
	}

}