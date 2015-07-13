package edu.iastate.hungnv.empiricalstudy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.caucho.quercus.env.ArrayValueImpl;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.ObjectExtValue;
import com.caucho.quercus.env.StringBuilderValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.ObjectExtValue.EntrySet;
import com.caucho.quercus.statement.Statement;

import de.fosd.typechef.featureexpr.FeatureExpr;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.constraint.Constraint.Result;
import edu.iastate.hungnv.debug.OutputViewer;
import edu.iastate.hungnv.util.FileIO;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.Choice;
import edu.iastate.hungnv.value.Concat;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Switch;
import edu.iastate.hungnv.value.Undefined;

/**
 * 
 * @author HUNG
 *
 */
public class EmpiricalStudy {
	
	public static final String traceTxtFile 	= "C:\\Users\\HUNG\\Desktop\\eval-trace.txt";
	public static final String heapTxtFile 		= "C:\\Users\\HUNG\\Desktop\\eval-heap.txt";
	public static final String outputTxtFile 	= "C:\\Users\\HUNG\\Desktop\\eval-output.txt";
	
	/**
	 * Static instance of EmpiricalStudy
	 */
	public static EmpiricalStudy inst = new EmpiricalStudy();
    
    private ArrayList<String> statements;
    private ArrayList<String> values;
    private ArrayList<String> outputValues;
	
	/**
	 * Called when Env is started.
	 */
    public void envStarted() {
    	statements = new ArrayList<String>();
    	values = new ArrayList<String>();
    	outputValues = new ArrayList<String>();
    }
    
    /**
     * Called when Env is closed.
     */
    public void envClosed(Env env) {
    	/*
    	 * Record values
    	 */
		for (NameValuePair pair : getNameValuePairsFromEnv(env)) {
			recordNameValuePair(pair.getName(), pair.getValue(), 0, Constraint.TRUE, values);
		}
		
		/*
		 * Record output values
		 */
		recordOutputValue(OutputViewer.inst.getFinalOutputValue(), Constraint.TRUE, outputValues);
		
    	/*
    	 * Write results to files
    	 */
    	writeListToFile(statements, traceTxtFile);
    	writeListToFile(values, heapTxtFile);
    	writeListToFile(outputValues, outputTxtFile);
    }
    
    /**
     * Records a statement
     */
    public void recordStatement(Statement statement, Env env) {
    	String location = statement.getLocation().prettyPrint();
    	FeatureExpr featureExpr = env.getEnv_().getScope().getConstraint().getFeatureExpr();
    	int featureCount = featureExpr.collectDistinctFeatures().size();
    	
    	statements.add(location + " # " + featureExpr + " # " + featureCount);
    }
    
    /**
     * Records a name-value pair
     */
    private void recordNameValuePair(String name, Value value, int depth, Constraint constraint, ArrayList<String> values) {
    	value = MultiValue.simplify(value, constraint);
    	
    	String valueStr = getAbbreviatedString(value);
    	String valueType = value.getClass().getSimpleName();
    	
    	HashSet<String> featureSet = getFeatureSet(value);
    	String featureSetStr = getFeatureSetString(featureSet);
    	int featureCount = featureSet.size();
    	int alternativeValueCount = countAlternativeValues(value);
    	
    	boolean isString = isString(value);
    	int size = depth == 0 ? (isString ? -1 : computeSize(value)) : -1;
    	int minSize = depth == 0 ? (isString ? -1 : computeMinSize(value)) : -1;
    	int maxSize = depth == 0 ? (isString ? -1 : computeMaxSize(value)) : -1;
    	
    	int stringSize = depth == 0 ? (isString ? computeStringSize(value) : -1) : -1;
    	int minStringSize = depth == 0 ? (isString ? computeMinStringSize(value) : -1) : -1;
    	int maxStringSize = depth == 0 ? (isString ? computeMaxStringSize(value) : -1) : -1;
    	
    	values.add(name + " # " 
    			+ valueStr + " # "
    			+ valueType + " # "
    			+ depth + " # "
    			+ featureSetStr + " # " 
    			+ featureCount + " # " 
    			+ alternativeValueCount + " # "
    			+ (size < 0 ? "-" : String.valueOf(size)) + " # "
    			+ (minSize < 0 ? "-" : String.valueOf(minSize)) + " # "
    			+ (maxSize < 0 ? "-" : String.valueOf(maxSize)) + " # "
    			+ (stringSize < 0 ? "-" : String.valueOf(stringSize)) + " # "
    			+ (minStringSize < 0 ? "-" : String.valueOf(minStringSize))	+ " # "
    			+ (maxStringSize < 0 ? "-" : String.valueOf(maxStringSize))
    	);
    	
		findNextNameValuePairs(value, depth, constraint, values);
    }
    
