package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;
import com.caucho.quercus.expr.FunIncludeExpr;
import com.caucho.vfs.Path;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Null;

/**
 * 
 * @author HUNG
 *
 */
public class FunIncludeExpr_ {

	/**
	 * @see com.caucho.quercus.expr.FunIncludeExpr.eval(Env)
	 */
	public static Value eval(Env env,
								FunIncludeExpr _this,
								Expr _expr, Path _dir, boolean _isRequire) {
		
		Value combinedReturnValue = null;
		Value retValue = null;

		for (Case case_ : MultiValue.flatten(_expr.eval(env))) {
			Value flattenedValue = case_.getValue();
			Constraint constraint = case_.getConstraint();
			
			if (!env.getEnv_().canEnterNewScope(constraint))
				continue;
			
			boolean constraintAlwaysTrue = constraint.isTautology();
			
			if (!constraintAlwaysTrue)
				env.getEnv_().enterNewScope(constraint);
		
			//----- BEGIN OF ORIGINAL CODE -----
		
				StringValue name = flattenedValue.toStringValue(); // INST Original: StringValue name = _expr.eval(env).toStringValue();
			      
			    env.pushCall(_this, NullValue.NULL, new Value[] { name });
			    try {
			      retValue = env.include(_dir, name, _isRequire, false); // INST Original: return env.include(_dir, name, _isRequire, false);  
			    } finally {
			      env.popCall();
			    }
			    
			//----- END OF ORIGINAL CODE -----
			    
			if (!constraintAlwaysTrue)
			   	env.getEnv_().exitScope();
			
			retValue = MultiValue.createChoiceValue(constraint, retValue, Null.NULL);
			
			if (combinedReturnValue == null)
				combinedReturnValue = retValue;
			else
				combinedReturnValue = MultiValue.createSwitchValue(combinedReturnValue, retValue);
		}
		
		return combinedReturnValue;
	}
	  
}