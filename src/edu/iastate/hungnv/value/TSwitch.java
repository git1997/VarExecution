package edu.iastate.hungnv.value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author HUNG
 *
 */
public class TSwitch<T> implements Iterable<TCase<T>> {
	
	private List<TCase<T>> cases = new ArrayList<TCase<T>>();
	
	/*
	 * Getters and setters
	 */
	
	public void addCase(TCase<T> case_) {
		cases.add(case_);
	}
	
	public void addCases(TSwitch<T> switch_) {
		cases.addAll(switch_.cases);
	}
	
	public List<TCase<T>> getCases() {
		return new ArrayList<TCase<T>>(cases);
	}
	
	/*
	 * Methods
	 */
	
	@Override
	public Iterator<TCase<T>> iterator() {
		return cases.iterator();
	}
	
}
