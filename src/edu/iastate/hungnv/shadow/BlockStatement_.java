package edu.iastate.hungnv.shadow;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.statement.BlockStatement;
import com.caucho.quercus.statement.Statement;

import edu.iastate.hungnv.util.Logging;
import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class BlockStatement_ {
	
	private static int currentDebugLocation = 0;
	
    private static String[][] debugLocations = 	new String[][]{
											    	{"content.php", "41"},
											    	{"post-template.php", "166"},
											    	{"plugin.php", "166"},
											    	//{"formatting.php", "1738"},
											    };
	
	/**
	 * @see com.caucho.quercus.statement.BlockStatement.execute(Env)
	 */
	public static Value execute(Env env, BlockStatement _this, Statement[] _statements) {

		for (int i = 0; i < _statements.length; i++) {
			Statement statement = _statements[i];
	
	        Logging.LOGGER.info("Executing " + statement.getLocation().prettyPrint());
	        
	        if (currentDebugLocation < debugLocations.length
	        	&& statement.getLocation().getFileName().endsWith(debugLocations[currentDebugLocation][0])
	        	&& statement.getLocation().getLineNumber() == Integer.valueOf(debugLocations[currentDebugLocation][1]))
	        {
	        	System.out.println("Break point #" + currentDebugLocation);
	        	currentDebugLocation++;
	        }
	        
	        Value value = statement.execute(env);
	
	        if (value != null) {
	        	// TODO Handle return, continue, break here
	        	
	        	if (MultiValue.whenNull(value).isContradiction())
	        		return value;
	        }
		}

		return null;
	}

}
