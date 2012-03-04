package edu.iastate.hungnv.shadow.expr;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Switch;

/**
 * 
 * @author HUNG
 *
 */
public abstract class AbstractBinaryExpr_ {
	
	/**
	 * Evaluates a binary expression.
	 */
	public Value eval(Env env, Expr leftExpr, Expr rightExpr) {
		Value leftValue = leftExpr.eval(env);
		Value rightValue = rightExpr.eval(env);

		Switch switch_ = new Switch();
		
		for (Case leftCase : MultiValue.flatten(leftValue))
		for (Case rightCase : MultiValue.flatten(rightValue)) {
			Constraint constraint = Constraint.createAndConstraint(leftCase.getConstraint(), rightCase.getConstraint());
			Value value = evalBasicCase(leftCase.getValue(), rightCase.getValue());
			
			if (constraint.isSatisfiable()) // TODO This check is optional
				switch_.addCase(new Case(constraint, value));
		}
		
		return switch_;
	}
	
	/**
	 * Evaluates a binary expression.
	 * @param leftValue		A Quercus value, not null
	 * @param rightValue	A Quercus value, not null
	 * @return The evaluated value
	 */
	protected abstract Value evalBasicCase(Value leftValue, Value rightValue);
	
}
