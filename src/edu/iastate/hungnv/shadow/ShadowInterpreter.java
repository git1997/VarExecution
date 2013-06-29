package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.constraint.Constraint.Result;
import edu.iastate.hungnv.util.Logging;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Switch;

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
		 * @return 		The result of the execution
		 */
		public Value evalBasicCase(Value value, Env env);
	}
	
	/**
	 * Execute the code with a given regular Value.
	 * @param value		A regular value, not null
	 * @param handler	The handler for a *Quercus* value
	 */
	public static Value eval(Value value, IBasicCaseHandler handler, Env env) {
		Switch combinedReturnValue = new Switch();
		
		for (Case case_ : MultiValue.flatten(value)) {
			Value flattenedValue = case_.getValue();
			Constraint constraint = case_.getConstraint();
			
			Constraint aggregatedConstraint = env.getEnv_().getScope().getConstraint();
			Constraint.Result result = aggregatedConstraint.tryAddingConstraint(constraint);
			boolean constraintAlwaysTrue = (result == Result.THE_SAME);
			boolean constraintAlwaysFalse = (result == Result.ALWAYS_FALSE);
			
			if (constraintAlwaysFalse)
				continue;
			
			if (!constraintAlwaysTrue)
				env.getEnv_().enterNewScope(constraint);
			
			//----- EVAL BASIC CASE -----
			Value retValue = handler.evalBasicCase(flattenedValue, env);    
		    //---------------------------
			
			if (!constraintAlwaysTrue)
			   	env.getEnv_().exitScope();
			
			if (constraintAlwaysTrue)
				return retValue;

			/*
			 * Handle the case where retValue is a MultiValue
			 */
			if (retValue instanceof MultiValue) {
				retValue = ((MultiValue) retValue).simplify(constraint);
			
				if (retValue instanceof MultiValue) {
					Logging.LOGGER.fine("In ShadowInterpreter.java: retValue is a MultiValue. Please debug.");
					retValue = null;
				}
			}
			
			combinedReturnValue.addCase(new Case(constraint, retValue));
		}
		
		// TODO Check if combinedReturnValue is an empty Switch and debug why this happens. 

		return combinedReturnValue;
	}

}
