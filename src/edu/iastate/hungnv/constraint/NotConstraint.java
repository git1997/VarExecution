package edu.iastate.hungnv.constraint;

/**
 * 
 * @author HUNG
 *
 */
public class NotConstraint extends Constraint {

	private Constraint constraint;	// The child constraint

	/**
	 * Construtor
	 * @param constraint
	 */
	public NotConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	/*
	 * Getters and setters
	 */
	
	/**
	 * @return the child constraint
	 */
	public Constraint getConstraint() {
		return constraint;
	}

	/*
	 * Methods
	 */
	
	@Override
	public String toString() {
		return "! " + constraint.toString();
	}
	
}
