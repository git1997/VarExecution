package edu.iastate.hungnv.constraint;

/**
 * 
 * @author HUNG
 *
 */
public class SimpleConstraint extends Constraint {

	private String constraint;

	/**
	 * Constructor
	 * @param constraint
	 */
	public SimpleConstraint(String constraint) {
		this.constraint = constraint;
	}
	
	/*
	 * Getters and setters
	 */
	
	public String getConstraint() {
		return constraint;
	}
	
	/*
	 * Methods
	 */
	
	@Override
	public String toString() {
		return constraint;
	}

}
