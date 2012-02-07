package edu.iastate.hungnv.shadow;

import java.util.Map;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;
import com.caucho.quercus.expr.FunIncludeExpr;
import com.caucho.vfs.Path;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class FunIncludeExpr_ {

	/**
	 * @see com.caucho.quercus.expr.FunIncludeExpr.eval(Env)
	 */
	public static Value eval(Env env, FunIncludeExpr expr, Expr _expr, Path _dir, boolean _isRequire) {
		Value val = _expr.eval(env);
		
		Map<Value, Constraint> map = MultiValue.getAllPossibleValues(val);
		
		for (Value value: map.keySet()) {
			Constraint constraint = map.get(value);
			
			StringValue name = value.toStringValue();
	      
			env.pushCall(expr, NullValue.NULL, new Value[] { name });
		    try {
		    	env.getEnv_().enterNewScope(constraint);
		      
		    	env.include(_dir, name, _isRequire, false);
		    	
		    	env.getEnv_().exitScope();
		    } finally {
		      env.popCall();
		    }
		}
		
		return null; // TODO Handle returned value
	}
	  
}