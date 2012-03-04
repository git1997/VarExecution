package edu.iastate.hungnv.scope;

import java.util.HashSet;
import java.util.Set;

import edu.iastate.hungnv.constraint.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class Scope {

	public static final Scope GLOBAL = new Scope(Constraint.TRUE, null);
	
	// Pointer to the outer scope
	private Scope outerScope;	// Can be null (if the current scope is Global)
	
	// The constraint of the current scope
	private Constraint constraint;		
	
	// The dirty values in the current scope
	private HashSet<ScopedValue> dirtyValues = new HashSet<ScopedValue>();
	
	/**
	 * Constructor
	 * @param constraint
	 * @param outerScope	Can be null (if the current scope is Global)
	 */
	public Scope(Constraint constraint, Scope outerScope) {
		this.constraint = constraint;
		this.outerScope = outerScope;
	}
	
	/*
	 * Getters and setters
	 */
	
	/**
	 * @return The outer scope, can be null (if the current scope is Global)
	 */
	public Scope getOuterScope() {
		return outerScope;
	}

	/**
	 * @return The constraint of the current scope
	 * @see edu.iastate.hungnv.scope.Scope.getAggregatedConstraint()
	 */
	public Constraint getConstraint() {
		return constraint;
	}

	public void addDirtyValue(ScopedValue scopedValue) {
		dirtyValues.add(scopedValue);
	}
	
	public Set<ScopedValue> getDirtyValues() {
		return new HashSet<ScopedValue>(dirtyValues);
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * @return The aggregated constraint of the current scope and all its outer scopes.
	 */
	public Constraint getAggregatedConstraint() {
		Constraint aggregatedConstraint = constraint;

		Scope curScope = this;
		while ((curScope = curScope.getOuterScope()) != null) {
			aggregatedConstraint = Constraint.createAndConstraint(curScope.getConstraint(), aggregatedConstraint);
		}
		
		return aggregatedConstraint;
	}
	
	/**
	 * @return A string describing the scope
	 */
	@Override
	public String toString() {
		return (this == GLOBAL ? "GLOBAL" : constraint.toString());
	}
	
}
