package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.ArrayValue;
import com.caucho.quercus.env.ArrayValueImpl;
import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class ArrayValueImpl_ {
	
	/**
	 * @see com.caucho.quercus.env.ArrayValueImpl.append(Value, Value)
	 */
	public static ArrayValue append(Value key, Value value, final ArrayValueImpl _this) {
		if (key instanceof MultiValue) {
			// Convert Array(CHOICE(Cond, x, y) => z) into Array(x => CHOICE(Cond, z, UNDEFINED), y => CHOICE(!Cond, z, UNDEFINED))
			for (Case case_ : ((MultiValue) key).flatten()) {
				Value flattenedKey = case_.getValue();
				Constraint constraint = case_.getConstraint();
				
				Value modifiedValue = MultiValue.createChoiceValue(constraint, value, _this.get(flattenedKey));
				
				// Eval basic case
				_this.append_basic(flattenedKey, modifiedValue);
			}
			
			return _this;
		}
		else
			return _this.append_basic(key, value);
	}

}
