package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.JavaInvoker;
import com.caucho.quercus.env.QuercusClass;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;
import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.scope.ScopedValue;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.Switch;
import edu.iastate.hungnv.valuearray.FlattenedValueArray;
import edu.iastate.hungnv.valuearray.ValueArray;

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
				Value[] args_ = ((FlattenedValueArray) flattendedArgs_).get();
				return _this.callMethod_orig(env, qClass, qThis, args_);
			}
		}, env);
	}
	
	private static Switch flatten(Value[] args) {
		int len = args.length;
		Switch flattenedArgsSet = new Switch();
		
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
		
		for (Case case_ : new ValueArray(argValues).flatten()) {
			Value[] flattenedArgValues = ((FlattenedValueArray) case_.getValue()).get();
			Constraint constraint = case_.getConstraint();
			
			for (int i = 0; i < len; i++) {
				if (args[i] instanceof Var)
					argValues[i] = new Var(flattenedArgValues[i]);
				else
					argValues[i] = flattenedArgValues[i];
			}
			
			Case flattenedArgs = new Case(constraint, new FlattenedValueArray(argValues));
			flattenedArgsSet.addCase(flattenedArgs);
		}
		
		return flattenedArgsSet;
	}
	  
}
