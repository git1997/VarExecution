package edu.iastate.hungnv.shadow.expr;

import com.caucho.quercus.env.Value;

/**
 * 
 * @author HUNG
 *
 */
public class ToArrayExpr_ extends AbstractUnaryExpr_ {

	@Override
	protected Value evalBasicCase(Value value) {
		return value.toArray();
	}

}