    /**
     * Finds name-value pairs at deeper levels
     */
    private void findNextNameValuePairs(Value value, int depth, Constraint constraint, ArrayList<String> values) {
	    if (value instanceof Concat) {
			// Do nothing
		}
	    
		else if (value instanceof Choice) {
			Constraint trueConstraint = Constraint.createAndConstraint(constraint, ((Choice) value).getConstraint());
			if (trueConstraint.isSatisfiable())
				findNextNameValuePairs(((Choice) value).getValue1(), depth, trueConstraint, values);
			
			Constraint falseConstraint = Constraint.createAndConstraint(constraint, Constraint.createNotConstraint(((Choice) value).getConstraint()));
			if (falseConstraint.isSatisfiable())
				findNextNameValuePairs(((Choice) value).getValue2(), depth, falseConstraint, values);
		}
	    
		else if (value instanceof Switch) {
			for (Case case_ : (Switch) value) {
				Constraint caseConstraint =  Constraint.createAndConstraint(constraint, case_.getConstraint());
				if (caseConstraint.isSatisfiable())
					findNextNameValuePairs(case_.getValue(), depth, caseConstraint, values);
			}
		}
	    
		else if (value instanceof Undefined) {
			// Do nothing
		}
	    
		else if (value instanceof ArrayValueImpl) {
	    	for (NameValuePair pair : getNameValuePairsFromArray((ArrayValueImpl) value)) {
				recordNameValuePair(pair.getName(), pair.getValue(), depth + 1, constraint, values);
			}
		}
	    
		else if (value instanceof ObjectExtValue) {
	    	for (NameValuePair pair : getNameValuePairsFromObject((ObjectExtValue) value)) {
				recordNameValuePair(pair.getName(), pair.getValue(), depth + 1, constraint, values);
			}
		}
	    
		else {
			// Do nothing
		}
    }
    
    /**
     * Returns a string describing a value.
     */
    private String getAbbreviatedString(Value value) {
    	String valueStr = value.toString();
    	
    	if (valueStr.length() > 20)
    		valueStr = valueStr.substring(0, 20) + "...";
    	
    	valueStr = valueStr.replace("\r\n", " ").replace("\n", " ").replace("#", " ");
    	
    	return valueStr;
    }
    
    /**
     * Returns the features that a given value depends on.
     */
    private HashSet<String> getFeatureSet(Value value) {
    	HashSet<String> features = new HashSet<String>();
    	
    	for (Case case_ : flattenValue(value)) {
    		FeatureExpr featureExpr = case_.getConstraint().getFeatureExpr();
    		for (scala.collection.Iterator<String> iter = featureExpr.collectDistinctFeatures().iterator(); iter.hasNext(); )
    			features.add(iter.next());
    	}
    	
    	return features;
    }
    
    /**
     * Returns a string that describes a feature set.
     */
    private String getFeatureSetString(HashSet<String> featureSet) {
    	StringBuilder str = new StringBuilder();
    	for (String s : featureSet) {
    		str.append("[" + s + "]");
    	}
    	return str.toString();
    }
    
