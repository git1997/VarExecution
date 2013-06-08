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
	
	/**
	 * @see com.caucho.quercus.statement.BlockStatement.execute(Env)
	 */
	public static Value execute(Env env, BlockStatement _this, Statement[] _statements) {

		for (int i = 0; i < _statements.length; i++) {
			Statement statement = _statements[i];
	
	        Logging.LOGGER.info("Executing " + statement.getLocation().prettyPrint());
	      	  
	        String debugFile = "wp-scripts.php";
	        int debugLine = 182;
	        if (statement.getLocation().getFileName().endsWith(debugFile) && statement.getLocation().getLineNumber() == debugLine) 
	        	System.out.println();
	        
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
