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
	public static Value execute(Env env, final ForeachStatement _this, final Value origObj, Value obj) {
		// TODO Consider flattening origObj
		
		if (! (obj instanceof MultiValue))
			return _this.execute_basic(env, origObj, obj);
		
		return ShadowInterpreter.eval(obj, new ShadowInterpreter.IBasicCaseHandler() {
			@Override
			public Value evalBasicCase(Value flattenedObj, Env env) {
				return _this.execute_basic(env, origObj, flattenedObj);
			}
		}, env);
	}

}
