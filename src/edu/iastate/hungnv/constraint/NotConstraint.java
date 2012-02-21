package edu.iastate.hungnv.constraint;

/**
 * 
 * @author HUNG
 *
 */
public class NotConstraint extends Constraint {

	/**
	 * Construtor
	 * @param constraint
	 */
	public NotConstraint(Constraint constraint) {
		this.featureExpr = constraint.featureExpr.not();
	}
	
}
