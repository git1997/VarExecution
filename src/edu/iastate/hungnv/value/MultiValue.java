package edu.iastate.hungnv.value;

import java.util.HashMap;
import java.util.Map;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public abstract class MultiValue extends Value {
	
	/*
	 * Abstract methods
	 */

	/**
	 * Returns a Quercus Value.
	 * @return A Quercus Value or null
	 */
	public abstract Value getRepresentativeValue();
	
	/**
	 * Returns all possible Quercus Values.
	 * @return All possible Quercus Values
	 */
	public abstract Map<Value, Constraint> getAllPossibleValues();
	
	/*
	 * Static methods
	 */
	
	/**
	 * Returns a variational Value
	 * @param constraint
	 * @param trueBranchValue	A regular value, not null
	 * @param falseBranchValue	A regular value, not null
	 * @return	A regular value
	 */
	public static Value createVariationalValue(Constraint constraint, Value trueBranchValue, Value falseBranchValue) {
		// TODO Optimize this task
		
		if (trueBranchValue == com.caucho.quercus.env.NullValue.NULL)
			trueBranchValue = NullValue.NULL;
		
		if (falseBranchValue == com.caucho.quercus.env.NullValue.NULL)
			falseBranchValue = NullValue.NULL;
		
		return new Choice(constraint, trueBranchValue, falseBranchValue);
	}
	
	/**
	 * Returns all possible Quercus Values of a regular Value.
	 * @param value		A regular value, not null
	 * @return All possible Quercus Values of the given value
	 */
	public static Map<Value, Constraint> getAllPossibleValues(Value value) {
		if (value instanceof MultiValue) {
			return ((MultiValue) value).getAllPossibleValues();
		}
		else  {
			Map<Value, Constraint> map = new HashMap<Value, Constraint>();
			map.put(value, Constraint.TRUE);
			return map;
		}
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public void print(Env env)
	{
		// TODO Revise
		
		getRepresentativeValue().print(env);
		System.out.println("Printing: " + toString());
	}
	
}