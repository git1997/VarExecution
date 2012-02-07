package edu.iastate.hungnv.value;

import java.util.Map;

import com.caucho.quercus.env.ConstStringValue;
import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class Concat extends MultiValue {
	
	private Value value1;	// A regular value, not null
	private Value value2;	// A regular value, not null

	/**
	 * Constructor
	 * @param value1	A regular value, not null
	 * @param value2	A regular value, not null
	 */
	public Concat(Value value1, Value value2) {
		this.value1 = value1;
		this.value2 = value2;
	}
	
	/*
	 * Getters and setters
	 */
	
	public Value getValue1() {
		return value1;
	}
	
	public Value getValue2() {
		return value2;
	}
	
	/*
	 * Methods
	 */
	
	@Override
	public Value getRepresentativeValue() {
		return new ConstStringValue(value1.toString() + value2.toString());
	}
	
	@Override
	public Map<Value, Constraint> getAllPossibleValues() {
		// TODO Revise
		
		return MultiValue.getAllPossibleValues(new ConstStringValue(value1.toString() + value2.toString()));
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return "CONCAT(" + value1.toString() + ", " + value2.toString() + ")";
	}
	
}
