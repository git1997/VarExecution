package edu.iastate.hungnv.shadow;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.constraint.Constraint;
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

}
