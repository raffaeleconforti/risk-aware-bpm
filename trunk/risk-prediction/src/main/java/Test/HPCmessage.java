package Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * Author: Raffaele Conforti 
 * Creation Date: 26/09/2013
 *
 */

public class HPCmessage implements Serializable{
	
	private String[] colno = null;
	private double[] row = null;
	
	private int RW = 0;
	private int RWW = 0;
	private int RW2S = 0;
	private int X = 0;
	private long max = 0L;

	private ArrayList<Constrain> constrain1 = null;

	private ArrayList<Constrain> constrain2a = null;
	
	private ArrayList<Constrain> constrain2b = null;
	
	private ArrayList<Constrain> constrain3a = null;
	
	private ArrayList<Constrain> constrain3b = null;
	
	private ArrayList<Constrain> constrain3c = null;
	
	private ArrayList<Constrain> constrain3d = null;

	private ArrayList<Constrain> constrain4 = null;

	private double[] solution = null;
	
	public double[] getSolution() {
		return solution;
	}

	public void setSolution(double[] solution) {
		this.solution = solution;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}
	
	public int getRW() {
		return RW;
	}

	public void setRW(int rW) {
		RW = rW;
	}

	public int getRWW() {
		return RWW;
	}

	public void setRWW(int rWW) {
		RWW = rWW;
	}

	public int getRW2S() {
		return RW2S;
	}

	public void setRW2S(int rW2S) {
		RW2S = rW2S;
	}

	public int getX() {
		return X;
	}

	public void setX(int x) {
		X = x;
	}

	public String[] getColno() {
		return colno;
	}
	
	public void setColno(String[] colno) {
		this.colno = colno;
	}
	
	public double[] getRow() {
		return row;
	}
	
	public void setRow(double[] row) {
		this.row = row;
	}
	
	public ArrayList<Constrain> getConstrain1() {
		return constrain1;
	}
	
	public void setConstrain1(LinkedList<Constrain> constrain1) {
		this.constrain1 = new ArrayList<Constrain>(constrain1);
	}
		
	public ArrayList<Constrain> getConstrain2a() {
		return constrain2a;
	}
	
	public void setConstrain2a(LinkedList<Constrain> constrain2a) {
		this.constrain2a = new ArrayList<Constrain>(constrain2a);
	}
	
	public ArrayList<Constrain> getConstrain2b() {
		return constrain2b;
	}
	
	public void setConstrain2b(LinkedList<Constrain> constrain2b) {
		this.constrain2b = new ArrayList<Constrain>(constrain2b);
	}
		
	public ArrayList<Constrain> getConstrain3a() {
		return constrain3a;
	}
	
	public void setConstrain3a(LinkedList<Constrain> constrain3a) {
		this.constrain3a = new ArrayList<Constrain>(constrain3a);
	}
	
	public ArrayList<Constrain> getConstrain3b() {
		return constrain3b;
	}
	
	public void setConstrain3b(LinkedList<Constrain> constrain3b) {
		this.constrain3b = new ArrayList<Constrain>(constrain3b);
	}
	
	public ArrayList<Constrain> getConstrain3c() {
		return constrain3c;
	}
	
	public void setConstrain3c(LinkedList<Constrain> constrain3c) {
		this.constrain3c = new ArrayList<Constrain>(constrain3c);
	}
	
	public ArrayList<Constrain> getConstrain3d() {
		return constrain3d;
	}
	
	public void setConstrain3d(LinkedList<Constrain> constrain3d) {
		this.constrain3d = new ArrayList<Constrain>(constrain3d);
	}
	
	public ArrayList<Constrain> getConstrain4() {
		return constrain4;
	}
	
	public void setConstrain4(LinkedList<Constrain> constrain4) {
		this.constrain4 = new ArrayList<Constrain>(constrain4);
	}
	
	public class Constrain implements Serializable{
		
		private ArrayList<Integer> constrain = null;
		private ArrayList<Double> coefficient = null;
		
		private Double minBoundaries = null;
		private Double maxBoundaries = null;
		
		public ArrayList<Integer> getConstrain() {
			return constrain;
		}
		
		public void setConstrain(LinkedList<Integer> constrain) {
			this.constrain = new ArrayList<Integer>(constrain);
		}
		
		public ArrayList<Double> getCoefficient() {
			return coefficient;
		}
		
		public void setCoefficient(LinkedList<Double> coefficient) {
			this.coefficient = new ArrayList<Double>(coefficient);
		}

		public Double getMinBoundaries() {
			return minBoundaries;
		}

		public void setMinBoundaries(Double minBoundaries) {
			this.minBoundaries = minBoundaries;
		}

		public Double getMaxBoundaries() {
			return maxBoundaries;
		}

		public void setMaxBoundaries(Double maxBoundaries) {
			this.maxBoundaries = maxBoundaries;
		}
		
	}

}
