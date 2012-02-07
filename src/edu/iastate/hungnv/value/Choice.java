package edu.iastate.hungnv.value;

import java.util.HashMap;
import java.util.Map;

import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class Choice extends MultiValue {

	private Constraint constraint;
	private Value value1;	// A regular value, not null
	private Value value2;	// A regular value, not null
	
	/**
	 * Constructor
	 * @param constraint
	 * @param value1	A regular value, not null
	 * @param value2	A regular value, not null
	 */
	public Choice(Constraint constraint, Value value1, Value value2) {
		this.constraint = constraint;
		this.value1 = value1;
		this.value2 = value2;
	}
	
	/*
	 * Getters and setters
	 */
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	public Value getValue1() {
		return value1;
	}
	
	public Value getValue2() {
		return value2;
	}
	
	/*
	 * Methods
	 */
	
	@Override
	public Value getRepresentativeValue() {
		if (value1 instanceof MultiValue)
			return ((MultiValue) value1).getRepresentativeValue();
		else
			return value1;
	}
	
	@Override
	public Map<Value, Constraint> getAllPossibleValues() {
		Map<Value, Constraint> map = new HashMap<Value, Constraint>();
		
		Map<Value, Constraint> map1 = MultiValue.getAllPossibleValues(value1);
		Map<Value, Constraint> map2 = MultiValue.getAllPossibleValues(value2);
		
		for (Value value : map1.keySet()) {
			Constraint constraint = map1.get(value);
			map.put(value, Constraint.createAndConstraint(this.constraint, constraint));
		}
		
		Constraint notConstraint = Constraint.createNotConstraint(this.constraint);
		for (Value value : map2.keySet()) {
			Constraint constraint = map2.get(value);
			map.put(value, Constraint.createAndConstraint(notConstraint, constraint));
		}
		
		return map;
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return "CHOICE(" + constraint.toString() + ", " + value1.toString() + ", " + value2.toString() + ")";
	}
	
}
