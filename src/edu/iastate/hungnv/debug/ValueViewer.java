package edu.iastate.hungnv.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.caucho.quercus.env.ArrayValueImpl;
import com.caucho.quercus.env.ObjectExtValue;
import com.caucho.quercus.env.ObjectExtValue.EntrySet;
import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.constraint.Constraint;
import edu.iastate.hungnv.value.Case;
import edu.iastate.hungnv.value.Choice;
import edu.iastate.hungnv.value.Concat;
import edu.iastate.hungnv.value.MultiValue;
import edu.iastate.hungnv.value.Switch;
import edu.iastate.hungnv.value.Undefined;

/**
 * 
 * @author HUNG
 *
 */
public class ValueViewer {
	
	public static final String xmlFile 			= "C:\\Users\\HUNG\\Desktop\\values.xml";
	public static final String xmlFile00 		= "C:\\Users\\HUNG\\Desktop\\values00-derived.xml";
	public static final String xmlFile01 		= "C:\\Users\\HUNG\\Desktop\\values01-derived.xml";
	public static final String xmlFile10 		= "C:\\Users\\HUNG\\Desktop\\values10-derived.xml";
	public static final String xmlFile11 		= "C:\\Users\\HUNG\\Desktop\\values11-derived.xml";
	
	public static final String XML_ROOT 		= "ROOT";
	public static final String XML_NUM_ATTRS 	= "NumAttrs";
	public static final String XML_ATTR			= "Attr";
	
	public static final String XML_NAME_VALUE	= "NAME-VALUE";
	public static final String XML_VALUE 		= "VALUE";
	public static final String XML_CONCAT 		= "CONCAT";
	public static final String XML_CHOICE 		= "CHOICE";
	public static final String XML_SWITCH		= "SWITCH";
	public static final String XML_CASE 		= "CASE";
	public static final String XML_UNDEFINED	= "UNDEFINED";
	public static final String XML_ARRAY		= "ARRAY";
	public static final String XML_OBJECT		= "OBJECT";
	
	public static final String XML_DESC			= "Desc";
	
	public static final String XML_INFO1		= "Info1";
	public static final String XML_INFO2		= "Info2";
	
	/**
	 * List of NameValuePairs
	 */
	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	
	/**
	 * Adds a name-value pair
	 * @param name
	 * @param value
	 */
	public void add(String name, Value value) {
		nameValuePairs.add(new NameValuePair(name, value));
	}
	
	/**
	 * Writes all values to an XML file
	 * @param xmlFile
	 */
	public void writeToXmlFile(String xmlFile) {
		writeToXmlFile(xmlFile, null);
	}
	
	/**
	 * Writes all values satisfying a given constraint to an XML file
	 * @param xmlFile
	 * @param constraint
	 */
	public void writeToXmlFile(String xmlFile, Constraint constraint) {
		Document xmlDocument = XmlDocument.newDocument();
		
		Element rootElement = xmlDocument.createElement(XML_ROOT);
		rootElement.setAttribute(XML_NUM_ATTRS, "2");
		rootElement.setAttribute(XML_ATTR + "1", XML_INFO1);
		rootElement.setAttribute(XML_ATTR + "2", XML_INFO2);
		xmlDocument.appendChild(rootElement);
		
		Collections.sort(nameValuePairs, SortNameValuePairByName.inst);
		
		for (NameValuePair pair : nameValuePairs) {
			Element element = createXmlElementForNameValuePair(pair.getName(), pair.getValue(), xmlDocument, constraint);
			if (element != null)
				rootElement.appendChild(element);
		}
		
		XmlDocument.writeXmlDocumentToFile(xmlDocument, xmlFile);
	}