    /**
     * Counts the number of alternative values that a given value has.
     */
    private int countAlternativeValues(Value value) {
    	Switch switch_ = flattenValue(value);
    	Constraint whenUndefined = MultiValue.whenUndefined(switch_);
    	
    	if (whenUndefined.isSatisfiable())
    		return switch_.getCases().size() + 1;
    	else
    		return switch_.getCases().size();
    }
    
    /**
     * Returns true if the value is a (possibly multi-value) string
     */
    private boolean isString(Value value) {
    	// TODO Revise
    	
    	return (value instanceof Concat) || (value instanceof StringValue);
    }
    
    /**
     * Computes the size of a given value.
     */
    private int computeSize(Value value) {
    	int size = 0;
    	
    	for (Case case_ : flattenValue(value)) {
    		Value flattenedValue = case_.getValue();
    		
    		if (flattenedValue instanceof ArrayValueImpl) {
    			size += computeArraySize((ArrayValueImpl) flattenedValue);
    		}
    		else if (flattenedValue instanceof ObjectExtValue) {
    			size += computeObjectSize((ObjectExtValue) flattenedValue);
    		}
    		else { // PrimitiveValue
    			size++;
    		}
    	}
    	
    	return size;
    }
    
    private int computeArraySize(ArrayValueImpl array) {
    	int size = 1;
		
    	for (Iterator<Map.Entry<Value, Value>> iter = array.getIterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			size += computeSize(pair.getValue());
		}
		
		return size;
    }
    
    private int computeObjectSize(ObjectExtValue object) {
    	int size = 1;
    	
		for (Iterator<Map.Entry<Value,Value>> iter = ((EntrySet) object.entrySet()).iterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			size += computeSize(pair.getValue());
		}
		
		return size;
    }
    
    /**
     * Computes the min-size of a given value.
     */
    private int computeMinSize(Value value) {
    	int minSize = 0;
    	
    	for (Case case_ : flattenValue(value)) {
    		Value flattenedValue = case_.getValue();
    		
    		int size;
    		if (flattenedValue instanceof ArrayValueImpl) {
    			size = computeMinArraySize((ArrayValueImpl) flattenedValue);
    		}
    		else if (flattenedValue instanceof ObjectExtValue) {
    			size = computeMinObjectSize((ObjectExtValue) flattenedValue);
    		}
    		else { // PrimitiveValue
    			size = 1;
    		}
    		
    		if (minSize == 0 || size < minSize)
    			minSize = size;
    	}
    	
    	return minSize;
    }
    
    private int computeMinArraySize(ArrayValueImpl array) {
    	int size = 1;
		
    	for (Iterator<Map.Entry<Value, Value>> iter = array.getIterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			size += computeMinSize(pair.getValue());
		}
		
		return size;
    }
    
    private int computeMinObjectSize(ObjectExtValue object) {
    	int size = 1;
    	
		for (Iterator<Map.Entry<Value,Value>> iter = ((EntrySet) object.entrySet()).iterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			size += computeMinSize(pair.getValue());
		}
		
		return size;
    }
    
    /**
     * Computes the max-size of a given value.
     */
    private int computeMaxSize(Value value) {
    	int maxSize = 0;
    	
    	for (Case case_ : flattenValue(value)) {
    		Value flattenedValue = case_.getValue();
    		
    		int size;
    		if (flattenedValue instanceof ArrayValueImpl) {
    			size = computeMaxArraySize((ArrayValueImpl) flattenedValue);
    		}
    		else if (flattenedValue instanceof ObjectExtValue) {
    			size = computeMaxObjectSize((ObjectExtValue) flattenedValue);
    		}
    		else { // PrimitiveValue
    			size = 1;
    		}
    		
    		if (size > maxSize)
    			maxSize = size;
    	}
    	
    	return maxSize;
    }
    
