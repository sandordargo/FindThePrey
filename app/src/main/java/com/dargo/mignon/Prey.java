package com.dargo.mignon;

public class Prey {
	public boolean myIsGenerated = false;
	public boolean myIsFound = false;
	public int myXCoordinate;
	public int myYCoordinate;
	public int myScreenWidth;
	public int myScreenHeight;
	public double myMaxDistance;
	
	public Prey(int iXCoordinate, int iYCoordinate, int iScreenWidth, int iScreenHeight) {
		this.myIsGenerated = true;
		this.myXCoordinate = iXCoordinate;
		this.myYCoordinate = iYCoordinate;
		this.myScreenWidth = iScreenWidth;
		this.myScreenHeight = iScreenHeight;
		this.myMaxDistance = calculateMaxDistance();
	}
	
	public double calculateMaxDistance() {
		//calculate distance of duck and maxdistance
		//d = sqrt (sqr(x2-x1) + sqr(y2-y1))
		double cornerDistance [] = new double [4];
		cornerDistance[0] = Math.sqrt(Math.pow(this.myXCoordinate - 0, 2) + Math.pow(this.myYCoordinate - 0, 2));
		cornerDistance[1] = Math.sqrt(Math.pow(this.myXCoordinate - 0, 2) + Math.pow(this.myYCoordinate - this.myScreenHeight, 2));
		cornerDistance[2] = Math.sqrt(Math.pow(this.myXCoordinate - this.myScreenWidth, 2) + Math.pow(this.myYCoordinate - this.myScreenHeight, 2));
		cornerDistance[3] = Math.sqrt(Math.pow(this.myXCoordinate -this. myScreenWidth, 2) + Math.pow(this.myYCoordinate - 0, 2));
		
		double maxVal = cornerDistance[0];
		
		for (int i = 1; i < 4; i++) {
			if (cornerDistance[i] > maxVal)	{
				maxVal = cornerDistance[i]; 
			}
		}
		
		return maxVal;
	}
}
