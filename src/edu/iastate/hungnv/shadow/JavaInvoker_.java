package edu.iastate.hungnv.shadow;

import java.util.Arrays;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.JavaInvoker;
import com.caucho.quercus.env.QuercusClass;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;
import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.scope.ScopedValue;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Switch;

/**
 * 
 * @author HUNG
 *
 */
public class JavaInvoker_ {

	/**
	 * @see com.caucho.quercus.env.JavaInvoker.callMethod(Env, QuercusClass, Value, Value[])
	 */
	public static Value callMethod(Env env,
									final QuercusClass qClass,
									final Value qThis,
									Value []args,
									final JavaInvoker _this) {
		
		Value flattenedArgs = flatten(args);
		
		return ShadowInterpreter.eval(flattenedArgs, new ShadowInterpreter.IBasicCaseHandler() {
			@Override
			public Value evalBasicCase(Value flattendedArgs_, Env env) {
				Value[] args_ = (Value[]) ((WrappedObject) flattendedArgs_).getObject();
				return _this.callMethod_orig(env, qClass, qThis, args_);
			}
		}, env);
	}
	
	private static Switch flatten(Value[] args) {
		int len = args.length;
		Switch flattenedArgsSet = new Switch();
		
		// Pre-processing
		Value[] argValues = new Value[len];
		for (int i = 0; i < len; i++) {
			if (args[i] instanceof Var)
				argValues[i] = ((Var) args[i]).getRawValue();
			else
				argValues[i] = args[i];
			
			// TODO Revise
			if (argValues[i] instanceof ScopedValue)
				argValues[i] = ((ScopedValue) argValues[i]).getValue();
		}
		
		// The real flattening operation takes place here
		Switch flattenedArgSet_ = flatten2(argValues);
		
		// Post-processing
		for (Case case_ : flattenedArgSet_) {
			Value[] flattenedArgValues = (Value[]) ((WrappedObject) case_.getValue()).getObject();
			Constraint constraint = case_.getConstraint();
			
			for (int i = 0; i < len; i++) {
				if (args[i] instanceof Var)
					argValues[i] = new Var(flattenedArgValues[i]);
				else
					argValues[i] = flattenedArgValues[i];
			}
			
			Case flattenedArgs = new Case(constraint, new WrappedObject(Arrays.copyOf(argValues, argValues.length)));
			flattenedArgsSet.addCase(flattenedArgs);
		}
		
		return flattenedArgsSet;
	}
	
	public static Switch flatten2(Value[] values) {
		int len = values.length;
		Switch arraySwitch = new Switch();
		
		// Get all possible values of array elements
		Case[][] cases = new Case[len][];
		for (int i = 0; i < len; i++) {
			Switch switch_ = MultiValue.flatten(values[i]);
			cases[i] = switch_.getCases().toArray(new Case[0]);
		}
		
		// curCursor is used to mark the current selected values for the array 
		int[] curCursor = new int[len];
		for (int i = 0; i < len; i++)
			curCursor[i] = 0;
		
		// flattenedValues will contain flattened values of the array elements
		Value[] flattenedValues = new Value[len];
		
		while (true) {
			// Get the current selected values
			Constraint constraint = Constraint.TRUE;
			for (int i = 0; i < len; i++) {
				Case curCase = cases[i][curCursor[i]];
				
				flattenedValues[i] = curCase.getValue();
				constraint = Constraint.createAndConstraint(constraint, curCase.getConstraint());
			}
			
			if (constraint.isSatisfiable()) { // This check is required
				Case newCase = new Case(constraint, new WrappedObject(Arrays.copyOf(flattenedValues, flattenedValues.length)));
				arraySwitch.addCase(newCase);
			}
			
			// Update curCursor
			int i;
			for (i = len - 1; i >= 0; i--) {
				if (curCursor[i] < cases[i].length - 1) {
					curCursor[i]++;
					break;
				}
				else
					curCursor[i] = 0;
			}
			
			// Exit if all combinations have been visited
			if (i == -1)
				break;
		}
		
		return arraySwitch;
	}
	  
}
