package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Null;

/**
 * 
 * @author HUNG
 *
 */
public class ShadowInterpreter {
	
	public interface IBasicCaseHandler {
		
		/**
		 * Execute the code with a given Quercus value.
		 * @param value	A Quercus value, not null
		 */
		public Value evalBasicCase(Value value, Env env);
	}
	
	/**
	 * Execute the code with a given regular Value.
	 * @param value		A regular value, not null
	 * @param handler	The handler for a *Quercus* value
	 */
	public static Value eval(Value value, IBasicCaseHandler handler, Env env) {
		Value combinedReturnValue = null;
		Value retValue = null;
		
		for (Case case_ : MultiValue.flatten(value)) {
			Value flattenedValue = case_.getValue();
			Constraint constraint = case_.getConstraint();
			
			if (!env.getEnv_().canEnterNewScope(constraint))
				continue;
			
			boolean constraintAlwaysTrue = constraint.isTautology();
			
			if (!constraintAlwaysTrue)
				env.getEnv_().enterNewScope(constraint);
			
			//----- EVAL BASIC CASE -----
			retValue = handler.evalBasicCase(flattenedValue, env);    
		    //---------------------------
			
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
