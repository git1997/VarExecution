package edu.iastate.hungnv.scope;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class ScopedValue extends Value {
	
	// Pointer to the outer ScopedValue (could be several scopes apart)
	private ScopedValue outerScopedValue; // Null if scope = GLOBAL, Not null if scope != GLOBAL
	
	// The current scope
	private Scope scope;	// Not null
	
	// The current (regular) value
	private Value value;	// A regular value, not null
	
	/**
	 * Constructor
	 * @param scope				Not null
	 * @param value				A regular value, not null
	 * @param outerScopedValue	Null if scope = GLOBAL, Not null if scope != GLOBAL
	 */
	public ScopedValue(Scope scope, Value value, ScopedValue outerScopedValue) {
		setScope(scope);
		setValue(value);
		setOuterScopedValue(outerScopedValue);
	}
	
	/*
	 * Getters and setters
	 */
	
	/**
	 * Returns the outer ScopedValue
	 * Note: the current ScopedValue and the outerScopedValue could be several scopes apart.
	 * @return The outer ScopedValue, null if scope = GLOBAL, Not null if scope != GLOBAL
	 */
	public ScopedValue getOuterScopedValue() {
		return outerScopedValue;
	}
	
	/**
	 * Sets the outer ScopedValue
	 * @param outerScopedValue	Null if scope = GLOBAL, Not null if scope != GLOBAL
	 */
	public void setOuterScopedValue(ScopedValue outerScopedValue) {
		this.outerScopedValue = outerScopedValue;
	}
	
	/**
	 * @return The current scope, not null
	 */
	public Scope getScope() {
		return scope;
	}
	
	/**
	 * Sets the current scope
	 * @param scope	Not null
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	/**
	 * Returns the (regular) value in the current scope
	 * @return 	A regular value, not null
	 */
	public Value getValue() {
		return value;
	}
	
	/**
	 * Sets the (regular) value in the current scope
	 * @param value		A regular value, not null
	 */
	public void setValue(Value value) {
		this.value = value;
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * @return A string describing the ScopedValue (with scoping information)
	 */
	public String toStringWithScoping() {
		return scope.toString() 
				+ " => " + (value == com.caucho.quercus.env.NullValue.NULL ? "NullValue" : value.toString())  
				+ (outerScopedValue != null ? ("; " + outerScopedValue.toStringWithScoping()) : "");
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	@Override
	public Value toAutoArray() {
		return value.toAutoArray();
	}
	
	@Override
	public Value putField(Env env, StringValue name, Value object) {
	    return value.putField(env, name, object);
	}
	
	@Override
	public Value get(Value index) {
		return value.get(index);
	}
	
}
