package edu.iastate.hungnv.scope;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.caucho.quercus.QuercusException;
import com.caucho.quercus.QuercusRuntimeException;
import com.caucho.quercus.env.ArrayValue;
import com.caucho.quercus.env.ArrayValueImpl;
import com.caucho.quercus.env.BinaryBuilderValue;
import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.Callable;
import com.caucho.quercus.env.CallbackError;
import com.caucho.quercus.env.CopyRoot;
import com.caucho.quercus.env.DoubleValue;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.FieldVisibility;
import com.caucho.quercus.env.LargeStringBuilderValue;
import com.caucho.quercus.env.LongValue;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.NumberValue;
import com.caucho.quercus.env.ObjectValue;
import com.caucho.quercus.env.QuercusClass;
import com.caucho.quercus.env.QuercusLanguageException;
import com.caucho.quercus.env.ResourceValue;
import com.caucho.quercus.env.SerializeMap;
import com.caucho.quercus.env.StringBuilderValue;
import com.caucho.quercus.env.StringInputStream;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.UnicodeBuilderValue;
import com.caucho.quercus.env.UnsetValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.ValueType;
import com.caucho.quercus.env.Var;
import com.caucho.quercus.function.AbstractFunction;
import com.caucho.quercus.marshal.Marshal;
import com.caucho.vfs.WriteStream;

import edu.iastate.hungnv.util.Logging;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("serial")
public class ScopedValue extends Value {
	
	// Pointer to the outer ScopedValue (could be several scopes apart)
	private ScopedValue outerScopedValue; // Null if scope = GLOBAL, Not null if scope != GLOBAL
	
	// The current scope
	private Scope scope;	// Not null
	
	// The current (regular) value
	private Value value;	// A regular value, not null
	
	/**
	 * Constructor
	 * @param scope				Not null
	 * @param value				A regular value, not null
	 * @param outerScopedValue	Null if scope = GLOBAL, Not null if scope != GLOBAL
	 */
	public ScopedValue(Scope scope, Value value, ScopedValue outerScopedValue) {
		setScope(scope);
		setValue(value);
		setOuterScopedValue(outerScopedValue);
	}
	
	/*
	 * Getters and setters
	 */
	
	/**
	 * Returns the outer ScopedValue
	 * Note: the current ScopedValue and the outerScopedValue could be several scopes apart.
	 * @return The outer ScopedValue, null if scope = GLOBAL, Not null if scope != GLOBAL
	 */
	public ScopedValue getOuterScopedValue() {
		return outerScopedValue;
	}
	
	/**
	 * Sets the outer ScopedValue
	 * @param outerScopedValue	Null if scope = GLOBAL, Not null if scope != GLOBAL
	 */
	public void setOuterScopedValue(ScopedValue outerScopedValue) {
		this.outerScopedValue = outerScopedValue;
	}
	
	/**
	 * @return The current scope, not null
	 */
	public Scope getScope() {
		return scope;
	}
	
	/**
	 * Sets the current scope
	 * @param scope	Not null
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	/**
	 * Returns the (regular) value in the current scope
	 * @return 	A regular value, not null
	 */
	public Value getValue() {
		return value;
	}
	
	/**
	 * Sets the (regular) value in the current scope
	 * @param value		A regular value, not null
	 */
	public void setValue(Value value) {
		this.value = value;
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * @return A string describing the ScopedValue (with scoping information)
	 */
	public String toStringWithScoping() {
		return scope.toString() 
				+ " => " + (value == com.caucho.quercus.env.NullValue.NULL ? "NullValue" : value.toString())  
				+ (outerScopedValue != null ? ("; " + outerScopedValue.toStringWithScoping()) : "");
	}
	
	/*
	 * Shadowed methods of the Value class
	 */
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	  //
	  // Properties
	  //

	  /**
	   * Returns the value's class name.
	   */
	  @Override
	  public String getClassName()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getType();
	  }

