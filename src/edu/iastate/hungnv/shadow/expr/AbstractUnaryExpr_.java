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
public abstract class AbstractUnaryExpr_ {

	/**
	 * Evaluates a unary expression.
	 */
	public Value eval(Env env, Expr expr) {
		Value val = expr.eval(env);
		
		Switch switch_ = new Switch();
		
		for (Case case_ : MultiValue.flatten(val)) {
			Constraint constraint = case_.getConstraint();
			Value value = evalBasicCase(case_.getValue());
			
			switch_.addCase(new Case(constraint, value));
		}
		
		return switch_;
	}
	
	/**
	 * Evaluates a unary expression.
	 * @param value		A Quercus value, not null
	 * @return The evaluated value
	 */
	protected abstract Value evalBasicCase(Value value);
	
}
