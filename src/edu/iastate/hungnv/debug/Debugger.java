package edu.iastate.hungnv.debug;

import com.caucho.quercus.Location;

/**
 * 
 * @author HUNG
 *
 */
public class Debugger {
	
    private static final String[][] debugLocations 
    	= new String[][]{
    		{"functions.wp-scripts.php", "126"} 
    	};
	
	private int currentDebugLocation = 0;
    
	/**
	 * Static instance of Debugger
	 */
	public static Debugger inst = new Debugger();
    
	/**
	 * Checks a breakpoint
	 */
    public void checkBreakpoint(Location location) {
	    if (currentDebugLocation < debugLocations.length
	    	&& location.getFileName().endsWith(debugLocations[currentDebugLocation][0])
	    	&& location.getLineNumber() == Integer.valueOf(debugLocations[currentDebugLocation][1]))
	    {
	    	System.out.println("Break point #" + currentDebugLocation + ": " + location);
	    	currentDebugLocation++;
	    }
    }

}
