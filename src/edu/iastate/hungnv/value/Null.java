package edu.iastate.hungnv.value;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class Null extends MultiValue {

	public static final Null NULL = new Null();

	/**
	 * Private constructor
	 */
	private Null() {
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
		return "NULL";
	}
	
}
