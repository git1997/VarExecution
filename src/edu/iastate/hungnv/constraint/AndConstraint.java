package edu.iastate.hungnv.constraint;

/**
 * 
 * @author HUNG
 *
 */
public class AndConstraint extends Constraint {
	
	/**
	 * Construtor
	 * @param constraint1
	 * @param constraint2
	 */
	public AndConstraint(Constraint constraint1, Constraint constraint2) {
		this.featureExpr = constraint1.featureExpr.and(constraint2.featureExpr);
	}
	
}
