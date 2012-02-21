package edu.iastate.hungnv.value;

import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.ConstStringValue;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.util.Logging;

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
	 * Returns all possible Quercus Values.
	 * @return All possible Quercus Values
	 */
	public abstract Switch flatten();
	
	/*
	 * Static methods
	 */
	
	/**
	 * Returns all possible Quercus Values of a regular Value.
	 * @param value		A regular value, not null
	 * @return All possible Quercus Values of the given value
	 */
	public static Switch flatten(Value value) {
		if (value instanceof MultiValue) {
			return ((MultiValue) value).flatten();
		}
		else 
			return new Case(Constraint.TRUE, value).flatten();
	}
	
	/**
	 * Returns a regular Value (usually a Case Value)
	 * @param constraint
	 * @param value			A Quercus value, not null
	 * @return	A regular Value (usually a Case Value)
	 */
	public static Value createCaseValue(Constraint constraint, Value value) {
		return new Case(constraint, value);
	}
	
	/**
	 * Returns a regular Value (usually a Switch Value)
	 * @param value1	A regular value, not null
	 * @param value2	A regular value, not null
	 * @return	A regular Value (usually a Switch Value)
	 */
	public static Value createSwitchValue(Value value1, Value value2) {
		// TODO Optimize this task
		
		Switch switch_ = new Switch();
		switch_.addCases(flatten(value1));
		switch_.addCases(flatten(value2));
		
		return switch_;
	}
	
	/**
	 * Returns a regular Value (usually a Choice Value)
	 * @param constraint
	 * @param trueBranchValue	A regular value, not null
	 * @param falseBranchValue	A regular value, not null
	 * @return	A regular Value (usually a Choice Value)
	 */
	public static Value createChoiceValue(Constraint constraint, Value trueBranchValue, Value falseBranchValue) {
		// TODO Optimize this task
		
		// Handle specially for the case after executing the else branch of an if statement
		// CHOICE(!cond, y, CHOICE(cond, x, z)) => CHOICE(cond, x, y) 
		if (falseBranchValue instanceof Choice) {
			if (Constraint.createNotConstraint(constraint).equivalentTo(
					((Choice) falseBranchValue).getConstraint()))

				return new Choice(((Choice) falseBranchValue).getConstraint(), ((Choice) falseBranchValue).getValue1(), trueBranchValue);
		}
		
		return new Choice(constraint, trueBranchValue, falseBranchValue);
	}
	
	/**
	 * Returns a regular Value (usually a Concat Value)
	 * @param value1	A regular value, not null
	 * @param value2	A regular value, not null
	 * @return	A regular Value (usually a Concat Value)
	 */
	public static Value createConcatValue(Value value1, Value value2) {
		return new Concat(value1, value2);
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public void print(Env env)
	{
		// TODO Revise
		
		new ConstStringValue(toString()).print(env);
		Logging.LOGGER.info("Printing: " + toString());
	}
	
	/*
	 * Experimental methods
	 */
	
	public Value evalUnaryNotExpr() {
		Switch newSwitch = new Switch();
		
		for (Case case_ : this.flatten()) {
			Case newCase = new Case(case_.getConstraint(), (case_.getValue() == BooleanValue.TRUE ? BooleanValue.FALSE : BooleanValue.TRUE));
			newSwitch.addCase(newCase);
		}
		
		return newSwitch;
	}
	
	public Value evalBinaryEqualsExpr(Value value) {
		Switch newSwitch = new Switch();
		
		for (Case case_ : this.flatten()) {
			Case newCase = new Case(case_.getConstraint(), (case_.getValue().eql(value) ? BooleanValue.TRUE : BooleanValue.FALSE));
			newSwitch.addCase(newCase);
		}
		
		return newSwitch;
	}
	
}