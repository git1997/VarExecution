package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;
import com.caucho.quercus.expr.FunIncludeOnceExpr;
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
public class FunIncludeOnceExpr_ {

	/**
	 * @see com.caucho.quercus.expr.FunIncludeOnceExpr.eval(Env)
	 */
	public static Value eval(Env env,
								FunIncludeOnceExpr _this,
								Expr _expr, Path _dir, boolean _isRequire) {
		
		Value combinedReturnValue = null;
		Value retValue = null;

		for (Case case_ : MultiValue.flatten(_expr.eval(env))) {
			Value flattenedValue = case_.getValue();
			Constraint constraint = case_.getConstraint();
			
			boolean constraintAlwaysTrue = constraint.isTautology();
			
			if (!constraintAlwaysTrue)
				env.getEnv_().enterNewScope(constraint);
		
			//----- BEGIN OF ORIGINAL CODE -----
		
			    StringValue name = flattenedValue.toStringValue(); // INST Original: StringValue name = _expr.eval(env).toStringValue();

			    env.pushCall(_this, NullValue.NULL, new Value[] { name });
			    
			    try {
			      if (_dir != null)
			        retValue = env.includeOnce(_dir, name, _isRequire); // INST Original: return env.includeOnce(_dir, name, _isRequire);
			      else if (_isRequire)
			        retValue = env.requireOnce(name); // INST Original: return env.requireOnce(name);
			      else
			        retValue = env.includeOnce(name); // INST Original: return env.includeOnce(name);
			    }
			    finally {
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