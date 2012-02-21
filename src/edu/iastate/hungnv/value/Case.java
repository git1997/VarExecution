package edu.iastate.hungnv.value;

import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class Case extends MultiValue {
	
	private Constraint constraint;
	private Value value;	// A Quercus value, not null
	
	/**
	 * Constructor
	 * @param constraint
	 * @param value		A Quercus value, not null
	 */
	public Case(Constraint constraint, Value value) {
		this.constraint = constraint;
		this.value = value;
	}

	/*
	 * Getters and setters
	 */
	
	public Constraint getConstraint() {
		return constraint;
	}

	public Value getValue() {
		return value;
	}
	
	/*
	 * Methods
	 */

	@Override
	public Switch flatten() {
		Switch switch_ = new Switch();
		switch_.addCase(this);
		
		return switch_;
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return "CASE(" + constraint.toString() + ", " + value.toString() + ")";
	}
	
}
