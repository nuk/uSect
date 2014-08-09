package org.unbiquitous.games.uSect.environment;

public abstract class Random{
	private static double value = -1;
	private static java.util.Random r = new java.util.Random();
	
	public static void setvalue(double value) {
		Random.value = value;
	}
	
	public static void setSeed(long seed) {
		r = new java.util.Random(seed);
	}
	
	public static double v(){
		if(value >=0 ){
			return value;
		}
		return r.nextDouble();
	}
}