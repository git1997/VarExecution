package edu.iastate.hungnv.value;

import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class TCase<T> {
	
	private Constraint constraint;	// The constraint must be satisfiable
	private T object;				// An object, not null
	
	/**
	 * Constructor
	 * @param constraint	The constraint must be satisfiable
	 * @param object		An object, not null
	 */
	public TCase(Constraint constraint, T object) {
		this.constraint = constraint;
		this.object = object;
	}

	/*
	 * Getters and setters
	 */
	
	/**
	 * @return The constraint (must be satisfiable)
	 */
	public Constraint getConstraint() {
		return constraint;
	}

	/**
	 * @return The object (not null)
	 */
	public T getObject() {
		return object;
	}
	
}
