package edu.iastate.hungnv.value;

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
	public Switch flatten() {
		// TODO Revise
		Switch switch_ = new Switch();
		
		Switch cases1 = MultiValue.flatten(value1);
		Switch cases2 = MultiValue.flatten(value2);
		
		for (Case case1 : cases1)
		for (Case case2 : cases2) {
			Value value = new ConstStringValue(case1.getValue().toString() + case2.getValue().toString());
			Constraint constraint = Constraint.createAndConstraint(case1.getConstraint(), case2.getConstraint());
			
			if (constraint.isSatisfiable()) // This check is required
				switch_.addCase(new Case(constraint, value));
		}
		
		return switch_;
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		// TODO Revise
		
		//return "CONCAT(" + value1.toString() + ", " + value2.toString() + ")";
		return value1.toString() + value2.toString();
	}
	
	@Override
	public boolean isset() {
		return true;
	}
	
}
