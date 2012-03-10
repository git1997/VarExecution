package edu.iastate.hungnv.shadow.expr;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.BinaryAppendExpr;
import com.caucho.quercus.expr.Expr;

import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class BinaryAppendExpr_ {
	
	/**
	 * @see com.caucho.quercus.expr.BinaryAppendExpr.eval(Env)
	 */
	public static Value eval(Env env, Expr _value, BinaryAppendExpr _next) {
		Value value = _value.eval(env);
		
	    Value sb = value.toStringBuilder(env);
		
	    for (BinaryAppendExpr ptr = _next; ptr != null; ptr = ptr.getNext()) {
	      Value ptrValue = ptr.getValue().eval(env);
	
	      sb = MultiValue.createConcatValue(sb, ptrValue);
	    }
	    
	    return sb;
	}

}
