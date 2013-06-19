package edu.iastate.hungnv.shadow;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.ArrayValueImpl;
import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.regressiontest.RegressionTest;
import edu.iastate.hungnv.util.Logging;
import edu.iastate.hungnv.value.MultiValue;

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
