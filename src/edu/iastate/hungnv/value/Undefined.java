package edu.iastate.hungnv.value;

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
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return "UNDEFINED";
	}
	
}
