package edu.iastate.hungnv.constraint;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.bdd.BDDFeatureExprFactory$;

/**
 * 
 * @author HUNG
 *
 */
public class Constraint {
	
	// Use the JavaBDD library instead of Sat4j 
	static {
		FeatureExprFactory.setDefault(BDDFeatureExprFactory$.MODULE$);
	}
	
	public static final Constraint TRUE	 = new Constraint(FeatureExprFactory.True());
	
	public static final Constraint FALSE = new Constraint(FeatureExprFactory.False());
	
	// The FeatureExpr representing this constraint
	private FeatureExpr featureExpr;
	
	/**
	 * Private constructor
	 * @param featureExpr
	 */
	private Constraint(FeatureExpr featureExpr) {
		this.featureExpr = featureExpr;
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * @return True if the constraint is satisfiable
	 */
	public boolean isSatisfiable() {
		return featureExpr.isSatisfiable();
	}
	
	/**
	 * @return True if the constraint is a tautology
	 */
	public boolean isTautology() {
		return featureExpr.isTautology();
	}
	
	/**
	 * @return True if the constraint is a contradiction
	 */
	public boolean isContradiction() {
		return featureExpr.isContradiction();
	}
	
	/**
	 * @param constraint
	 * @return True if the two constraints are equivalent
	 */
	public boolean equivalentTo(Constraint constraint) {
		return (this.featureExpr.equivalentTo(constraint.featureExpr));
	}
	
	/**
	 * @param constraint
	 * @return True if the two constraints are opposite of each other
	 */
	public boolean oppositeOf(Constraint constraint) {
		return (this.featureExpr.equivalentTo(constraint.featureExpr.not()));
	}

	/**
	 * @return A string describing the constraint
	 */
	@Override
	public String toString() {
		return featureExpr.toString();
	}
	
	/*
	 * Static methods
	 */
	
	public static Constraint createConstraint(String constraint) {
		return new Constraint(FeatureExprFactory.createDefinedExternal(constraint));
	}
	
	public static Constraint createNotConstraint(Constraint constraint) {
		return new Constraint(constraint.featureExpr.not());
	}
	
	public static Constraint createAndConstraint(Constraint constraint1, Constraint constraint2) {
		return new Constraint(constraint1.featureExpr.and(constraint2.featureExpr));
	}
	
	public static Constraint createOrConstraint(Constraint constraint1, Constraint constraint2) {
		return new Constraint(constraint1.featureExpr.or(constraint2.featureExpr));
	}
	
}
