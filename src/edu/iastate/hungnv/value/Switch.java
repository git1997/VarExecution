package edu.iastate.hungnv.value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.caucho.quercus.env.Value;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class Switch extends MultiValue implements Iterable<Case> {
	
	private List<Case> cases = new ArrayList<Case>();
	
	/*
	 * Getters and setters
	 */
	
	public void addCase(Case case_) {
		cases.add(case_);
	}
	
	public void addCases(Switch switch_) {
		cases.addAll(switch_.cases);
	}
	
	public List<Case> getCases() {
		return new ArrayList<Case>(cases);
	}
	
	/*
	 * Methods
	 */
	
	@Override
	public Switch flatten() {
		return this;
	}
	
	@Override
	public Value simplify() {
		if (cases.size() == 1 && cases.get(0).getConstraint().isTautology())
			return cases.get(0).getValue();
		else
			return this;
	}

	@Override
	public Iterator<Case> iterator() {
		return cases.iterator();
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("SWITCH(");
		
		for (Case case_ : cases) {
			if (cases.indexOf(case_) > 0)
				string.append(", ");
			
			string.append(case_.toString());
		}
		string.append(")");
		
		return string.toString();
	}

}
