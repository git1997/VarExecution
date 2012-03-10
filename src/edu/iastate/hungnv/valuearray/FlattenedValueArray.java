package edu.iastate.hungnv.valuearray;

import java.util.Arrays;

import com.caucho.quercus.env.Value;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class FlattenedValueArray extends Value {

	private Value[] values; 	// Array of Quercus values

	/**
	 * Constructor
	 * @param values	Array of Quercus values
	 */
	public FlattenedValueArray(Value[] values) {
		this.values = Arrays.copyOf(values, values.length);
	}

	/*
	 * Getters and setters
	 */
	
	/**
	 * @return Array of Quercus values
	 */
	public Value[] get() {
		return Arrays.copyOf(values, values.length);
	}
	
}