	  /**
	   * Returns the backing QuercusClass.
	   */
	  @Override
	  public QuercusClass getQuercusClass()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return null;
	  }

	  /**
	   * Returns the called class
	   */
	  @Override
	  public Value getCalledClass(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    QuercusClass qClass = getQuercusClass();

	    if (qClass != null)
	      return env.createString(qClass.getName());
	    else {
	      env.warning(L.l("get_called_class() must be called in a class context"));

	      return BooleanValue.FALSE;
	    }
	  }

	  //
	  // Predicates and Relations
	  //

	  /**
	   * Returns true for an implementation of a class
	   */
	  @Override
	  public boolean isA(String name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for an implementation of a class
	   */
	  //@Override
	  public final boolean isA_(Value value) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    if (value.isObject())
	      return isA(value.getClassName());
	    else
	      return isA(value.toString());
	  }

	  /**
	   * Checks if 'this' is a valid protected call for 'className'
	   */
	  @Override
	  public void checkProtected(Env env, String className)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	  }

	  /**
	   * Checks if 'this' is a valid private call for 'className'
	   */
	  @Override
	  public void checkPrivate(Env env, String className)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	  }

	  /**
	   * Returns the ValueType.
	   */
	  @Override
	  public ValueType getValueType()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return ValueType.VALUE;
	  }

	  /**
	   * Returns true for an array.
	   */
	  @Override
	  public boolean isArray()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a double-value.
	   */
	  @Override
	  public boolean isDoubleConvertible()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a long-value.
	   */
	  @Override
	  public boolean isLongConvertible()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a long-value.
	   */
	  @Override
	  public boolean isLong()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a long-value.
	   */
	  @Override
	  public boolean isDouble()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a null.
	   */
	  @Override
	  public boolean isNull()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a number.
	   */
	  @Override
	  public boolean isNumberConvertible()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return isLongConvertible() || isDoubleConvertible();
	  }

	  /**
	   * Matches is_numeric
	   */
	  @Override
	  public boolean isNumeric()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for an object.
	   */
	  @Override
	  public boolean isObject()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /*
	   * Returns true for a resource.
	   */
	  @Override
	  public boolean isResource()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a StringValue.
	   */
	  @Override
	  public boolean isString()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a BinaryValue.
	   */
	  @Override
	  public boolean isBinary()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a UnicodeValue.
	   */
	  @Override
	  public boolean isUnicode()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a BooleanValue
	   */
	  @Override
	  public boolean isBoolean()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for a DefaultValue
	   */
	  @Override
	  public boolean isDefault()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  //
	  // marshal costs
	  //

	  /**
	   * Cost to convert to a boolean
	   */
	  @Override
	  public int toBooleanMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_BOOLEAN;
	  }

	  /**
	   * Cost to convert to a byte
	   */
	  @Override
	  public int toByteMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_INCOMPATIBLE;
	  }

	  /**
	   * Cost to convert to a short
	   */
	  @Override
	  public int toShortMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_INCOMPATIBLE;
	  }

	  /**
	   * Cost to convert to an integer
	   */
	  @Override
	  public int toIntegerMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_INCOMPATIBLE;
	  }

	  /**
	   * Cost to convert to a long
	   */
	  @Override
	  public int toLongMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_INCOMPATIBLE;
	  }

	  /**
	   * Cost to convert to a double
	   */
	  @Override
	  public int toDoubleMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_INCOMPATIBLE;
	  }

	  /**
	   * Cost to convert to a float
	   */
	  @Override
	  public int toFloatMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toDoubleMarshalCost() + 10;
	  }

	  /**
	   * Cost to convert to a character
	   */
	  @Override
	  public int toCharMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_CHAR;
	  }

	  /**
	   * Cost to convert to a string
	   */
	  @Override
	  public int toStringMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_STRING;
	  }

	  /**
	   * Cost to convert to a byte[]
	   */
	  @Override
	  public int toByteArrayMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_BYTE_ARRAY;
	  }

	  /**
	   * Cost to convert to a char[]
	   */
	  @Override
	  public int toCharArrayMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_CHAR_ARRAY;
	  }

	  /**
	   * Cost to convert to a Java object
	   */
	  @Override
	  public int toJavaObjectMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_JAVA_OBJECT;
	  }

	  /**
	   * Cost to convert to a binary value
	   */
	  @Override
	  public int toBinaryValueMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_STRING + 1;
	  }

	  /**
	   * Cost to convert to a StringValue
	   */
	  @Override
	  public int toStringValueMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_STRING + 1;
	  }

	  /**
	   * Cost to convert to a UnicodeValue
	   */
	  @Override
	  public int toUnicodeValueMarshalCost()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return Marshal.COST_TO_STRING + 1;
	  }

	  //
	  // predicates
	  //

	  /**
	   * Returns true if the value is set.
	   */
	  @Override
	  public boolean isset()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return true;
	  }

	  /**
	   * Returns true if the value is empty
	   */
	  @Override
	  public boolean isEmpty()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true if there are more elements.
	   */
	  @Override
	  public boolean hasCurrent()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true for equality
	   */
	  @Override
	  public Value eqValue(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return eq(rValue) ? BooleanValue.TRUE : BooleanValue.FALSE;
	  }

	  /**
	   * Returns true for equality
	   */
	  @Override
	  public boolean eq(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (rValue.isArray())
	      return rValue.eq(this);
	    else if (rValue instanceof BooleanValue)
	      return toBoolean() == rValue.toBoolean();
	    else if (isLongConvertible() && rValue.isLongConvertible())
	      return toLong() == rValue.toLong();
	    else if (isNumberConvertible() || rValue.isNumberConvertible())
	      return toDouble() == rValue.toDouble();
	    else
	      return toString().equals(rValue.toString());
	  }

	  /**
	   * Returns true for equality
	   */
	  @Override
	  public boolean eql(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this == rValue.toValue();
	  }

	  /**
	   * Returns a negative/positive integer if this Value is
	   * lessthan/greaterthan rValue.
	   */
	  @Override
	  public int cmp(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    // This is tricky: implemented according to Table 15-5 of
	    // http://us2.php.net/manual/en/language.operators.comparison.php

	    Value lVal = toValue();
	    Value rVal = rValue.toValue();

	    if (lVal instanceof StringValue && rVal instanceof NullValue)
	      return ((StringValue) lVal).cmpString(StringValue.EMPTY);

	    if (lVal instanceof NullValue && rVal instanceof StringValue)
	      return StringValue.EMPTY.cmpString((StringValue) rVal);

	    if (lVal instanceof StringValue && rVal instanceof StringValue)
	      return ((StringValue) lVal).cmpString((StringValue) rVal);

	    if (lVal instanceof NullValue
	        || lVal instanceof BooleanValue
	        || rVal instanceof NullValue
	        || rVal instanceof BooleanValue)
	    {
	      boolean lBool = toBoolean();
	      boolean rBool    = rValue.toBoolean();

	      if (!lBool && rBool) return -1;
	      if (lBool && !rBool) return 1;
	      return 0;
	    }

	    if (lVal.isObject() && rVal.isObject())
	      return ((ObjectValue) lVal).cmpObject((ObjectValue) rVal);

	    if ((lVal instanceof StringValue
	         || lVal instanceof NumberValue
	         || lVal instanceof ResourceValue)
	        && (rVal instanceof StringValue
	            || rVal instanceof NumberValue
	            || rVal instanceof ResourceValue))
	      return NumberValue.compareNum(lVal, rVal);

	    if (lVal instanceof ArrayValue) return 1;
	    if (rVal instanceof ArrayValue) return -1;
	    if (lVal instanceof ObjectValue) return 1;
	    if (rVal instanceof ObjectValue) return -1;

	    // XXX: proper default case?
	    throw new RuntimeException(
	      "values are incomparable: " + lVal + " <=> " + rVal);
	  }

	  /**
	   * Returns true for less than
	   */
	  @Override
	  public boolean lt(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return cmp(rValue) < 0;
	  }

	  /**
	   * Returns true for less than or equal to
	   */
	  @Override
	  public boolean leq(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return cmp(rValue) <= 0;
	  }

	  /**
	   * Returns true for greater than
	   */
	  @Override
	  public boolean gt(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return cmp(rValue) > 0;
	  }

	  /**
	   * Returns true for greater than or equal to
	   */
	  @Override
	  public boolean geq(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return cmp(rValue) >= 0;
	  }

	  //
	  // Conversions
	  //

	  /**
	   * Converts to a boolean.
	   */
	  @Override
	  public boolean toBoolean()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return true;
	  }

	  /**
	   * Converts to a long.
	   */
	  @Override
	  public long toLong()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toBoolean() ? 1 : 0;
	  }

	  /**
	   * Converts to an int
	   */
	  @Override
	  public int toInt()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return (int) toLong();
	  }

	  /**
	   * Converts to a double.
	   */
	  @Override
	  public double toDouble()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return 0;
	  }

	  /**
	   * Converts to a char
	   */
	  @Override
	  public char toChar()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    String s = toString();

	    if (s == null || s.length() < 1)
	      return 0;
	    else
	      return s.charAt(0);
	  }

	  /**
	   * Converts to a string.
	   *
	   * @param env
	   */
	  @Override
	  public StringValue toString(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toStringValue();
	  }

	  /**
	   * Converts to an array.
	   */
	  @Override
	  public Value toArray()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new ArrayValueImpl().append(this);
	  }

	  /**
	   * Converts to an array if null.
	   */
	  @Override
	  public Value toAutoArray()
	  {
		  return value.toAutoArray();
		  
//		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
//
//	    Env.getCurrent().warning(L.l("'{0}' cannot be used as an array.", 
//	                                 toDebugString()));
//
//	    return this;
	  }

	  /**
	   * Casts to an array.
	   */
	  @Override
	  public ArrayValue toArrayValue(Env env)
	  {
		  return value.toArrayValue(env);
		  
//		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
//
//	    env.warning(L.l("'{0}' ({1}) is not assignable to ArrayValue",
//	                  this, getType()));
//
//	    return null;
	  }

	  /**
	   * Converts to an object if null.
	   */
	  @Override
	  public Value toAutoObject(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Converts to an object.
	   */
	  @Override
	  public Value toObject(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    ObjectValue obj = env.createObject();

	    obj.putField(env, env.createString("scalar"), this);

	    return obj;
	  }

	  /**
	   * Converts to a java object.
	   */
	  @Override
	  public Object toJavaObject()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return null;
	  }

	  /**
	   * Converts to a java object.
	   */
	  @Override
	  public Object toJavaObject(Env env, @SuppressWarnings("rawtypes") Class type)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.warning(L.l("Can't convert {0} to Java {1}",
	                    getClass().getName(), type.getName()));

	    return null;
	  }

	  /**
	   * Converts to a java object.
	   */
	  @Override
	  public Object toJavaObjectNotNull(Env env, @SuppressWarnings("rawtypes") Class type)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.warning(L.l("Can't convert {0} to Java {1}",
	                    getClass().getName(), type.getName()));

	    return null;
	  }

	  /**
	   * Converts to a java boolean object.
	   */
	  @Override
	  public Boolean toJavaBoolean()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toBoolean() ? Boolean.TRUE : Boolean.FALSE;
	  }

	  /**
	   * Converts to a java byte object.
	   */
	  @Override
	  public Byte toJavaByte()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Byte((byte) toLong());
	  }

	  /**
	   * Converts to a java short object.
	   */
	  @Override
	  public Short toJavaShort()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Short((short) toLong());
	  }

	  /**
	   * Converts to a java Integer object.
	   */
	  @Override
	  public Integer toJavaInteger()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Integer((int) toLong());
	  }

	  /**
	   * Converts to a java Long object.
	   */
	  @Override
	  public Long toJavaLong()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Long((int) toLong());
	  }

	  /**
	   * Converts to a java Float object.
	   */
	  @Override
	  public Float toJavaFloat()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Float((float) toDouble());
	  }

	  /**
	   * Converts to a java Double object.
	   */
	  @Override
	  public Double toJavaDouble()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Double(toDouble());
	  }

	  /**
	   * Converts to a java Character object.
	   */
	  @Override
	  public Character toJavaCharacter()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Character(toChar());
	  }

	  /**
	   * Converts to a java String object.
	   */
	  @Override
	  public String toJavaString()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toString();
	  }

	  /**
	   * Converts to a java Collection object.
	   */
	  @Override
	  public Collection<?> toJavaCollection(Env env, Class<?> type)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.warning(L.l("Can't convert {0} to Java {1}",
	            getClass().getName(), type.getName()));

	    return null;
	  }

	  /**
	   * Converts to a java List object.
	   */
	  @Override
	  public List<?> toJavaList(Env env, Class<?> type)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.warning(L.l("Can't convert {0} to Java {1}",
	            getClass().getName(), type.getName()));

	    return null;
	  }

	  /**
	   * Converts to a java Map object.
	   */
	  @Override
	  public Map<?,?> toJavaMap(Env env, Class<?> type)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.warning(L.l("Can't convert {0} to Java {1}",
	            getClass().getName(), type.getName()));

	    return null;
	  }

	  /**
	   * Converts to a Java Calendar.
	   */
	  @Override
	  public Calendar toJavaCalendar()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Calendar cal = Calendar.getInstance();

	    cal.setTimeInMillis(toLong());

	    return cal;
	  }

	  /**
	   * Converts to a Java Date.
	   */
	  @Override
	  public Date toJavaDate()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Date(toLong());
	  }

	  /**
	   * Converts to a Java URL.
	   */
	  @Override
	  public URL toJavaURL(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    try {
	      return new URL(toString());
	    }
	    catch (MalformedURLException e) {
	      env.warning(L.l(e.getMessage()));
	      return null;
	    }
	  }

	  /**
	   * Converts to a Java BigDecimal.
	   */
	  @Override
	  public BigDecimal toBigDecimal()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new BigDecimal(toString());
	  }

	  /**
	   * Converts to a Java BigInteger.
	   */
	  @Override
	  public BigInteger toBigInteger()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new BigInteger(toString());
	  }

	  /**
	   * Converts to an exception.
	   */
	  @Override
	  public QuercusException toException(Env env, String file, int line)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    putField(env, env.createString("file"), env.createString(file));
	    putField(env, env.createString("line"), LongValue.create(line));

	    return new QuercusLanguageException(this);
	  }

	  /**
	   * Converts to a raw value.
	   */
	  @Override
	  public Value toValue()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Converts to a key.
	   */
	  @Override
	  public Value toKey()
	  {
		  return value.toKey();
		  
//		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
//
//	    throw new QuercusRuntimeException(L.l("{0} is not a valid key", this));
	  }

	  /**
	   * Convert to a ref.
	   */
	  @Override
	  public Value toRef()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Convert to a function argument value, e.g. for
	   *
	   * function foo($a)
	   *
	   * where $a is never assigned or modified
	   */
	  @Override
	  public Value toLocalValueReadOnly()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Convert to a function argument value, e.g. for
	   *
	   * function foo($a)
	   *
	   * where $a is never assigned, but might be modified, e.g. $a[3] = 9
	   */
	  @Override
	  public Value toLocalValue()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Convert to a function argument value, e.g. for
	   *
	   * function foo($a)
	   *
	   * where $a may be assigned.
	   */
	  @Override
	  public Value toLocalRef()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Convert to a function argument value, e.g. for
	   *
	   * function foo($a)
	   *
	   * where $a is used as a variable in the function
	   */
	  @Override
	  public Var toLocalVar()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toLocalRef().toVar();
	  }

	  /**
	   * Convert to a function argument reference value, e.g. for
	   *
	   * function foo(&$a)
	   *
	   * where $a is used as a variable in the function
	   */
	  @Override
	  public Var toLocalVarDeclAsRef()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Var(this);
	  }
	  
	  /**
	   * Converts to a local $this, which can depend on the calling class
	   */
	  @Override
	  public Value toLocalThis(QuercusClass qClass)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Convert to a function argument reference value, e.g. for
	   *
	   * function foo(&$a)
	   *
	   * where $a is never assigned in the function
	   */
	  @Override
	  public Value toRefValue()
	  {
		  return value.toRefValue();
		  
//		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
//
//	    return this;
	  }

	  /**
	   * Converts to a Var.
	   */
	  @Override
	  public Var toVar()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Var(this);
	  }

	  /**
	   * Convert to a function argument reference value, e.g. for
	   *
	   * function foo(&$a)
	   *
	   * where $a is used as a variable in the function
	   */
	  @Override
	  public Value toArgRef()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Env.getCurrent()
	      .warning(L.l(
	        "'{0}' is an invalid reference, because only "
	        + "variables may be passed by reference.",
	        this));

	    return NullValue.NULL;
	  }

	  /**
	   * Converts to a StringValue.
	   */
	  @Override
	  public StringValue toStringValue()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toStringValue(Env.getInstance());
	  }

	  /*
	   * Converts to a StringValue.
	   */
	  @Override
	  public StringValue toStringValue(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toStringBuilder(env);
	  }

	  /**
	   * Converts to a Unicode string.  For unicode.semantics=false, this will
	   * still return a StringValue. For unicode.semantics=true, this will
	   * return a UnicodeStringValue.
	   */
	  @Override
	  public StringValue toUnicode(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toUnicodeValue(env);
	  }

	  /**
	   * Converts to a UnicodeValue for marshaling, so it will create a
	   * UnicodeValue event when unicode.semantics=false.
	   */
	  @Override
	  public StringValue toUnicodeValue()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toUnicodeValue(Env.getInstance());
	  }

	  /**
	   * Converts to a UnicodeValue for marshaling, so it will create a
	   * UnicodeValue event when unicode.semantics=false.
	   */
	  @Override
	  public StringValue toUnicodeValue(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    // php/0ci0
	    return new UnicodeBuilderValue(env.createString(toString()));
	  }

	  /**
	   * Converts to a BinaryValue.
	   */
	  @Override
	  public StringValue toBinaryValue()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toBinaryValue(Env.getInstance());
	  }

	  /**
	   * Converts to a BinaryValue.
	   */
	  @Override
	  public StringValue toBinaryValue(String charset)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toBinaryValue();
	  }

	  /**
	   * Converts to a BinaryValue.
	   */
	  @Override
	  public StringValue toBinaryValue(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    StringValue bb = env.createBinaryBuilder();

	    bb.append(this);

	    return bb;

	      /*
	    try {
	      int length = 0;
	      while (true) {
	        bb.ensureCapacity(bb.getLength() + 256);

	        int sublen = is.read(bb.getBuffer(),
	                             bb.getOffset(),
	                             bb.getLength() - bb.getOffset());

	        if (sublen <= 0)
	          return bb;
	        else {
	          length += sublen;
	          bb.setOffset(length);
	        }
	      }
	    } catch (IOException e) {
	      throw new QuercusException(e);
	    }
	      */
	  }

	  /**
	   * Returns a byteArrayInputStream for the value.
	   * See TempBufferStringValue for how this can be overriden
	   *
	   * @return InputStream
	   */
	  @Override
	  public InputStream toInputStream()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new StringInputStream(toString());
	  }

	  /**
	   * Converts to a string builder
	   */
	  @Override
	  public StringValue toStringBuilder()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toStringBuilder(Env.getInstance());
	  }

	  /**
	   * Converts to a string builder
	   */
	  @Override
	  public StringValue toStringBuilder(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return env.createUnicodeBuilder().appendUnicode(this);
	  }

	  /**
	   * Converts to a string builder
	   */
	  @Override
	  public StringValue toStringBuilder(Env env, Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toStringBuilder(env).appendUnicode(value);
	  }

	  /**
	   * Converts to a string builder
	   */
	  @Override
	  public StringValue toStringBuilder(Env env, StringValue value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toStringBuilder(env).appendUnicode(value);
	  }

	  /**
	   * Converts to a string builder
	   */
	  @Override
	  public StringValue copyStringBuilder()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toStringBuilder();
	  }

	  /**
	   * Converts to a long vaule
	   */
	  @Override
	  public LongValue toLongValue()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return LongValue.create(toLong());
	  }

	  /**
	   * Converts to a double vaule
	   */
	  @Override
	  public DoubleValue toDoubleValue()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new DoubleValue(toDouble());
	  }
	  
	  /**
	   * Returns true for a callable object.
	   */
	  @Override
	  public boolean isCallable(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }
	  
	  /**
	   * Returns the callable's name for is_callable()
	   */
	  @Override
	  public String getCallableName()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return null;
	  }
	  
	  /**
	   * Converts to a callable
	   */
	  @Override
	  public Callable toCallable(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.warning(L.l("Callable: '{0}' is not a valid callable argument",
	                    toString()));

	    return new CallbackError(toString());
	  }

	  //
	  // Operations
	  //

	  /**
	   * Append to a string builder.
	   */
	  @Override
	  public StringValue appendTo(UnicodeBuilderValue sb)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return sb.append(toString());
	  }

	  /**
	   * Append to a binary builder.
	   */
	  @Override
	  public StringValue appendTo(StringBuilderValue sb)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return sb.append(toString());
	  }

	  /**
	   * Append to a binary builder.
	   */
	  @Override
	  public StringValue appendTo(BinaryBuilderValue sb)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return sb.appendBytes(toString());
	  }

	  /**
	   * Append to a binary builder.
	   */
	  @Override
	  public StringValue appendTo(LargeStringBuilderValue sb)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return sb.append(toString());
	  }

	  /**
	   * Copy for assignment.
	   */
	  @Override
	  public Value copy()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Copy as an array item
	   */
	  @Override
	  public Value copyArrayItem()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return copy();
	  }

	  /**
	   * Copy as a return value
	   */
	  @Override
	  public Value copyReturn()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    // php/3a5d

	    return this;
	  }

	  /**
	   * Copy for serialization
	   */
	  //@Override
	  public final Value copy_(Env env) // TODO Overide method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    return copy(env, new IdentityHashMap<Value,Value>());
	  }

	  /**
	   * Copy for serialization
	   */
	  @Override
	  public Value copy(Env env, IdentityHashMap<Value,Value> map)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Copy for serialization
	   */
	  @Override
	  public Value copyTree(Env env, CopyRoot root)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Clone for the clone keyword
	   */
	  @Override
	  public Value clone(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Copy for saving a method's arguments.
	   */
	  @Override
	  public Value copySaveFunArg()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return copy();
	  }

	  /**
	   * Returns the type.
	   */
	  @Override
	  public String getType()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return "value";
	  }

	  /*
	   * Returns the resource type.
	   */
	  @Override
	  public String getResourceType()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return null;
	  }

	  /**
	   * Returns the current key
	   */
	  @Override
	  public Value key()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return BooleanValue.FALSE;
	  }

	  /**
	   * Returns the current value
	   */
	  @Override
	  public Value current()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return BooleanValue.FALSE;
	  }

	  /**
	   * Returns the next value
	   */
	  @Override
	  public Value next()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return BooleanValue.FALSE;
	  }

	  /**
	   * Returns the previous value
	   */
	  @Override
	  public Value prev()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return BooleanValue.FALSE;
	  }

	  /**
	   * Returns the end value.
	   */
	  @Override
	  public Value end()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return BooleanValue.FALSE;
	  }

	  /**
	   * Returns the array pointer.
	   */
	  @Override
	  public Value reset()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return BooleanValue.FALSE;
	  }

	  /**
	   * Shuffles the array.
	   */
	  @Override
	  public Value shuffle()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return BooleanValue.FALSE;
	  }

	  /**
	   * Pops the top array element.
	   */
	  @Override
	  public Value pop(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.warning("cannot pop a non-array");

	    return NullValue.NULL;
	  }

	  /**
	   * Finds the method name.
	   */
	  @Override
	  public AbstractFunction findFunction(String methodName)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return null;
	  }

	  //
	  // function invocation
	  //

	  /**
	   * Evaluates the function.
	   */
	  @Override
	  public Value call(Env env, Value []args)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Callable call = toCallable(env);

	    if (call != null)
	      return call.call(env, args);
	    else
	      return env.warning(L.l("{0} is not a valid function",
	                             this));
	  }

	  /**
	   * Evaluates the function, returning a reference.
	   */
	  @Override
	  public Value callRef(Env env, Value []args)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    AbstractFunction fun = env.getFunction(this);

	    if (fun != null)
	      return fun.callRef(env, args);
	    else
	      return env.warning(L.l("{0} is not a valid function",
	                             this));
	  }

	  /**
	   * Evaluates the function, returning a copy
	   */
	  @Override
	  public Value callCopy(Env env, Value []args)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    AbstractFunction fun = env.getFunction(this);

	    if (fun != null)
	      return fun.callCopy(env, args);
	    else
	      return env.warning(L.l("{0} is not a valid function",
	                             this));
	  }

	  /**
	   * Evaluates the function.
	   */
	  @Override
	  public Value call(Env env) // TODO Revise
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
		  return super.call(env);
	    //return call(env, NULL_ARG_VALUES);
	  }

	  /**
	   * Evaluates the function.
	   */
	  @Override
	  public Value callRef(Env env) // TODO Revise
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
		  return super.callRef(env);
	    //return callRef(env, NULL_ARG_VALUES);
	  }

	  /**
	   * Evaluates the function with an argument .
	   */
	  @Override
	  public Value call(Env env, Value a1)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return call(env, new Value[] { a1 });
	  }

	  /**
	   * Evaluates the function with an argument .
	   */
	  @Override
	  public Value callRef(Env env, Value a1)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callRef(env, new Value[] { a1 });
	  }

	  /**
	   * Evaluates the function with arguments
	   */
	  @Override
	  public Value call(Env env, Value a1, Value a2)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return call(env, new Value[] { a1, a2 });
	  }

	  /**
	   * Evaluates the function with arguments
	   */
	  @Override
	  public Value callRef(Env env, Value a1, Value a2)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callRef(env, new Value[] { a1, a2 });
	  }

	  /**
	   * Evaluates the function with arguments
	   */
	  @Override
	  public Value call(Env env, Value a1, Value a2, Value a3)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return call(env, new Value[] { a1, a2, a3 });
	  }

	  /**
	   * Evaluates the function with arguments
	   */
	  @Override
	  public Value callRef(Env env, Value a1, Value a2, Value a3)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callRef(env, new Value[] { a1, a2, a3 });
	  }

	  /**
	   * Evaluates the function with arguments
	   */
	  @Override
	  public Value call(Env env, Value a1, Value a2, Value a3, Value a4)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return call(env, new Value[] { a1, a2, a3, a4 });
	  }

	  /**
	   * Evaluates the function with arguments
	   */
	  @Override
	  public Value callRef(Env env, Value a1, Value a2, Value a3, Value a4)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callRef(env, new Value[] { a1, a2, a3, a4 });
	  }

	  /**
	   * Evaluates the function with arguments
	   */
	  @Override
	  public Value call(Env env, Value a1, Value a2, Value a3, Value a4, Value a5)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return call(env, new Value[] { a1, a2, a3, a4, a5 });
	  }

	  /**
	   * Evaluates the function with arguments
	   */
	  @Override
	  public Value callRef(Env env,
	                       Value a1, Value a2, Value a3, Value a4, Value a5)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callRef(env, new Value[] { a1, a2, a3, a4, a5 });
	  }

	  //
	  // Methods invocation
	  //

	  /**
	   * Evaluates a method.
	   */
	  @Override
	  public Value callMethod(Env env,
	                          StringValue methodName, int hash,
	                          Value []args)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (isNull()) {
	      return env.error(L.l("Method call '{0}' is not allowed for a null value.",
	                           methodName));
	    }
	    else {
	      return env.error(L.l("'{0}' is an unknown method of {1}.",
	                           methodName,
	                           toDebugString()));
	    }
	  }

	  /**
	   * Evaluates a method.
	   */
	  //@Override
	  public final Value callMethod_(Env env,
	                                StringValue methodName,
	                                Value []args) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethod(env, methodName, hash, args);
	  }


	  /**
	   * Evaluates a method.
	   */
	  @Override
	  public Value callMethodRef(Env env,
	                             StringValue methodName, int hash,
	                             Value []args)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethod(env, methodName, hash, args);
	  }

	  /**
	   * Evaluates a method.
	   */
	  //@Override
	  public final Value callMethodRef_(Env env,
	                                   StringValue methodName,
	                                   Value []args) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethodRef(env, methodName, hash, args);
	  }

	  /**
	   * Evaluates a method with 0 args.
	   */
	  @Override
	  public Value callMethod(Env env, StringValue methodName, int hash) // TODO Revise
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
		  return super.callMethod(env, methodName, hash);