    private int computeMaxArraySize(ArrayValueImpl array) {
    	int size = 1;
		
    	for (Iterator<Map.Entry<Value, Value>> iter = array.getIterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			size += computeMaxSize(pair.getValue());
		}
		
		return size;
    }
    
    private int computeMaxObjectSize(ObjectExtValue object) {
    	int size = 1;
    	
		for (Iterator<Map.Entry<Value,Value>> iter = ((EntrySet) object.entrySet()).iterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			size += computeMaxSize(pair.getValue());
		}
		
		return size;
    }
    
    /**
     * Computes the string size of a given value.
     */
    private int computeStringSize(Value value) {
    	int size = 0;
    	
    	if (value instanceof Concat) {
    		for (Value childValue : (Concat) value) {
    			size += computeStringSize(childValue);
    		}  
    	}
    	else if (value instanceof Choice) {
    		size += computeStringSize(((Choice) value).getValue1());
    		size += computeStringSize(((Choice) value).getValue2());
    	}
    	else if (value instanceof Switch) {
	    	for (Case case_ : (Switch) value) {
	    		Value flattenedValue = case_.getValue();
	    		size += computeStringSize(flattenedValue);
	    	}
    	}
	    else if (value instanceof Undefined) {
	    	// Do nothing
	    }
	    else if (value instanceof ArrayValueImpl) {
   			System.err.println("In EmpiricalStudy.java: value cannot be ArrayValueImpl.");
   			System.exit(0); // This can't happen
   		}
	    else if (value instanceof ObjectExtValue) {
   			System.err.println("In EmpiricalStudy.java: value cannot be ObjectExtValue.");
   			System.exit(0); // This can't happen
   		}
	    else { // PrimitiveValue
	    	size += value.toString().length();
	    }
    	
    	return size;
    }
    
    /**
     * Computes the min-string-size of a given value.
     */
    private int computeMinStringSize(Value value) {
    	int minSize = -1;
    	
    	if (value instanceof Concat) {
    		for (Value childValue : (Concat) value) {
    			minSize += computeMinStringSize(childValue);
    		}  
    	}
    	else if (value instanceof Choice) {
    		int size1 = computeMinStringSize(((Choice) value).getValue1());
    		int size2 = computeMinStringSize(((Choice) value).getValue2());
    		if (minSize == -1 || size1 < minSize)
    			minSize = size1;
    		if (minSize == -1 || size2 < minSize)
    			minSize = size2;
    	}
    	else if (value instanceof Switch) {
	    	for (Case case_ : (Switch) value) {
	    		Value flattenedValue = case_.getValue();
	    		int size = computeMinStringSize(flattenedValue);
	    		if (minSize == -1 || size < minSize)
	    			minSize = size;
	    	}
    	}
	    else if (value instanceof Undefined) {
	    	// Do nothing
	    }
	    else if (value instanceof ArrayValueImpl) {
   			System.err.println("In EmpiricalStudy.java: value cannot be ArrayValueImpl.");
   			System.exit(0); // This can't happen
   		}
	    else if (value instanceof ObjectExtValue) {
   			System.err.println("In EmpiricalStudy.java: value cannot be ObjectExtValue.");
   			System.exit(0); // This can't happen
   		}
	    else { // PrimitiveValue
	    	minSize = value.toString().length();
	    }
    	
    	if (minSize == -1)
    		minSize = 0;
    	
    	return minSize;
    }
    
