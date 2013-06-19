package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.ConstStringValue;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.debug.OutputViewer;
import edu.iastate.hungnv.debug.TraceViewer;
import edu.iastate.hungnv.debug.ValueViewer;
import edu.iastate.hungnv.regressiontest.RegressionTest;
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
	
	// Turn on or off regression testing mode
	public static final boolean REGRESSION_TESTING = false;
	
	// String constants
	public static final String __INSTRUMENT__ = "__INSTRUMENT__";
	public static final String __REGRESSION_TESTING__ = "__REGRESSION_TESTING__";
	
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
		TraceViewer.inst.reset();
		OutputViewer.inst.setOutputValue(new ConstStringValue(""));
	}
	
	public void closing(Env env) {
		Logging.LOGGER.info("Env closing...");
		
		OutputViewer.inst.setFinalOutputValue(OutputViewer.inst.getOutputValue());
	}
	
	public void closed(Env env) {
		/*
		 * TraceViewer
		 */
		TraceViewer.inst.writeToXmlFile(TraceViewer.xmlFile);
		
		/*
		 * OutputViewer
		 */
		if (REGRESSION_TESTING) {
			RegressionTest.inst.generateOutputs(OutputViewer.inst);
		}
		else if (INSTRUMENT) {
			OutputViewer.inst.writeToXmlFile(OutputViewer.xmlFileAll);
			OutputViewer.inst.writeToTxtFile(OutputViewer.txtFileDerived, Constraint.TRUE); // TODO Revise
		}
		else
			OutputViewer.inst.writeToTxtFile(OutputViewer.txtFile);
		
		/*
		 * ValueViewer
		 */
		ValueViewer viewer = new ValueViewer();
		for (StringValue name : env.getEnv().keySet()) {
			Value value = env.getEnv().get(name).get();
			if (!name.toString().equals("__OUTPUT__")) // ADHOC Adhoc code so that the special variable output is not printed
				viewer.add(name, value);
		}
		
		if (REGRESSION_TESTING) {
			RegressionTest.inst.generateHeaps(viewer);
		}
		if (INSTRUMENT) {
			viewer.writeToXmlFile(ValueViewer.xmlFileAll);
			viewer.writeToXmlFile(ValueViewer.xmlFileDerived, Constraint.TRUE); // TODO Revise
		}
		else
			viewer.writeToXmlFile(ValueViewer.xmlFile);
		
		/*
		 * Tests
		 */
//		Tester.inst.test(OutputViewer.inst.getFinalOutputValue());

		Logging.LOGGER.info("Env closed.");
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
	  
	/*
	 * Experimental methods
	 */
	
}