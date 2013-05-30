package edu.iastate.hungnv.value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.caucho.quercus.env.ConstStringValue;
import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class Concat extends MultiValue implements Iterable<Value> {
	
	private ArrayList<Value> childNodes = new ArrayList<Value>(); // A size-2+ list of not-null regular values
	
	/*
	 * Constructors
	 */
	
	/**
	 * Constructor
	 * @param values	A size-2+ list of not-null regular values
	 */
	public Concat(List<Value> values) {
		childNodes.addAll(values);
	}

	/*
	 * Getters and setters
	 */
	
	public List<Value> getChildNodes() {
		return new ArrayList<Value>(childNodes);
	}
	
	/*
	 * Methods
	 */
	
	@Override
	public Switch flatten() {
		
		return flatten(0, new Value[childNodes.size()]);
	}
	
	/**
	 * Flattens an array of values, given that the values from index 0 (inclusive) to idxToFlatten (exclusive) have been flattened.
	 * @param idxToFlatten
	 * @param flattenedValues
	 * @return
	 */
	private Switch flatten(int idxToFlatten, Value[] flattenedValues) {
		Switch finalResult = new Switch();
		
		if (idxToFlatten < childNodes.size()) {
			Switch result1 = MultiValue.flatten(childNodes.get(idxToFlatten));
			
			for (Case case1 : result1) {
				Value value1 = case1.getValue();
				Constraint constraint1 = case1.getConstraint();
				
				flattenedValues[idxToFlatten] = value1;
				Switch result2 = flatten(idxToFlatten + 1, flattenedValues);
				
				for (Case case2 : result2) {
					Value value2 = case2.getValue();
					Constraint constraint2 = case2.getConstraint();
					
					Constraint constraint = Constraint.createAndConstraint(constraint1, constraint2);
					
					if (constraint.isSatisfiable()) // This check is required
						finalResult.addCase(new Case(constraint, value2));
				}
			}
		}
		else {
			StringBuilder result = new StringBuilder();
			for (Value childNode : flattenedValues)
				result.append(childNode.toString());
			
			finalResult.addCase(new Case(Constraint.TRUE, new ConstStringValue(result.toString())));
		}
		
		return finalResult;
	}
	
	@Override
	public Value simplify(Constraint constraint) {
		Value simplifiedValue = MultiValue.simplify(childNodes.get(0), constraint);
		
		for (int i = 1; i < childNodes.size(); i++) {
			Value nextValue = MultiValue.simplify(childNodes.get(i), constraint);
			simplifiedValue = MultiValue.createConcatValue(simplifiedValue, nextValue, true);
		}

		return simplifiedValue;
	}
	
	@Override
	public Iterator<Value> iterator() {
		return childNodes.iterator();
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		// TODO Produce a warning here
		
		StringBuilder result = new StringBuilder();
		for (Value childNode : childNodes)
			result.append(childNode.toString());
		
		return result.toString();
	}
	
	@Override
	public boolean isset() {
		return true;
	}
	
}