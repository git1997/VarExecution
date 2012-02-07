package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;
import com.caucho.quercus.statement.Statement;

import edu.iastate.hungnv.constraint.Constraint;

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
		Constraint constraint = Constraint.createConstraint(condition.toString());
		
		env.getEnv_().enterNewScope(constraint);
		trueBlock.execute(env);
		env.getEnv_().exitScope();
		
		if (falseBlock != null) {
			env.getEnv_().enterNewScope(Constraint.createNotConstraint(constraint));
			falseBlock.execute(env);
			env.getEnv_().exitScope();
		}
		
		return null; // TODO Handle returned value
	}
  
}