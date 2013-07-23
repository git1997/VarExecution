package edu.iastate.hungnv.shadow;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.ArrayValue;
import com.caucho.quercus.env.ArrayValueImpl;
import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;
import com.caucho.quercus.env.ArrayValue.Entry;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.regressiontest.RegressionTest;
import edu.iastate.hungnv.util.Logging;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.MultiValue.IOperation;

/**
 * 
 * @author HUNG
 *
 */
public class Functions {
	
	public static class __CHOICE__ {
		
		public static Value evalImpl(Value[] args) {
			if (args.length == 3) {
				return MultiValue.createChoiceValue(Constraint.createConstraint(args[0].toString()), args[1], args[2]);
			}
			else if (args.length == 1) {
				return MultiValue.createChoiceValue(Constraint.createConstraint(args[0].toString()), BooleanValue.TRUE, BooleanValue.FALSE);
			}
			else
				return NullValue.NULL; // Should not reach here
		}
		
	}
	
	public static class __ASSERT__ {
		
		public static Value evalImpl(Value[] args, Location location) {
			if (args.length != 1)
				return NullValue.NULL; // Should not reach here
			
			Value assertedValue = args[0];
			
			Constraint falseConstraint = MultiValue.whenFalse(assertedValue);
			if (falseConstraint.isSatisfiable())
				Logging.LOGGER.severe("Assertion Error: " + location.prettyPrint() + ". Asserted value is false when " + falseConstraint.toString());
			
			// Optional:
			Constraint undefinedConstraint = MultiValue.whenUndefined(assertedValue);
			if (undefinedConstraint.isSatisfiable())
				Logging.LOGGER.severe("Assertion Error: " + location.prettyPrint() + ". Asserted value is undefined when " + undefinedConstraint.toString());
			
			return NullValue.NULL;
		}
		
	}
	
	public static class __DEBUG__ {
		
		public static Value evalImpl(Value[] args, Location location) {
			Logging.LOGGER.info("Breakpoint: " + location.prettyPrint());
			
			return NullValue.NULL;
		}
		
	}
	
	public static class __PLUGINS__ {
		
		public static Value evalImpl(Value[] args) {
			return RegressionTest.inst.loadPlugins();
		}
		
	}
	
	/**
	 * @see com.caucho.quercus.lib.VariableModule.is_null(Value)
	 */
	public static class is_null {
		
		public static Value eval(Value arg) {
			if (arg instanceof Var)
				arg = ((Var) arg).getRawValue();
			
			if (arg instanceof MultiValue) {
				Constraint undefinedCases = MultiValue.whenUndefined(arg);
				return MultiValue.createChoiceValue(undefinedCases, BooleanValue.TRUE, BooleanValue.FALSE);
			}
			
			else if (arg instanceof ArrayValueImpl)
				return ArrayValueImpl_.isNull((ArrayValueImpl) arg);
			
			else
				return arg.isNull() ? BooleanValue.TRUE : BooleanValue.FALSE;
		}
		
	}
	
	/**
	 * @see com.caucho.quercus.lib.ArrayModule.in_array(Value, ArrayValue, boolean)
	 */
	public static class in_array {
		
		public static Value eval(Value[] args) {
			Value arg0 = args[0] instanceof Var ? ((Var) args[0]).getRawValue() : args[0];
			Value arg1 = args[1] instanceof Var ? ((Var) args[1]).getRawValue() : args[1];
			Value arg2 = args.length > 2 ? (args[2] instanceof Var ? ((Var) args[2]).getRawValue() : args[2]) : null;
			
			final Value needle = arg0;
			final ArrayValue stack = arg1 instanceof ArrayValue ? (ArrayValue) arg1 : null;
			final boolean strict = arg2 != null ? (arg2 == BooleanValue.TRUE) : false; 
			
			if (stack == null)
				return BooleanValue.FALSE;
			
			Constraint existCond = Constraint.FALSE;
		    for (Entry entry = stack.getHead(); entry != null; entry = entry.getNext()) {
		    	
	    		Value result = MultiValue.operateOnValue(entry.getValue(), new IOperation() {
					@Override
					public Value operate(Value flattenedValue) {
						if (strict)
							return flattenedValue.eql(needle) ? BooleanValue.TRUE : BooleanValue.FALSE;
						else
							return flattenedValue.eq(needle) ? BooleanValue.TRUE : BooleanValue.FALSE;
					}
	    		});
	    		
	    		existCond = Constraint.createOrConstraint(existCond, MultiValue.whenTrue(result));
		    }

		    return MultiValue.createChoiceValue(existCond, BooleanValue.TRUE, BooleanValue.FALSE);
		}
		
	}	
	
	/*
	 * TODO PENDING CHANGES
	 */
	
	/*
	public static class is_callable {
		
		public static Value eval(Value arg, final Env env) {
			if (arg instanceof Var)
				arg = ((Var) arg).getRawValue();
			
			Value retValue = MultiValue.operateOnValue(arg, new IOperation()  {
					@Override
					public Value operate(Value value) {
						if (value instanceof ArrayValueImpl)
							return ArrayValueImpl_.isCallable((ArrayValueImpl) value, env);
						
						return value.isCallable(env) ? BooleanValue.TRUE : BooleanValue.FALSE;
					}
			});

			// TODO Revise
			Constraint undefinedCases = MultiValue.whenUndefined(retValue);
			retValue = MultiValue.createChoiceValue(undefinedCases, BooleanValue.FALSE, retValue);
			
			return retValue;
		}
		
	}
	*/

}
