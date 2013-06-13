package edu.iastate.hungnv.shadow;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.debug.OutputViewer;
import edu.iastate.hungnv.debug.ValueViewer;

/**
 * 
 * @author HUNG
 *
 */
public class Config {
	
	public static final Constraint 
		plugins[] = {
			Constraint.createConstraint("CAL"),
			Constraint.createConstraint("WEA"),
			Constraint.createConstraint("AKI"),
			Constraint.createConstraint("CAR"),
			Constraint.createConstraint("COD"),
			Constraint.createConstraint("INV"),
			Constraint.createConstraint("LAZ"),
			Constraint.createConstraint("FAC"),
			Constraint.createConstraint("BLO"),
			Constraint.createConstraint("SYN"),
		};
	
	public static final String 
		testConfigs[] = {
			"0000000000",
			"1000000000",
			"0100000000",
			"0010000000",
			"0001000000",
			"0000100000",
			"0000010000",
			"0000001000",
			"0000000100",
			"0000000010",
			"0000000001",
			"1100000000",
			"0011000000",
			"0000110000",
			"0000001100",
			"0000000011",
			"1110000000",
			"0001110000",
			"0000001111",
			"1111100000",
			"0000011111",
			"1111111111",
		};
	
	/**
	 * Static instance of Config
	 */
	public static Config inst = new Config();
	
	/**
	 * Generate concrete outputs from a MultiOutput
	 */
	public void generateConcreteOutputs(OutputViewer outputViewer) {
		for (String testConfig : testConfigs) {
			Constraint constraint = getConstraintForTestConfig(plugins, testConfig);
			OutputViewer.inst.writeToTxtFile(OutputViewer.txtFileConfig.replace("{CONFIG}", testConfig), constraint);
		}
	}
	
	/**
	 * Generate concreate values from a MultiValue
	 */
	public void generateConcreteValues(ValueViewer valueViewer) {
		for (String testConfig : testConfigs) {
			Constraint constraint = getConstraintForTestConfig(plugins, testConfig);
			valueViewer.writeToXmlFile(ValueViewer.xmlFileConfig.replace("{CONFIG}", testConfig), constraint);
		}
	}
	
	/**
	 * Returns the constraint describing a test config.
	 */
	private Constraint getConstraintForTestConfig(Constraint[] plugins, String testConfig) {
		Constraint constraint = Constraint.TRUE;
		
		for (int i = 0; i < testConfig.length(); i++) {
			Constraint pluginConstraint;
			
			if (testConfig.charAt(i) == '1')
				pluginConstraint = plugins[i];
			else
				pluginConstraint = Constraint.createNotConstraint(plugins[i]);
			
			constraint = Constraint.createAndConstraint(constraint, pluginConstraint);
		}
		return constraint;
	}

}
