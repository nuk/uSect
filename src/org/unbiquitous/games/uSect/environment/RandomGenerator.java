package org.unbiquitous.games.uSect.environment;

public class RandomGenerator{
	private double value = -1;

	public void setvalue(double value) {
		this.value = value;
	}
	
	public double v(){
		if(value >=0 ){
			return value;
		}
		return Math.random();
	}
}