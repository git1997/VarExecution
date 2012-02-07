package edu.iastate.hungnv.constraint;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Constraint {
	
	public static final Constraint TRUE = new SimpleConstraint("TRUE");
	
	/*
	 * Abstract methods
	 */
	
	/**
	 * @return A string describing the constraint
	 */
	@Override
	public abstract String toString();
	
	/*
	 * Methods
	 */
	
	/**
	 * @param constraint
	 * @return True if the two constraints are the same
	 */
	public boolean equals(Constraint constraint) {
		// TODO Revise
		
		return (this == constraint);
	}
	
	/*
	 * Static methods
	 */
	
	public static Constraint createConstraint(String constraint) {
		return new SimpleConstraint(constraint);
	}
	
	public static Constraint createNotConstraint(Constraint constraint) {
		if (constraint instanceof NotConstraint)
			return ((NotConstraint) constraint).getConstraint();
		else
			return new NotConstraint(constraint);
	}
	
	public static Constraint createAndConstraint(Constraint constraint1, Constraint constraint2) {
		if (constraint1.equals(Constraint.TRUE))
			return constraint2;
		else if (constraint2.equals(Constraint.TRUE))
			return constraint1;
		else
			return new AndConstraint(constraint1, constraint2);
	}
	
}