	/**
	 * Creates an XML element for a name-value pair
	 * @param name
	 * @param value
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForNameValuePair(String name, Value value, Document xmlDocument, Constraint constraint) {
		if (constraint != null && value instanceof MultiValue) {
			Switch flattenedValue = ((MultiValue) value).flatten(constraint);
		
			List<Case> cases = flattenedValue.getCases();
			
			if (cases.size() == 0)
				return null;
			else if (cases.size() == 1)
				value = cases.get(0).getValue();
			else
				value = flattenedValue;
		}
		
		Element element = xmlDocument.createElement(XML_NAME_VALUE);
		element.setAttribute(XML_DESC, name);
		element.setAttribute(XML_INFO1, value instanceof ObjectExtValue ? "Object" : value.toString());
		
		Element childElement = createXmlElementForValue(value, xmlDocument, constraint);
		if (childElement != null)
			element.appendChild(childElement);
		
		return (element.hasChildNodes() ? element : null);
	}
	
	/**
	 * Creates an XML element for a value
	 * @param value
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForValue(Value value, Document xmlDocument, Constraint constraint) {
		if (value instanceof Concat) {
			return createXmlElementForConcat((Concat) value, xmlDocument, constraint);
		}
		else if (value instanceof Choice) {
			return createXmlElementForChoice((Choice) value, xmlDocument, constraint);
		}
		else if (value instanceof Switch) {
			return createXmlElementForSwitch((Switch) value, xmlDocument, constraint);
		}
		else if (value instanceof Case) {
			return createXmlElementForCase((Case) value, xmlDocument, constraint);
		}
		else if (value instanceof Undefined) {
			return createXmlElementForUndefined((Undefined) value, xmlDocument, constraint);
		}
		else if (value instanceof ArrayValueImpl) {
			return createXmlElementForArray((ArrayValueImpl) value, xmlDocument, constraint);
		}
		else if (value instanceof ObjectExtValue) {
			return createXmlElementForObject((ObjectExtValue) value, xmlDocument, constraint);
		}
		else {
			//System.out.println("Please handle " + value.getClass().getSimpleName());
			Element element = xmlDocument.createElement(XML_VALUE);
			element.setAttribute(XML_DESC, value.toString());

			return element;
		}
	}
	
	/**
	 * Creates an XML element for a CONCAT
	 * @param concat
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForConcat(Concat concat, Document xmlDocument, Constraint constraint) {
		Element element = xmlDocument.createElement(XML_CONCAT);
		//element.setAttribute(XML_INFO1, concat.getValue1().toString());
		//element.setAttribute(XML_INFO2, concat.getValue2().toString());

		Element child1 = createXmlElementForValue(concat.getValue1(), xmlDocument, constraint);
		if (child1 != null)
			element.appendChild(child1);
		
		Element child2 = createXmlElementForValue(concat.getValue2(), xmlDocument, constraint);
		if (child2 != null)
			element.appendChild(child2);
		
		return (element.hasChildNodes() ? element : null);
	}
	
	/**
	 * Creates an XML element for a CHOICE
	 * @param choice
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForChoice(Choice choice, Document xmlDocument, Constraint constraint) {
		Element element = xmlDocument.createElement(XML_CHOICE);
		element.setAttribute(XML_INFO1, choice.getConstraint().toString());

		Element child1 = createXmlElementForValue(choice.getValue1(), xmlDocument, constraint);
		if (child1 != null)
			element.appendChild(child1);
		
		Element child2 = createXmlElementForValue(choice.getValue2(), xmlDocument, constraint);
		if (child2 != null)
			element.appendChild(child2);
		
		return (element.hasChildNodes() ? element : null);
	}
	
	/**
	 * Creates an XML element for a SWITCH
	 * @param switch_
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForSwitch(Switch switch_, Document xmlDocument, Constraint constraint) {
		Element element = xmlDocument.createElement(XML_SWITCH);

		for (Case case_ : switch_) {
			Element child = createXmlElementForCase(case_, xmlDocument, constraint);
			if (child != null)
				element.appendChild(child);
		}
		
		return (element.hasChildNodes() ? element : null);
	}
	
	/**
	 * Creates an XML element for a CASE
	 * @param case_
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForCase(Case case_, Document xmlDocument, Constraint constraint) {
		Element element = xmlDocument.createElement(XML_CASE);
		element.setAttribute(XML_INFO1, case_.getConstraint().toString());

		Element child = createXmlElementForValue(case_.getValue(), xmlDocument, constraint);
		if (child != null)
			element.appendChild(child);
		
		return (element.hasChildNodes() ? element : null);
	}
	
	/**
	 * Creates an XML element for an UNDEFINED
	 * @param case_
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForUndefined(Undefined undefined, Document xmlDocument, Constraint constraint) {
		Element element = xmlDocument.createElement(XML_UNDEFINED);
		return element;
	}

	/**
	 * Creates an XML element for an ARRAY
	 * @param array
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForArray(ArrayValueImpl array, Document xmlDocument, Constraint constraint) {
		Element element = xmlDocument.createElement(XML_ARRAY);
		
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Iterator<Map.Entry<Value, Value>> iter = array.getIterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			pairs.add(new NameValuePair(pair.getKey().toString(), pair.getValue()));
		}
			
		Collections.sort(pairs, SortNameValuePairByName.inst);
		
		for (NameValuePair pair : pairs) {
			Element childElement = createXmlElementForNameValuePair(pair.getName(), pair.getValue(), xmlDocument, constraint);
			if (childElement != null)
				element.appendChild(childElement);
		}

		return (element.hasChildNodes() ? element : null);
	}
	
	/**
	 * Creates an XML element for an OBJECT
	 * @param object
	 * @param xmlDocument
	 * @param constraint
	 * @return
	 */
	private Element createXmlElementForObject(ObjectExtValue object, Document xmlDocument, Constraint constraint) {
		Element element = xmlDocument.createElement(XML_OBJECT);
		
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Iterator<Map.Entry<Value,Value>> iter = ((EntrySet) object.entrySet()).iterator(); iter.hasNext(); ) {
			Map.Entry<Value, Value> pair = iter.next();
			pairs.add(new NameValuePair(pair.getKey().toString(), pair.getValue()));
		}
			
		Collections.sort(pairs, SortNameValuePairByName.inst);
		
		for (NameValuePair pair : pairs) {
			Element childElement = createXmlElementForNameValuePair(pair.getName(), pair.getValue(), xmlDocument, constraint);
			if (childElement != null)
				element.appendChild(childElement);
		}

		return (element.hasChildNodes() ? element : null);
	}
	
	/**
	 * Class NameValuePair
	 */
	private class NameValuePair {
		
		private String name;
		private Value value;
		
		public NameValuePair(String name, Value value) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		
		public Value getValue() {
			return value;
		}
		
	}
	
	/**
	 * Helper class to support sorting of NameValuePairs
	 */
	private static class SortNameValuePairByName implements Comparator<NameValuePair> {

		public static SortNameValuePairByName inst = new SortNameValuePairByName();
		
		@Override
		public int compare(NameValuePair pair1, NameValuePair pair2) {
			return pair1.getName().compareTo(pair2.getName());
		}

	}

}
