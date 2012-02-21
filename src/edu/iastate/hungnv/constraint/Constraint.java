package edu.iastate.hungnv.constraint;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.bdd.BDDFeatureExprFactory$;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Constraint {
	
	public static final Constraint TRUE = new SimpleConstraint(true);
	
	// The FeatureExpr representing this constraint
	protected FeatureExpr featureExpr;
	
	// Use the JavaBDD library instead of Sat4j 
	{
		FeatureExprFactory.setDefault(BDDFeatureExprFactory$.MODULE$);
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * @return A string describing the constraint
	 */
	@Override
	public String toString() {
		return featureExpr.toString();
	}
	
	/**
	 * @param constraint
	 * @return True if the two constraints are equivalent
	 */
	public boolean equivalentTo(Constraint constraint) {
		return (this.featureExpr.equivalentTo(constraint.featureExpr));
	}
	
	/*
	 * Static methods
	 */
	
	public static Constraint createConstraint(String constraint) {
		return new SimpleConstraint(constraint);
	}
	
	public static Constraint createNotConstraint(Constraint constraint) {
		return new NotConstraint(constraint);
	}
	
	public static Constraint createAndConstraint(Constraint constraint1, Constraint constraint2) {
		return new AndConstraint(constraint1, constraint2);
	}
	
}
