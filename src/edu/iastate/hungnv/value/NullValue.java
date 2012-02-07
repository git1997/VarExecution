package edu.iastate.hungnv.value;

import java.util.HashMap;
import java.util.Map;

import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class NullValue extends MultiValue {

	public static final NullValue NULL = new NullValue();

	/**
	 * Private constructor
	 */
	private NullValue() {
	}
	
	/*
	 * Methods
	 */
	
	@Override
	public Value getRepresentativeValue() {
		return null;
	}

	@Override
	public Map<Value, Constraint> getAllPossibleValues() {
		return new HashMap<Value, Constraint>();
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return "NULL";
	}

}
