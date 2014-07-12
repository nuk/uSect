package org.unbiquitous.games.uSect.environment;

public abstract class Random{
	private static double value = -1;

	public static void setvalue(double value) {
		Random.value = value;
	}
	
	public static double v(){
		if(value >=0 ){
			return value;
		}
		return Math.random();
	}
}