package edu.iastate.hungnv.shadow.expr;

import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class BinaryOrExpr_ extends AbstractBinaryExpr_ {

	@Override
	protected Value earlyEval(Value leftValue) {
		if (MultiValue.whenTrue(leftValue).isTautology())
			return BooleanValue.TRUE;
		else
			return null;
	}
	
	@Override
	protected Value evalBasicCase(Value leftValue, Value rightValue) {
		if (leftValue.toBoolean() || rightValue.toBoolean())
			return BooleanValue.TRUE;
		else
			return BooleanValue.FALSE;
	}
	  
}