    /**
     * Computes the max-string-size of a given value.
     */
    private int computeMaxStringSize(Value value) {
    	int maxSize = 0;
    	
    	if (value instanceof Concat) {
    		for (Value childValue : (Concat) value) {
    			maxSize += computeMaxStringSize(childValue);
    		}  
    	}
    	else if (value instanceof Choice) {
    		int size1 = computeMaxStringSize(((Choice) value).getValue1());
    		int size2 = computeMaxStringSize(((Choice) value).getValue2());
    		if (size1 > maxSize)
    			maxSize = size1;
    		if (size2 > maxSize)
    			maxSize = size2;
    	}
    	else if (value instanceof Switch) {
	    	for (Case case_ : (Switch) value) {
	    		Value flattenedValue = case_.getValue();
	    		int size = computeMaxStringSize(flattenedValue);
	    		if (size > maxSize)
	    			maxSize = size;
	    	}
    	}
	    else if (value instanceof Undefined) {
	    	// Do nothing
	    }
	    else if (value instanceof ArrayValueImpl) {
   			System.err.println("In EmpiricalStudy.java: value cannot be ArrayValueImpl.");
   			System.exit(0); // This can't happen
   		}
	    else if (value instanceof ObjectExtValue) {
   			System.err.println("In EmpiricalStudy.java: value cannot be ObjectExtValue.");
   			System.exit(0); // This can't happen
   		}
	    else { // PrimitiveValue
	    	maxSize = value.toString().length();
	    }
    	
    	return maxSize;
    }
    
    /**
     * Records output value
     */
    private void recordOutputValue(Value value, Constraint constraint, ArrayList<String> outputValues) {
    	value = MultiValue.simplify(value, constraint);
    	
	    if (value instanceof Concat) {
    		for (Value childValue : (Concat) value)
    			recordOutputValue(childValue, constraint, outputValues);
    		return;
		}
	    
		else if (value instanceof Choice) {
			Constraint trueConstraint = Constraint.createAndConstraint(constraint, ((Choice) value).getConstraint());
			Value trueValue = ((Choice) value).getValue1();
			if (trueConstraint.isSatisfiable())
				recordOutputValue(trueValue, trueConstraint, outputValues);
			
			Constraint falseConstraint = Constraint.createAndConstraint(constraint, Constraint.createNotConstraint(((Choice) value).getConstraint()));
			Value falseValue = ((Choice) value).getValue2();
			if (falseConstraint.isSatisfiable())
				recordOutputValue(falseValue, falseConstraint, outputValues);
			
			return;
		}
	    
		else if (value instanceof Switch) {
			value = groupSameStringValuesInSwitch((Switch) value, constraint);
			
			Concat concat = trySimplifyingSwitchToConcat((Switch) value, constraint);
			if (concat != null) {
				recordOutputValue(concat, constraint, outputValues);
				return;
			}
			
			Choice choice = trySimplifyingSwitchToChoice((Switch) value, constraint);
			if (choice != null) {
				recordOutputValue(choice, constraint, outputValues);
				return;
			}
			
			for (Case case_ : (Switch) value) {
				Constraint caseConstraint =  Constraint.createAndConstraint(constraint, case_.getConstraint());
				if (caseConstraint.isSatisfiable())
					recordOutputValue(case_.getValue(), caseConstraint, outputValues);
			}
			return;
		}
	    
		else if (value instanceof Undefined) {
			// Do nothing
			return;
		}
	    
		else if (value instanceof ArrayValueImpl) {
   			System.err.println("In EmpiricalStudy.java: value cannot be ArrayValueImpl.");
   			System.exit(0); // This can't happen
		}
	    
		else if (value instanceof ObjectExtValue) {
   			System.err.println("In EmpiricalStudy.java: value cannot be ObjectExtValue.");
   			System.exit(0); // This can't happen
		}
	    
		else { // PrimitiveValue
	    	String valueStr = getAbbreviatedString(value);
	    	String valueType = value.getClass().getSimpleName();
	    	
	    	FeatureExpr featureExpr = constraint.getFeatureExpr();
	    	String featureExprStr = featureExpr.toString();
	    	int featureCount = featureExpr.collectDistinctFeatures().size();
	    	
	    	int stringSize = value.toString().length();
	    	
	    	outputValues.add(valueStr + " # "
	    			+ valueType + " # "
	    			+ featureExprStr + " # " 
	    			+ featureCount + " # " 
	    			+ String.valueOf(stringSize)
	    	);
	    	
	    	return;
		}
    }
    
