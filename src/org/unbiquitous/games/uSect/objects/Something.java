package org.unbiquitous.games.uSect.objects;

import java.awt.Point;
import java.util.UUID;

import org.unbiquitous.games.uSect.environment.Environment;

public class Something {
	public enum Type {NUTRIENT,SECT, CORPSE}
	
	private UUID id; 
	private Type type;
	private Environment env;
	
	public Something(UUID id, Environment env, Type type) {
		super();
		this.id = id;
		this.env = env;
		this.type = type;
	}

	public UUID id() {		return id;}
	public Point center() {	return env.position(id);}
	public Type type() {	return type;}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Something){
			return ((Something)obj).id.equals(this.id) ;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
}