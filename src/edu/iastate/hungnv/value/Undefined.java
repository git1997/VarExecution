package edu.iastate.hungnv.value;

import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class Undefined extends MultiValue {

	public static final Undefined UNDEFINED = new Undefined();

	/**
	 * Private constructor
	 */
	private Undefined() {
	}
	
	/*
	 * Methods
	 */
	
	@Override
	public Switch flatten() {
		return new Switch();
	}

	@Override
	public Value simplify(Constraint constraint) {
		return this;
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return "UNDEFINED";
	}
	
}