    /**
     * Groups the same string values in a Switch.
     */
    private Switch groupSameStringValuesInSwitch(Switch switch_, Constraint constraint) {
		HashMap<String, Constraint> map = new HashMap<String, Constraint>();
		for (Case case_ : switch_) {
			String string = case_.getValue().toString();
			if (map.containsKey(string))
				map.put(string, Constraint.createOrConstraint(map.get(string), case_.getConstraint()));
			else
				map.put(string, case_.getConstraint());
		}
		
		Switch simplifiedSwitch = new Switch();
		for (String string : map.keySet()) {
			simplifiedSwitch.addCase(new Case(map.get(string), new StringBuilderValue(string)));
		}
    	return simplifiedSwitch;
    }
    
    /**
     * Tries simplifying a Switch to a Concat.
     * Returns null if it is not possbile.
     */
    private Concat trySimplifyingSwitchToConcat(Switch switch_, Constraint constraint) {
    	if (constraint.tryAddingConstraint(MultiValue.whenUndefined(switch_)) != Result.ALWAYS_FALSE)
    		return null;
    	
    	String commonFirstPart = null;
    	for (Case case_ : switch_) {
    		String string = case_.getValue().toString();
    		if (commonFirstPart == null)
    			commonFirstPart = string;
    		else {
    			for (int i = 0; i < commonFirstPart.length(); i++)
    				if (i >= string.length() || commonFirstPart.charAt(i) != string.charAt(i)) {
    					commonFirstPart = commonFirstPart.substring(0, i);
    					break;
    			}
    		}
    	}

    	String commonLastPart = null;
    	for (Case case_ : switch_) {
    		String string = case_.getValue().toString();
    		if (commonLastPart == null)
    			commonLastPart = string;
    		else {
    			for (int i = 0; i < commonLastPart.length(); i++)
    				if (string.length() - 1 - i < 0 || commonLastPart.charAt(commonLastPart.length() - 1 - i) != string.charAt(string.length() - 1 - i)) {
    					commonLastPart = commonLastPart.substring(commonLastPart.length() - i);
    					break;
    			}
    		}
    	}
    	
    	if (commonFirstPart.isEmpty() && commonLastPart.isEmpty())
    		return null;
    	
    	Switch newSwitch = new Switch();
    	for (Case case_ : switch_) {
    		String string = case_.getValue().toString();
    		String newString = string.substring(commonFirstPart.length(), string.length() - commonLastPart.length());
    		
    		newSwitch.addCase(new Case(case_.getConstraint(), new StringBuilderValue(newString)));
    	}
    	
    	Value part1 = new StringBuilderValue(commonFirstPart);
    	Value part2 = MultiValue.createSwitchValue(newSwitch);
    	Value part3 = new StringBuilderValue(commonLastPart);
    	
    	return (Concat) (MultiValue.createConcatValue(MultiValue.createConcatValue(part1, part2), part3));
    }
    
    /**
     * Tries simplifying a Switch to a Choice.
     * Returns null if it is not possbile.
     */
    private Choice trySimplifyingSwitchToChoice(Switch switch_, Constraint constraint) {
    	// TODO Revise
    	
    	Constraint set1Constraint = Constraint.FALSE;
    	HashSet<Case> set1Cases = new HashSet<Case>();
    	HashSet<Case> set2Cases = new HashSet<Case>();
    	
    	for (Case case_ : switch_) {
    		String string = case_.getValue().toString();
    		if (string.contains("[my_calendar]")) { // ADHOC Partition a Switch based on the value [my_calendar]
    			set1Constraint = Constraint.createOrConstraint(set1Constraint, case_.getConstraint());
    			set1Cases.add(case_);
    		}
    		else
    			set2Cases.add(case_);
    	}
    	
    	if (constraint.tryAddingConstraint(set1Constraint) != Result.UNDETERMINED)
    		return null;
    	
    	Switch switch1 = new Switch();
    	for (Case case_ : set1Cases) {
    		switch1.addCase(case_);
    	}
    	
    	Switch switch2 = new Switch();
    	for (Case case_ : set2Cases) {
    		switch2.addCase(case_);
    	}
    	
    	Value part1 = MultiValue.createSwitchValue(switch1);
    	Value part2 = MultiValue.createSwitchValue(switch2);
    	
    	return (Choice) MultiValue.createChoiceValue(set1Constraint, part1, part2);
    }
    
