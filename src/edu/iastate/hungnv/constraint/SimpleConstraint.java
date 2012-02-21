package edu.iastate.hungnv.constraint;

import de.fosd.typechef.featureexpr.FeatureExprFactory;

/**
 * 
 * @author HUNG
 *
 */
public class SimpleConstraint extends Constraint {
	
	/**
	 * Constructor
	 * @param bool
	 */
	public SimpleConstraint(boolean bool) {
		this.featureExpr = (bool ? FeatureExprFactory.True() : FeatureExprFactory.False());
	}

	/**
	 * Constructor
	 * @param constraint
	 */
	public SimpleConstraint(String constraint) {
		this.featureExpr = FeatureExprFactory.createDefinedExternal(constraint);
	}
	
}
