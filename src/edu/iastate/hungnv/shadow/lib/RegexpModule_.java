package edu.iastate.hungnv.shadow.lib;

import com.caucho.quercus.env.ArrayValue;
import com.caucho.quercus.env.ArrayValueImpl;
import com.caucho.quercus.env.Callable;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.LongValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;
import com.caucho.quercus.lib.regexp.IllegalRegexpException;
import com.caucho.quercus.lib.regexp.Regexp;
import com.caucho.quercus.lib.regexp.RegexpState;

import edu.iastate.hungnv.value.MultiValue;

/**
 * 
 * @author HUNG
 *
 */
public class RegexpModule_ {
	
	/**
	 * @see com.caucho.quercus.lib.regexp.RegexpModule.pregReplaceCallback(Env, Regexp, Callable, StringValue, long, Value)
	 */
	public static Value pregReplaceCallback_(Env env,
											Regexp regexp,
											Callable fun,
											StringValue subject,
											long limit,
											Value countV,
											long LONG_MAX)
		throws IllegalRegexpException 
	{
	    if (limit < 0)
	        limit = LONG_MAX;

	      if (! subject.isset()) {
	        return env.getEmptyString();
	      }
	      else {
	        return pregReplaceCallbackImpl_(env,
	                                       regexp,
	                                       fun,
	                                       subject,
	                                       limit,
	                                       countV,
	                                       LONG_MAX);
	      }
	}
	
	/**
	 * @see com.caucho.quercus.lib.regexp.RegexpModule.pregReplaceCallbackImpl(Env, Regexp, Callable, StringValue, long, Value)
	 */
	private static Value pregReplaceCallbackImpl_(Env env,
													Regexp regexp,
													Callable fun,
													StringValue subject,
													long limit,
													Value countV,
													long LONG_MAX)
		throws IllegalRegexpException
	{
	    @SuppressWarnings("static-access")
		StringValue empty = subject.EMPTY;

	    long numberOfMatches = 0;

	    if (limit < 0)
	      limit = LONG_MAX;

	    RegexpState regexpState = RegexpState.create(env, regexp);

	    regexpState.setSubject(env, subject);

	    Value result = subject.createStringBuilder();
	    int tail = 0;

	    while (regexpState.find() && numberOfMatches < limit) {
	      // Increment countV (note: if countV != null, then it should be a Var)
	      if (countV != null && countV instanceof Var) {
	        long count = countV.toValue().toLong();
	        countV.set(LongValue.create(count + 1));
	      }

	      int start = regexpState.start();
	      if (tail < start) {
	    	  if (result instanceof MultiValue)
	    		  result = MultiValue.createConcatValue(result, regexpState.substring(env, tail, start));
	    	  else
	    		  result = ((StringValue) result).append(regexpState.substring(env, tail, start));
	      }

	      ArrayValue regs = new ArrayValueImpl();

	      int lastGroup = 0;
	      for (int i = 0; i < regexpState.groupCount(); i++) {
	        StringValue group = regexpState.group(env, i);

	        if (group != null && ! group.isEmpty()) {
	          /* PHP's preg_replace_callback does not return empty groups */
	          // php/154b, c
	          
	          for (int j = lastGroup + 1; j < i; j++) {
	            regs.put(empty);
	          }

	          regs.put(group);
	        }
	      }

	      Value replacement = fun.call(env, regs);

	      if (result instanceof MultiValue || replacement instanceof MultiValue)
	    	  result = MultiValue.createConcatValue(result, replacement);
	      else
	    	  result = ((StringValue) result).append(replacement);

	      tail = regexpState.end();

	      numberOfMatches++;
	    }

	    if (tail < regexpState.getSubjectLength()) {
	    	if (result instanceof MultiValue)
	    		result = MultiValue.createConcatValue(result, regexpState.substring(env, tail));
	    	else
	    		result = ((StringValue) result).append(regexpState.substring(env, tail));
	    }

	    env.freeRegexpState(regexpState);

	    return result;
	}

}
