package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;
import com.caucho.quercus.statement.Statement;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class IfStatement_ {
	
	/**
	 * @see com.caucho.quercus.statement.IfStatement.execute(Env)
	 */
	public static Value execute(Env env, Expr condition, Statement trueBlock, Statement falseBlock) {
		Value condValue = condition.eval(env);
		
		Constraint constraint = MultiValue.whenTrue(condValue);
		
	    if (constraint.isTautology()) {
	    	return trueBlock.execute(env);
	    }
	    else if (constraint.isContradiction()) {
	    	if (falseBlock != null)
	    		return falseBlock.execute(env);
	    	else
	    		return null;
	    }
		
		if (env.getEnv_().canEnterNewScope(constraint)) {
			env.getEnv_().enterNewScope(constraint);
			trueBlock.execute(env);
			env.getEnv_().exitScope();
		}
		
		if (falseBlock != null) {
			Constraint notConstraint = Constraint.createNotConstraint(constraint);
			if (env.getEnv_().canEnterNewScope(notConstraint)) {
				env.getEnv_().enterNewScope(notConstraint);
				falseBlock.execute(env);
				env.getEnv_().exitScope();
			}
		}
		
		return null; // TODO Handle returned value
	}
  
}