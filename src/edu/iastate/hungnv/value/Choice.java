package edu.iastate.hungnv.value;

import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class Choice extends MultiValue {

	private Constraint constraint;
	private Value value1;	// A regular value, not null
	private Value value2;	// A regular value, not null
	
	/**
	 * Constructor
	 * @param constraint
	 * @param value1	A regular value, not null
	 * @param value2	A regular value, not null
	 */
	public Choice(Constraint constraint, Value value1, Value value2) {
		this.constraint = constraint;
		this.value1 = value1;
		this.value2 = value2;
	}
	
	/*
	 * Getters and setters
	 */
	
	public Constraint getConstraint() {
		return constraint;
	}
	
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
		Switch switch_ = new Switch();
		
		Switch cases1 = MultiValue.flatten(value1);
		Switch cases2 = MultiValue.flatten(value2);
		
		for (Case case_ : cases1) {
			Value value = case_.getValue();
			Constraint constraint = Constraint.createAndConstraint(this.constraint, case_.getConstraint());
			
			switch_.addCase(new Case(constraint, value));
		}
		
		Constraint notConstraint = Constraint.createNotConstraint(this.constraint);
		for (Case case_ : cases2) {
			Value value = case_.getValue();
			Constraint constraint = Constraint.createAndConstraint(notConstraint, case_.getConstraint());
			
			switch_.addCase(new Case(constraint, value));
		}
		
		return switch_;
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return "CHOICE(" + constraint.toString() + ", " + value1.toString() + ", " + value2.toString() + ")";
	}
	
	@Override
	public Value get(Value index) {
		return MultiValue.createChoiceValue(constraint, value1.get(index), value2.get(index));
	}
	
}