//	    return callMethod(env, methodName, hash, NULL_ARG_VALUES);
	  }

	  /**
	   * Evaluates a method with 0 args.
	   */
	  //@Override
	  public final Value callMethod_(Env env, StringValue methodName) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethod(env, methodName, hash);
	  }

	  /**
	   * Evaluates a method with 0 args.
	   */
	  @Override
	  public Value callMethodRef(Env env, StringValue methodName, int hash) // TODO Revise
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
		  return callMethodRef(env, methodName, hash);
//	    return callMethodRef(env, methodName, hash, NULL_ARG_VALUES);
	  }

	  /**
	   * Evaluates a method with 0 args.
	   */
	  //@Override
	  public final Value callMethodRef_(Env env, StringValue methodName) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethodRef(env, methodName, hash);
	  }

	  /**
	   * Evaluates a method with 1 arg.
	   */
	  @Override
	  public Value callMethod(Env env,
	                          StringValue methodName, int hash,
	                          Value a1)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethod(env, methodName, hash, new Value[] { a1 });
	  }

	  /**
	   * Evaluates a method with 1 arg.
	   */
	  //@Override
	  public final Value callMethod_(Env env,
	                                StringValue methodName,
	                                Value a1) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethod(env, methodName, hash, a1);
	  }

	  /**
	   * Evaluates a method with 1 arg.
	   */
	  @Override
	  public Value callMethodRef(Env env,
	                             StringValue methodName, int hash,
	                             Value a1)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethodRef(env, methodName, hash, new Value[] { a1 });
	  }

	  /**
	   * Evaluates a method with 1 arg.
	   */
	  //@Override
	  public final Value callMethodRef_(Env env,
	                                   StringValue methodName,
	                                   Value a1) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethodRef(env, methodName, hash, a1);
	  }

	  /**
	   * Evaluates a method with 2 args.
	   */
	  @Override
	  public Value callMethod(Env env,
	                          StringValue methodName, int hash,
	                          Value a1, Value a2)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethod(env, methodName, hash, new Value[] { a1, a2 });
	  }

	  /**
	   * Evaluates a method with 2 args.
	   */
	  //@Override
	  public final Value callMethod_(Env env,
	                                StringValue methodName,
	                                Value a1, Value a2) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethod(env, methodName, hash,
	                      a1, a2);
	  }

	  /**
	   * Evaluates a method with 2 args.
	   */
	  @Override
	  public Value callMethodRef(Env env,
	                             StringValue methodName, int hash,
	                             Value a1, Value a2)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethodRef(env, methodName, hash, new Value[] { a1, a2 });
	  }

	  /**
	   * Evaluates a method with 2 args.
	   */
	  //@Override
	  public final Value callMethodRef_(Env env,
	                                   StringValue methodName,
	                                   Value a1, Value a2) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethodRef(env, methodName, hash,
	                         a1, a2);
	  }

	  /**
	   * Evaluates a method with 3 args.
	   */
	  @Override
	  public Value callMethod(Env env,
	                          StringValue methodName, int hash,
	                          Value a1, Value a2, Value a3)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethod(env, methodName, hash, new Value[] { a1, a2, a3 });
	  }

	  /**
	   * Evaluates a method with 3 args.
	   */
	  //@Override
	  public final Value callMethod_(Env env,
	                                StringValue methodName,
	                                Value a1, Value a2, Value a3) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethod(env, methodName, hash,
	                      a1, a2, a3);
	  }

	  /**
	   * Evaluates a method with 3 args.
	   */
	  @Override
	  public Value callMethodRef(Env env,
	                             StringValue methodName, int hash,
	                             Value a1, Value a2, Value a3)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethodRef(env, methodName, hash, new Value[] { a1, a2, a3 });
	  }

	  /**
	   * Evaluates a method with 3 args.
	   */
	  //@Override
	  public final Value callMethodRef_(Env env,
	                                   StringValue methodName,
	                                   Value a1, Value a2, Value a3) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethodRef(env, methodName, hash,
	                         a1, a2, a3);
	  }

	  /**
	   * Evaluates a method with 4 args.
	   */
	  @Override
	  public Value callMethod(Env env,
	                          StringValue methodName, int hash,
	                          Value a1, Value a2, Value a3, Value a4)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethod(env, methodName, hash,
	                      new Value[] { a1, a2, a3, a4 });
	  }

	  /**
	   * Evaluates a method with 4 args.
	   */
	  //@Override
	  public final Value callMethod_(Env env,
	                                StringValue methodName,
	                                Value a1, Value a2, Value a3, Value a4) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethod(env, methodName, hash,
	                      a1, a2, a3, a4);
	  }

	  /**
	   * Evaluates a method with 4 args.
	   */
	  @Override
	  public Value callMethodRef(Env env,
	                             StringValue methodName, int hash,
	                             Value a1, Value a2, Value a3, Value a4)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethodRef(env, methodName, hash,
	                         new Value[] { a1, a2, a3, a4 });
	  }

	  /**
	   * Evaluates a method with 4 args.
	   */
	  //@Override
	  public final Value callMethodRef_(Env env,
	                                   StringValue methodName,
	                                   Value a1, Value a2, Value a3, Value a4) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethodRef(env, methodName, hash,
	                         a1, a2, a3, a4);
	  }

	  /**
	   * Evaluates a method with 5 args.
	   */
	  @Override
	  public Value callMethod(Env env,
	                          StringValue methodName, int hash,
	                          Value a1, Value a2, Value a3, Value a4, Value a5)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethod(env, methodName, hash,
	                      new Value[] { a1, a2, a3, a4, a5 });
	  }

	  /**
	   * Evaluates a method with 5 args.
	   */
	  //@Override
	  public final Value callMethod_(Env env,
	                             StringValue methodName,
	                             Value a1, Value a2, Value a3, Value a4, Value a5) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethod(env, methodName, hash,
	                         a1, a2, a3, a4, a5);
	  }

	  /**
	   * Evaluates a method with 5 args.
	   */
	  @Override
	  public Value callMethodRef(Env env,
	                             StringValue methodName, int hash,
	                             Value a1, Value a2, Value a3, Value a4, Value a5)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return callMethodRef(env, methodName, hash,
	                         new Value[] { a1, a2, a3, a4, a5 });
	  }

	  /**
	   * Evaluates a method with 5 args.
	   */
	  //@Override
	  public final Value callMethodRef_(Env env,
	                             StringValue methodName,
	                             Value a1, Value a2, Value a3, Value a4, Value a5) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    int hash = methodName.hashCodeCaseInsensitive();
	    
	    return callMethodRef(env, methodName, hash,
	                         a1, a2, a3, a4, a5);
	  }

	  //
	  // Methods from StringValue
	  //

	  /**
	   * Evaluates a method.
	   */
	  @SuppressWarnings("unused")
	private Value callClassMethod(Env env, AbstractFunction fun, Value []args)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return NullValue.NULL;
	  }

	  @SuppressWarnings("unused")
	private Value errorNoMethod(Env env, char []name, int nameLen)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    String methodName =  new String(name, 0, nameLen);

	    if (isNull()) {
	      return env.error(L.l("Method call '{0}' is not allowed for a null value.",
	                           methodName));
	    }
	    else {
	      return env.error(L.l("'{0}' is an unknown method of {1}.",
	                           methodName,
	                           toDebugString()));
	    }
	  }

	  //
	  // Arithmetic operations
	  //

	  /**
	   * Negates the value.
	   */
	  @Override
	  public Value neg()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return LongValue.create(- toLong());
	  }

	  /**
	   * Negates the value.
	   */
	  @Override
	  public Value pos()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return LongValue.create(toLong());
	  }

	  /**
	   * Adds to the following value.
	   */
	  @Override
	  public Value add(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (getValueType().isLongAdd() && rValue.getValueType().isLongAdd())
	      return LongValue.create(toLong() + rValue.toLong());

	    return DoubleValue.create(toDouble() + rValue.toDouble());
	  }

	  /**
	   * Multiplies to the following value.
	   */
	  @Override
	  public Value add(long lLong)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new DoubleValue(lLong + toDouble());
	  }

	  /**
	   * Pre-increment the following value.
	   */
	  @Override
	  public Value preincr(int incr)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return increment(incr);
	  }

	  /**
	   * Post-increment the following value.
	   */
	  @Override
	  public Value postincr(int incr)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return increment(incr);
	  }

	  /**
	   * Return the next integer
	   */
	  @Override
	  public Value addOne()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return add(1);
	  }

	  /**
	   * Return the previous integer
	   */
	  @Override
	  public Value subOne()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return sub(1);
	  }

	  /**
	   * Pre-increment the following value.
	   */
	  @Override
	  public Value preincr()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return increment(1);
	  }

	  /**
	   * Post-increment the following value.
	   */
	  @Override
	  public Value postincr()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return increment(1);
	  }

	  /**
	   * Pre-increment the following value.
	   */
	  @Override
	  public Value predecr()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return increment(-1);
	  }

	  /**
	   * Post-increment the following value.
	   */
	  @Override
	  public Value postdecr()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return increment(-1);
	  }

	  /**
	   * Increment the following value.
	   */
	  @Override
	  public Value increment(int incr)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    long lValue = toLong();

	    return LongValue.create(lValue + incr);
	  }

	  /**
	   * Subtracts to the following value.
	   */
	  @Override
	  public Value sub(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (getValueType().isLongAdd() && rValue.getValueType().isLongAdd())
	      return LongValue.create(toLong() - rValue.toLong());

	    return DoubleValue.create(toDouble() - rValue.toDouble());
	  }

	  /**
	   * Subtracts
	   */
	  @Override
	  public Value sub(long rLong)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new DoubleValue(toDouble() - rLong);
	  }


	  /**
	   * Substracts from the previous value.
	   */
	  @Override
	  public Value sub_rev(long lLong)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (getValueType().isLongAdd())
	      return LongValue.create(lLong - toLong());
	    else
	      return new DoubleValue(lLong - toDouble());
	  }

	  /**
	   * Multiplies to the following value.
	   */
	  @Override
	  public Value mul(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (getValueType().isLongAdd() && rValue.getValueType().isLongAdd())
	      return LongValue.create(toLong() * rValue.toLong());
	    else
	      return new DoubleValue(toDouble() * rValue.toDouble());
	  }

	  /**
	   * Multiplies to the following value.
	   */
	  @Override
	  public Value mul(long r)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (isLongConvertible())
	      return LongValue.create(toLong() * r);
	    else
	      return new DoubleValue(toDouble() * r);
	  }

	  /**
	   * Divides the following value.
	   */
	  @Override
	  public Value div(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (getValueType().isLongAdd() && rValue.getValueType().isLongAdd()) {
	      long l = toLong();
	      long r = rValue.toLong();

	      if (r != 0 && l % r == 0)
	        return LongValue.create(l / r);
	      else
	        return new DoubleValue(toDouble() / rValue.toDouble());
	    }
	    else
	      return new DoubleValue(toDouble() / rValue.toDouble());
	  }

	  /**
	   * Multiplies to the following value.
	   */
	  @Override
	  public Value div(long r)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    long l = toLong();

	    if (r != 0 && l % r == 0)
	      return LongValue.create(l / r);
	    else
	      return new DoubleValue(toDouble() / r);
	  }

	  /**
	   * modulo the following value.
	   */
	  @Override
	  public Value mod(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    double lDouble = toDouble();
	    double rDouble = rValue.toDouble();

	    return LongValue.create((long) lDouble % rDouble);
	  }

	  /**
	   * Shifts left by the value.
	   */
	  @Override
	  public Value lshift(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    long lLong = toLong();
	    long rLong = rValue.toLong();

	    return LongValue.create(lLong << rLong);
	  }

	  /**
	   * Shifts right by the value.
	   */
	  @Override
	  public Value rshift(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    long lLong = toLong();
	    long rLong = rValue.toLong();

	    return LongValue.create(lLong >> rLong);
	  }

	  /*
	   * Binary And.
	   */
	  @Override
	  public Value bitAnd(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return LongValue.create(toLong() & rValue.toLong());
	  }

	  /*
	   * Binary or.
	   */
	  @Override
	  public Value bitOr(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return LongValue.create(toLong() | rValue.toLong());
	  }

	  /**
	   * Binary xor.
	   */
	  @Override
	  public Value bitXor(Value rValue)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return LongValue.create(toLong() ^ rValue.toLong());
	  }

	  /**
	   * Absolute value.
	   */
	  @Override
	  public Value abs()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (getValueType().isDoubleCmp())
	      return new DoubleValue(Math.abs(toDouble()));
	    else
	      return LongValue.create(Math.abs(toLong()));
	  }

	  /**
	   * Returns the next array index based on this value.
	   */
	  @Override
	  public long nextIndex(long oldIndex)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return oldIndex;
	  }

	  //
	  // string functions
	  //

	  /**
	   * Returns the length as a string.
	   */
	  @Override
	  public int length()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toStringValue().length();
	  }

	  //
	  // Array functions
	  //

	  /**
	   * Returns the array size.
	   */
	  @Override
	  public int getSize()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return 1;
	  }

	  /**
	   * Returns the count, as returned by the global php count() function
	   */
	  @Override
	  public int getCount(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return 1;
	  }

	  /**
	   * Returns the count, as returned by the global php count() function
	   */
	  @Override
	  public int getCountRecursive(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getCount(env);
	  }

	  /**
	   * Returns an iterator for the key => value pairs.
	   */
	  @Override
	  public Iterator<Map.Entry<Value, Value>> getIterator(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getBaseIterator(env);
	  }

	  /**
	   * Returns an iterator for the key => value pairs.
	   */
	  @Override
	  public Iterator<Map.Entry<Value, Value>> getBaseIterator(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Set<Map.Entry<Value, Value>> emptySet = Collections.emptySet();

	    return emptySet.iterator();
	  }

	  /**
	   * Returns an iterator for the field keys.
	   * The default implementation uses the Iterator returned
	   * by {@link #getIterator(Env)}; derived classes may override and
	   * provide a more efficient implementation.
	   */
	  @Override
	  public Iterator<Value> getKeyIterator(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    final Iterator<Map.Entry<Value, Value>> iter = getIterator(env);

	    return new Iterator<Value>() {
	      @Override
	  public boolean hasNext() { return iter.hasNext(); }
	      @Override
	  public Value next()      { return iter.next().getKey(); }
	      @Override
	  public void remove()     { iter.remove(); }
	    };
	  }

	  /**
	   * Returns the field keys.
	   */
	  @Override
	  public Value []getKeyArray(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return NULL_VALUE_ARRAY;
	  }

	  /**
	   * Returns the field values.
	   */
	  @Override
	  public Value []getValueArray(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return NULL_VALUE_ARRAY;
	  }

	  /**
	   * Returns an iterator for the field values.
	   * The default implementation uses the Iterator returned
	   * by {@link #getIterator(Env)}; derived classes may override and
	   * provide a more efficient implementation.
	   */
	  @Override
	  public Iterator<Value> getValueIterator(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    final Iterator<Map.Entry<Value, Value>> iter = getIterator(env);

	    return new Iterator<Value>() {
	      @Override
	  public boolean hasNext() { return iter.hasNext(); }
	      @Override
	  public Value next()      { return iter.next().getValue(); }
	      @Override
	  public void remove()     { iter.remove(); }
	    };
	  }

	  //
	  // Object field references
	  //

	  /**
	   * Returns the field value
	   */
	  @Override
	  public Value getField(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return NullValue.NULL;
	  }

	  /**
	   * Returns the field ref.
	   */
	  @Override
	  public Var getFieldVar(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getField(env, name).toVar();
	  }

	  /**
	   * Returns the field used as a method argument
	   */
	  @Override
	  public Value getFieldArg(Env env, StringValue name, boolean isTop)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getFieldVar(env, name);
	  }

	  /**
	   * Returns the field ref for an argument.
	   */
	  @Override
	  public Value getFieldArgRef(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getFieldVar(env, name);
	  }

	  /**
	   * Returns the value for a field, creating an object if the field
	   * is unset.
	   */
	  @Override
	  public Value getFieldObject(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value v = getField(env, name);

	    if (! v.isset()) {
	      v = env.createObject();

	      putField(env, name, v);
	    }

	    return v;
	  }

	  /**
	   * Returns the value for a field, creating an object if the field
	   * is unset.
	   */
	  @Override
	  public Value getFieldArray(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value v = getField(env, name);

	    Value array = v.toAutoArray();

	    if (v != array) {
	      putField(env, name, array);

	      return array;
	    }
	    else if (array.isString()) {
	      // php/0484
	      return getFieldVar(env, name);
	    }
	    else {
	      return v;
	    }
	  }

	  /**
	   * Returns the field ref.
	   */
	  @Override
	  public Value putField(Env env, StringValue name, Value object)
	  {
		  return value.putField(env, name, object);
		  
//		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
//
//	    return NullValue.NULL;
	  }

	  //@Override
	  public final Value putField_(Env env, StringValue name, Value value,
	                              Value innerIndex, Value innerValue) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    Value result = value.append(innerIndex, innerValue);

	    return putField(env, name, result);
	  }

	  @Override
	  public void setFieldInit(boolean isInit)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	  }

	  /**
	   * Returns true if the object is in a __set() method call.
	   * Prevents infinite recursion.
	   */
	  @Override
	  public boolean isFieldInit()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true if the field is set
	   */
	  @Override
	  public boolean issetField(StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Removes the field ref.
	   */
	  @Override
	  public void unsetField(StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	  }

	  /**
	   * Removes the field ref.
	   */
	  @Override
	  public void unsetArray(Env env, StringValue name, Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	  }

	  /**
	   * Removes the field ref.
	   */
	  @Override
	  public void unsetThisArray(Env env, StringValue name, Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	  }

	  /**
	   * Returns the field as a Var or Value.
	   */
	  @Override
	  public Value getThisField(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getField(env, name);
	  }

	  /**
	   * Returns the field as a Var.
	   */
	  @Override
	  public Var getThisFieldVar(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getThisField(env, name).toVar();
	  }

	  /**
	   * Returns the field used as a method argument
	   */
	  @Override
	  public Value getThisFieldArg(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getThisFieldVar(env, name);
	  }

	  /**
	   * Returns the field ref for an argument.
	   */
	  @Override
	  public Value getThisFieldArgRef(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return getThisFieldVar(env, name);
	  }

	  /**
	   * Returns the value for a field, creating an object if the field
	   * is unset.
	   */
	  @Override
	  public Value getThisFieldObject(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value v = getThisField(env, name);

	    if (! v.isset()) {
	      v = env.createObject();

	      putThisField(env, name, v);
	    }

	    return v;
	  }

	  /**
	   * Returns the value for a field, creating an object if the field
	   * is unset.
	   */
	  @Override
	  public Value getThisFieldArray(Env env, StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value v = getThisField(env, name);

	    Value array = v.toAutoArray();

	    if (v == array)
	      return v;
	    else {
	      putField(env, name, array);

	      return array;
	    }
	  }

	  /**
	   * Initializes a new field, does not call __set if it is defined.
	   */
	  @Override
	  public void initField(StringValue key,
	                        Value value,
	                        FieldVisibility visibility)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    putThisField(Env.getInstance(), key, value);
	  }

	  /**
	   * Returns the field ref.
	   */
	  @Override
	  public Value putThisField(Env env, StringValue name, Value object)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return putField(env, name, object);
	  }

	  /**
	   * Sets an array field ref.
	   */
	  @Override
	  public Value putThisField(Env env,
	                            StringValue name,
	                            Value array,
	                            Value index,
	                            Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value result = array.append(index, value);

	    putThisField(env, name, result);

	    return value;
	  }

	  /**
	   * Returns true if the field is set
	   */
	  @Override
	  public boolean issetThisField(StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return issetField(name);
	  }

	  /**
	   * Removes the field ref.
	   */
	  @Override
	  public void unsetThisField(StringValue name)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    unsetField(name);
	  }

	  //
	  // field convenience
	  //

	  @Override
	  public Value putField(Env env, String name, Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return putThisField(env, env.createString(name), value);
	  }

	  /**
	   * Returns the array ref.
	   */
	  @Override
	  public Value get(Value index)
	  {
		  return value.get(index);
		  
//		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
//
//	    return UnsetValue.UNSET;
	  }

	  /**
	   * Returns a reference to the array value.
	   */
	  @Override
	  public Var getVar(Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value value = get(index);

	    if (value.isVar())
	      return (Var) value;
	    else
	      return new Var(value);
	  }

	  /**
	   * Returns a reference to the array value.
	   */
	  @Override
	  public Value getRef(Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return get(index);
	  }

	  /**
	   * Returns the array ref as a function argument.
	   */
	  @Override
	  public Value getArg(Value index, boolean isTop)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return get(index);
	  }

	  /**
	   * Returns the array value, copying on write if necessary.
	   */
	  @Override
	  public Value getDirty(Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return get(index);
	  }

	  /**
	   * Returns the value for a field, creating an array if the field
	   * is unset.
	   */
	  @Override
	  public Value getArray()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return this;
	  }

	  /**
	   * Returns the value for a field, creating an array if the field
	   * is unset.
	   */
	  @Override
	  public Value getArray(Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value var = getVar(index);
	    
	    return var.toAutoArray();
	  }

	  /**
	   * Returns the value for the variable, creating an object if the var
	   * is unset.
	   */
	  @Override
	  public Value getObject(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return NullValue.NULL;
	  }

	  /**
	   * Returns the value for a field, creating an object if the field
	   * is unset.
	   */
	  @Override
	  public Value getObject(Env env, Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value var = getVar(index);
	    
	    if (var.isset())
	      return var.toValue();
	    else {
	      var.set(env.createObject());
	      
	      return var.toValue();
	    }
	  }

	  @Override
	  public boolean isVar()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }
	  
	  /**
	   * Sets the value ref.
	   */
	  @Override
	  public Value set(Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return value;
	  }

	  /**
	   * Sets the array ref and returns the value
	   */
	  @Override
	  public Value put(Value index, Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Env.getCurrent().warning(L.l("{0} cannot be used as an array",
	                                 toDebugString()));
	    
	    return value;
	  }

	  /**
	   * Sets the array ref.
	   */
	  //@Override
	  public final Value put_(Value index, Value value,
	                         Value innerIndex, Value innerValue) // TODO Override method
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    Value result = value.append(innerIndex, innerValue);

	    put(index, result);

	    return innerValue;
	  }

	  /**
	   * Appends an array value
	   */
	  @Override
	  public Value put(Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    /*
	    Env.getCurrent().warning(L.l("{0} cannot be used as an array",
	                                 toDebugString()));
	                                 */

	    
	    return value;
	  }

	  /**
	   * Sets the array value, returning the new array, e.g. to handle
	   * string update ($a[0] = 'A').  Creates an array automatically if
	   * necessary.
	   */
	  @Override
	  public Value append(Value index, Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value array = toAutoArray();
	    
	    if (array.isArray())
	      return array.append(index, value);
	    else
	      return array;
	  }

	  /**
	   * Sets the array tail, returning the Var of the tail.
	   */
	  @Override
	  public Var putVar()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return new Var();
	  }

	  /**
	   * Appends a new object
	   */
	  @Override
	  public Value putObject(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    Value value = env.createObject();

	    put(value);

	    return value;
	  }

	  /**
	   * Return true if the array value is set
	   */
	  @Override
	  public boolean isset(Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return false;
	  }

	  /**
	   * Returns true if the key exists in the array.
	   */
	  @Override
	  public boolean keyExists(Value key)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return isset(key);
	  }

	  /**
	   * Returns the corresponding value if this array contains the given key
	   *
	   * @param key to search for in the array
	   *
	   * @return the value if it is found in the array, NULL otherwise
	   */
	  @Override
	  public Value containsKey(Value key)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return null;
	  }

	  /**
	   * Return unset the value.
	   */
	  @Override
	  public Value remove(Value index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return UnsetValue.UNSET;
	  }

	  /**
	   * Takes the values of this array, unmarshalls them to objects of type
	   * <i>elementType</i>, and puts them in a java array.
	   */
	  @Override
	  public Object valuesToArray(Env env, @SuppressWarnings("rawtypes") Class elementType)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.error(L.l("Can't assign {0} with type {1} to {2}[]",
	                  this,
	                  this.getClass(),
	                  elementType));
	    return null;
	  }

	  /**
	   * Returns the character at the named index.
	   */
	  @Override
	  public Value charValueAt(long index)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return NullValue.NULL;
	  }

	  /**
	   * Sets the character at the named index.
	   */
	  @Override
	  public Value setCharValueAt(long index, Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return NullValue.NULL;
	  }

	  /**
	   * Prints the value.
	   * @param env
	   */
	  @Override
	  public void print(Env env)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.print(toString(env));
	  }

	  /**
	   * Prints the value.
	   * @param env
	   */
	  @Override
	  public void print(Env env, WriteStream out)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    try {
	      out.print(toString(env));
	    } catch (IOException e) {
	      throw new QuercusRuntimeException(e);
	    }
	  }

	  /**
	   * Serializes the value.
	   *
	   * @param env
	   * @param sb holds result of serialization
	   * @param serializeMap holds reference indexes
	   */
	  @Override
	  public void serialize(Env env,
	                        StringBuilder sb,
	                        SerializeMap serializeMap)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    serializeMap.incrementIndex();

	    serialize(env, sb);
	  }

	  /**
	   * Encodes the value in JSON.
	   */
	  @Override
	  public void jsonEncode(Env env, StringValue sb)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    env.warning(L.l("type is unsupported; json encoded as null"));

	    sb.append("null");
	  }

	  /**
	   * Serializes the value.
	   */
	  @Override
	  public void serialize(Env env, StringBuilder sb)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    throw new UnsupportedOperationException(getClass().getName());
	  }

	  /**
	   * Exports the value.
	   */
	  @Override
	  public void varExport(StringBuilder sb)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    throw new UnsupportedOperationException(getClass().getName());
	  }

	  /**
	   * Binds a Java object to this object.
	   */
	  @Override
	  public void setJavaObject(Value value)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	  }

	  //
	  // Java generator code
	  //

	  /**
	   * Generates code to recreate the expression.
	   *
	   * @param out the writer to the Java source code.
	   */
	  @Override
	  public void generate(PrintWriter out)
	    throws IOException
	  {
	  }

	  protected static void printJavaChar(PrintWriter out, char ch)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    switch (ch) {
	      case '\r':
	        out.print("\\r");
	        break;
	      case '\n':
	        out.print("\\n");
	        break;
	      //case '\"':
	      //  out.print("\\\"");
	      //  break;
	      case '\'':
	        out.print("\\\'");
	        break;
	      case '\\':
	        out.print("\\\\");
	        break;
	      default:
	        out.print(ch);
	        break;
	    }
	  }

	  protected static void printJavaString(PrintWriter out, StringValue s)
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    if (s == null) {
	      out.print("");
	      return;
	    }

	    int len = s.length();
	    for (int i = 0; i < len; i++) {
	      char ch = s.charAt(i);

	      switch (ch) {
	      case '\r':
	        out.print("\\r");
	        break;
	      case '\n':
	        out.print("\\n");
	        break;
	      case '\"':
	        out.print("\\\"");
	        break;
	      case '\'':
	        out.print("\\\'");
	        break;
	      case '\\':
	        out.print("\\\\");
	        break;
	      default:
	        out.print(ch);
	        break;
	      }
	    }
	  }

	  @Override
	  public String toInternString()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toString().intern();
	  }

	  @Override
	  public String toDebugString()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return toString();
	  }

	  //@Override
	  public final void varDump_(Env env,
	                            WriteStream out,
	                            int depth,
	                            IdentityHashMap<Value, String> valueSet) // TODO Override method
	    throws IOException
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
		
	    if (valueSet.get(this) != null) {
	      out.print("*recursion*");
	      return;
	    }

	    valueSet.put(this, "printing");

	    try {
	      varDumpImpl(env, out, depth, valueSet);
	    }
	    finally {
	      valueSet.remove(this);
	    }
	  }

	  protected void varDumpImpl(Env env,
	                             WriteStream out,
	                             int depth,
	                             IdentityHashMap<Value, String> valueSet)
	    throws IOException
	  {
	    out.print("resource(" + toString() + ")");
	  }

	  //@Override
	  public final void printR_(Env env,
	                           WriteStream out,
	                           int depth,
	                           IdentityHashMap<Value, String> valueSet) // TODO Override method
	    throws IOException
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");
			
	    if (valueSet.get(this) != null) {
	      out.print("*recursion*");
	      return;
	    }

	    valueSet.put(this, "printing");

	    try {
	      printRImpl(env, out, depth, valueSet);
	    }
	    finally {
	      valueSet.remove(this);
	    }
	  }

	  protected void printRImpl(Env env,
	                            WriteStream out,
	                            int depth,
	                            IdentityHashMap<Value, String> valueSet)
	    throws IOException
	  {
	    out.print(toString());
	  }

	  protected void printDepth(WriteStream out, int depth)
	    throws IOException
	  {
	    for (int i = 0; i < depth; i++)
	      out.print(' ');
	  }

	  @Override
	  public int getHashCode()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return hashCode();
	  }

	  @Override
	  public int hashCode()
	  {
		Logging.LOGGER.fine("Unsupported operation for a ScopedValue.");

	    return 1021;
	  }

}