    /*
     * Utility methods
     */
    
    // Cache the flattenedValues to increase performance
    private HashMap<Value, Switch> flattenedValues = new HashMap<Value, Switch>();
    
    private Switch flattenValue(Value value) {
    	if (!flattenedValues.containsKey(value)) {
    		Switch originalSwitch = MultiValue.flatten(value);
    		
    		/*
    		 * Simplify the Switch
    		 */
    		HashMap<Value, Constraint> map = new HashMap<Value, Constraint>();
    		for (Case case_ : originalSwitch) {
    			if (map.containsKey(case_.getValue()))
    				map.put(case_.getValue(), Constraint.createOrConstraint(map.get(case_.getValue()), case_.getConstraint()));
    			else
    				map.put(case_.getValue(), case_.getConstraint());
    		}
    		
    		Switch simplifiedSwitch = new Switch();
    		for (Value value_ : map.keySet()) {
    			simplifiedSwitch.addCase(new Case(map.get(value_), value_));
    		}
    		
    		flattenedValues.put(value, simplifiedSwitch);
    	}
    	
    	return flattenedValues.get(value);
    }
    
    private ArrayList<NameValuePair> getNameValuePairsFromEnv(Env env) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		for (StringValue name : env.getEnv().keySet()) {
			Value value = env.getEnv().get(name).get();
			pairs.add(new NameValuePair(name.toString(), value));
		}
		Collections.sort(pairs, SortNameValuePairByName.inst);
		
		return pairs;
    }
    
    private ArrayList<NameValuePair> getNameValuePairsFromArray(ArrayValueImpl array) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		for (Iterator<Map.Entry<Value, Value>> iter = array.getIterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			pairs.add(new NameValuePair(pair.getKey().toString(), pair.getValue()));
		}
		Collections.sort(pairs, SortNameValuePairByName.inst);
		
		return pairs;
    }
    
    private ArrayList<NameValuePair> getNameValuePairsFromObject(ObjectExtValue object) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		for (Iterator<Map.Entry<Value,Value>> iter = ((EntrySet) object.entrySet()).iterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			pairs.add(new NameValuePair(pair.getKey().toString(), pair.getValue()));
		}
		Collections.sort(pairs, SortNameValuePairByName.inst);
		
		return pairs;
    }
    
	/**
	 * Class NameValuePair
	 */
	private class NameValuePair {
		
		private String name;
		private Value value;
		
		public NameValuePair(String name, Value value) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		
		public Value getValue() {
			return value;
		}
		
	}
	
	/**
	 * Helper class to support sorting of NameValuePairs
	 */
	private static class SortNameValuePairByName implements Comparator<NameValuePair> {

		public static SortNameValuePairByName inst = new SortNameValuePairByName();
		
		@Override
		public int compare(NameValuePair pair1, NameValuePair pair2) {
			return pair1.getName().compareTo(pair2.getName());
		}

	}
    
    private void writeListToFile(ArrayList<String> list, String file) {
    	StringBuilder str = new StringBuilder();
    	for (String line : list) {
    		str.append(line + System.lineSeparator());
    	}
    	FileIO.writeStringToFile(str.toString(), file);
    }
    
}
