package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.JavaInvoker;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.QuercusClass;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;
import com.caucho.quercus.expr.Expr;
import com.caucho.quercus.marshal.Marshal;
import com.caucho.util.L10N;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.scope.ScopedValue;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Null;
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
	public static Value callMethod(Env env, QuercusClass qClass, Value qThis, Value []origArgs,
									JavaInvoker _this,
									Class<?> [] _param, boolean _hasEnv, boolean _hasThis, Marshal []_marshalArgs,
									Expr [] _defaultExprs, String _name, int _minArgumentLength, L10N L, boolean _hasRestArgs,
									Value []NULL_VALUES, boolean _isRestReference, Marshal _unmarshalReturn) {
		
		Value combinedReturnValue = null;
		Value retValue = null;

		for (Case case_ : flatten(origArgs)) {
			Value[] args = ((FlattenedValueArray) case_.getValue()).get();
			Constraint constraint = case_.getConstraint();
			
			if (!env.getEnv_().canEnterNewScope(constraint))
				continue;
			
			boolean constraintAlwaysTrue = constraint.isTautology();
			
			if (!constraintAlwaysTrue)
				env.getEnv_().enterNewScope(constraint);
		
			//----- BEGIN OF ORIGINAL CODE -----
		  
			    int len = _param.length;
			
			    Object []javaArgs = new Object[len];
			
			    int k = 0;
			
			    if (_hasEnv)
			      javaArgs[k++] = env;
			
			    Object obj = null;
			
			    if (_hasThis) {
			      obj = qThis != null ? qThis.toJavaObject() : null;
			      javaArgs[k++] = qThis;
			    }
			    else if (! _this.isStatic() && ! _this.isConstructor()) {
			      obj = qThis != null ? qThis.toJavaObject() : null;
			    }
			    
			    String warnMessage = null;
			    for (int i = 0; i < _marshalArgs.length; i++) {
			      if (i < args.length && args[i] != null)
			        javaArgs[k] = _marshalArgs[i].marshal(env, args[i], _param[k]);
			      else if (_defaultExprs[i] != null) {
			        javaArgs[k] = _marshalArgs[i].marshal(env,
			                                              _defaultExprs[i],
			                                              _param[k]);
			      } else {
			        warnMessage = L.l(
			          "function '{0}' has {1} required arguments, "
			          + "but only {2} were provided",
			          _name,
			          _minArgumentLength,
			          args.length);
			
			        //return NullValue.NULL;
			
			        javaArgs[k] = _marshalArgs[i].marshal(env, NullValue.NULL, _param[k]);
			      }
			
			      /*
			      if (javaArgs[k] != null)
			        System.out.println("ARG: " + javaArgs[k] + " " + _marshalArgs[i]);
			      */
			
			      k++;
			    }
			
			    if (warnMessage != null)
			      env.warning(warnMessage);
			
			    if (_hasRestArgs) {
			      Value []rest;
			
			      int restLen = args.length - _marshalArgs.length;
			
			      if (restLen <= 0)
			        rest = NULL_VALUES;
			      else {
			        rest = new Value[restLen];
			
			        for (int i = _marshalArgs.length; i < args.length; i++) {
			          if (_isRestReference) {
			            rest[i - _marshalArgs.length] = args[i].toLocalVarDeclAsRef();
			          }
			          else
			            rest[i - _marshalArgs.length] = args[i].toValue();
			        }
			      }
			
			      javaArgs[k++] = rest;
			    }
			    else if (_marshalArgs.length < args.length) {
			    	// INST ADDED BY HUNG
			    	if (!Env_.INSTRUMENT || !_name.equals("debug_backtrace")) // Quick fix so that Quercus won't show spurious warnings (these warnings are caused by incorrect implementation of Quercus, not by the PHP code).
			    	// END OF ADDED CODE
			    		
			      // php/153o
			      env.warning(L.l(
			        "function '{0}' called with {1} arguments, "
			        + "but only expects {2} arguments",
			        _name,
			        args.length,
			        _marshalArgs.length));
			    }
			
			    Object result = _this.invoke(obj, javaArgs);
			
			    Value value = _unmarshalReturn.unmarshal(env, result);
			
			    retValue = value; // INST Original: return value;
	    
			//----- END OF ORIGINAL CODE -----
			
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
