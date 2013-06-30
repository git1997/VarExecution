package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.statement.ForeachStatement;

import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class ForeachStatement_ {
	
	/**
	 * @see com.caucho.quercus.statement.ForeachStatement.execute(Env)
	 */
	public static Value execute(Env env, final ForeachStatement _this, Value origObj, Value obj) {		
		if (! (obj instanceof MultiValue))
			return _this.execute_basic(env, origObj, obj);
		
		if (origObj != obj)
			return _this.execute_basic(env, origObj, obj);
		
		Value simplifiedObj = ((MultiValue) obj).simplify(env.getEnv_().getScope().getConstraint());
		if (! (simplifiedObj instanceof MultiValue))
			return _this.execute_basic(env, simplifiedObj, simplifiedObj);
		
		return ShadowInterpreter.eval(obj, new ShadowInterpreter.IBasicCaseHandler() {
			@Override
			public Value evalBasicCase(Value flattenedObj, Env env) {
				return _this.execute_basic(env, flattenedObj, flattenedObj);
			}
		}, env);
	}

}
