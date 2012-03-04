package edu.iastate.hungnv.shadow.expr;

import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.Value;

/**
 * 
 * @author HUNG
 *
 */
public class BinaryOrExpr_ extends AbstractBinaryExpr_ {

	@Override
	protected Value evalBasicCase(Value leftValue, Value rightValue) {
		if (leftValue.toBoolean() || rightValue.toBoolean())
			return BooleanValue.TRUE;
		else
			return BooleanValue.FALSE;
	}
	  
}
