package org.unbiquitous.games.uSect;

import java.awt.Point;
import java.util.UUID;

class Something {
	enum Type {NUTRIENT,SECT}
	
	private UUID id; 
	private Point center;
	private Type type;
	
	public Something(UUID id, Point center, Type type) {
		super();
		this.id = id;
		this.center = center;
		this.type = type;
	}

	public UUID id() {		return id;}
	public Point center() {	return center;}
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