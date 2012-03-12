package edu.iastate.hungnv.valuearray;

import java.util.Arrays;
import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Switch;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class ValueArray extends Value {
	
	private Value[] values;		// Array of regular values
	
	/**
	 * Constructor
	 * @param values	Array of regular values
	 */
	public ValueArray(Value[] values) {
		this.values = Arrays.copyOf(values, values.length);
	}
	
	/*
	 * Getters and setters
	 */
	
	/**
	 * @return Array of regular values
	 */
	public Value[] get() {
		return Arrays.copyOf(values, values.length);
	}
	
	/*
	 * Methods
	 */
	
	public Switch flatten() {
		int len = values.length;
		Switch arraySwitch = new Switch();
		
		// Get all possible values of array elements
		Case[][] cases = new Case[len][];
		for (int i = 0; i < len; i++) {
			Switch switch_ = MultiValue.flatten(values[i]);
			cases[i] = switch_.getCases().toArray(new Case[0]);
		}
		
		// curCursor is used to mark the current selected values for the array 
		int[] curCursor = new int[len];
		for (int i = 0; i < len; i++)
			curCursor[i] = 0;
		
		// flattenedValues will contain flattened values of the array elements
		Value[] flattenedValues = new Value[len];
		
		while (true) {
			// Get the current selected values
			Constraint constraint = Constraint.TRUE;
			for (int i = 0; i < len; i++) {
				Case curCase = cases[i][curCursor[i]];
				
				flattenedValues[i] = curCase.getValue();
				constraint = Constraint.createAndConstraint(constraint, curCase.getConstraint());
			}
			
			if (constraint.isSatisfiable()) { // This check is required
				Case newCase = new Case(constraint, new FlattenedValueArray(flattenedValues));
				arraySwitch.addCase(newCase);
			}
			
			// Update curCursor
			int i;
			for (i = len - 1; i >= 0; i--) {
				if (curCursor[i] < cases[i].length - 1) {
					curCursor[i]++;
					break;
				}
				else
					curCursor[i] = 0;
			}
			
			// Exit if all combinations have been visited
			if (i == -1)
				break;
		}
		
		return arraySwitch;
	}
	
}
