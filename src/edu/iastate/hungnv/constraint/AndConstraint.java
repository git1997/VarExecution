package edu.iastate.hungnv.constraint;

/**
 * 
 * @author HUNG
 *
 */
public class AndConstraint extends Constraint {
	
	private Constraint constraint1;
	private Constraint constraint2;

	/**
	 * Construtor
	 * @param constraint1
	 * @param constraint2
	 */
	public AndConstraint(Constraint constraint1, Constraint constraint2) {
		this.constraint1 = constraint1;
		this.constraint2 = constraint2;
	}
	
	/*
	 * Getters and setters
	 */
	
	public Constraint getConstraint1() {
		return constraint1;
	}

	public Constraint getConstraint2() {
		return constraint2;
	}

	/*
	 * Methods
	 */
	
	@Override
	public String toString() {
		return constraint1.toString() + " & " + constraint2.toString();
	}

}
