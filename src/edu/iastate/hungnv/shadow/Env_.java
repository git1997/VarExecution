package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.EnvVar;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.scope.Scope;
import edu.iastate.hungnv.scope.ScopedValue;
import edu.iastate.hungnv.util.Logging;
import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class Env_ {
	
	// Turn on or off instrumentation mode
	public static final boolean INSTRUMENT = true;
	
	// The current scope
	private Scope scope;
	
	/**
	 * Constructor
	 */
	public Env_() {
		this.scope = Scope.GLOBAL;
	}
	
	/*
	 * Getters & setters
	 */
	
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	/*
	 * Handling starting and closing events of the Env
	 */
	
	public void start(Env env) {
	}
	
	public void close(Env env) {
//		for (StringValue name : env.getEnv().keySet()) {
//			EnvVar envVar = env.getEnv().get(name);
//			Value value = envVar.get();
//			if (value instanceof MultiValue)
//				Logging.LOGGER.info("$" + name + " = " + value);
//		}
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/*
	 * Handling scopes
	 */
	
	/**
	 * @return True if the new scope has a satisfiable aggregated constraint
	 */
	public boolean canEnterNewScope(Constraint constraint) {
		Constraint aggregatedConstraint = Constraint.createAndConstraint(scope.getAggregatedConstraint(), constraint);
		return aggregatedConstraint.isSatisfiable();
	}
	
	/**
	 * Enters a new scope with a given constraint.
	 * @param constraint
	 */
	public void enterNewScope(Constraint constraint) {
		Scope newScope = new Scope(constraint, scope);
		this.scope = newScope;
	}
	
	/**
	 * Exits from the current scope to the outer scope.
	 * Also combine the values that have been modified in the current scope with their original values in the outer scope. 
	 */
	public void exitScope() {
		Scope outerScope = scope.getOuterScope();

		// Combine the values that have been modified in the current scope with their original values in the outer scope
		for (ScopedValue scopedValue : scope.getDirtyValues()) {
			Constraint scopeConstraint = scope.getConstraint();

			Value inScopeValue = scopedValue.getValue();
			Value outScopeValue = scopedValue.getOuterScopedValue().getValue();
			
			scopedValue.setScope(outerScope);
			scopedValue.setValue(MultiValue.createChoiceValue(scopeConstraint, inScopeValue, outScopeValue));
			
			if (scopedValue.getOuterScopedValue().getScope() == outerScope)
				scopedValue.setOuterScopedValue(scopedValue.getOuterScopedValue().getOuterScopedValue());
			else {
				// Do nothing, i.e. scopedValue.setOuterScopedValue(scopedValue.getOuterScopedValue());
			}
			
			outerScope.addDirtyValue(scopedValue);
		}
		
		// Return to the outer scope
		this.scope = outerScope;
	}
	
	/*
	 * Handling ScopedValues
	 */
	
	/**
	 * Creates a ScopedValue (with scoping information) when an oldValue is updated to a newValue, 
	 * 		so that the oldValue can be cached for later use.
	 * If the current scope is GLOBAL, return a regular value (this is to avoid 
	 * 		creating too many ScopedValues for values in the GLOBAL scope).
	 * @param oldValue	A regular value or a ScopedValue, not null
	 * @param newValue	A regular value, not null
	 * @return a ScopedValue with scoping information, or a regular value if the current scope is GLOBAL
	 */
	public Value addScopedValue(Value oldValue, Value newValue) {
		if (newValue instanceof ScopedValue) {
			Logging.LOGGER.warning("In Env_.addScopedValue: newValue must not be a ScopedValue. Please debug.");
		}
		
		if (this.getScope() == Scope.GLOBAL)
			return newValue;
		
		ScopedValue oldScopedValue;
		if (oldValue instanceof ScopedValue)
			oldScopedValue = (ScopedValue) oldValue;
		else
			oldScopedValue = new ScopedValue(Scope.GLOBAL, oldValue, null);
		
		Scope curentScope = this.getScope();
		Value currentValue = newValue;
		
		ScopedValue outerScopedValue;
		if (oldScopedValue.getScope() == curentScope)
			outerScopedValue = oldScopedValue.getOuterScopedValue();
		else 
			outerScopedValue = oldScopedValue;
		
		ScopedValue scopedValue = new ScopedValue(curentScope, currentValue, outerScopedValue);
		
		curentScope.addDirtyValue(scopedValue);
		
		return scopedValue;
	}
	
	/**
	 * Returns a regular value (without scoping information)
	 * @param value		A regular value or a ScopedValue, not null
	 * @return 	A regular value (without scoping information)
	 */
	public static Value removeScopedValue(Value value) {
		if (value instanceof ScopedValue) 
			return ((ScopedValue) value).getValue();
		else
			return value;
	}
	
	/*
	 * Shadowed methods
	 */
	  
	/**
	 * @see com.caucho.quercus.env.Env.setValue(StringValue, Value)
	 */
	public static Value setValue(StringValue name, Value value, Env env) {
		EnvVar envVar = env.getEnvVar(name);
		
		value = env.getEnv_().addScopedValue(envVar.get(), value);
		
		Logging.LOGGER.info("Assign $" + name + " with " + (value instanceof ScopedValue ? ((ScopedValue) value).toStringWithScoping() : value.toString()));
				
		envVar.set(value);
		
		return value;
	}
	
	/**
	 * @see com.caucho.quercus.env.Env.getValue(StringValue, boolean, boolean)
	 */
	public static Value getValue(StringValue name, boolean isAutoCreate, boolean isOutputNotice, Env env) {
	    EnvVar var = env.getEnvVar(name, isAutoCreate, isOutputNotice);
	    
	    if (var != null)
	    	return Env_.removeScopedValue(var.get());
	    else
	    	return NullValue.NULL;
	}
	
	/*
	 * Experimental methods
	 */
	
}