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
	 * @param oldValue	A regular value or a ScopedValue, not null
	 * @param newValue	A regular value, not null
	 * @return a ScopedValue with scoping information
	 */
	public static ScopedValue addScopedValue(Value oldValue, Value newValue) {
		Env_ env_ = Env.getInstance().getEnv_();
		
		ScopedValue oldScopedValue;
		if (oldValue instanceof ScopedValue)
			oldScopedValue = (ScopedValue) oldValue;
		else
			oldScopedValue = new ScopedValue(Scope.GLOBAL, oldValue, null);
		
		Scope curentScope = env_.getScope();
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
		
		if (Env_.hasStarted()) {
			value = addScopedValue(envVar.get(), value);
			
			Logging.LOGGER.info("Assign $" + name + " with " + ((ScopedValue) value).toStringWithScoping());
		}
				
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
	
	/**
	 * Returns true if the program has started execution
	 */
	public static boolean hasStarted() {
		return (Env.getInstance() != null);
	}
	
}