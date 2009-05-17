/*
 * Copyright (C) IBM Corp. 2008.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.jaql.json.type;

import java.math.BigDecimal;


/**
 * 
 */
public class JsonDouble extends JsonNumeric
{
  public double value;

  /**
   * 
   */
  public JsonDouble()
  {
  }

  /**
   * @param value
   */
  public JsonDouble(double value)
  {
    this.value = value;
  }

  /**
   * @param str
   */
  public JsonDouble(String str)
  {
    value = Double.parseDouble(str);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JNumeric#decimalValue()
   */
  @Override
  public BigDecimal decimalValue()
  {
    return new BigDecimal(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JNumeric#doubleValue()
   */
  @Override
  public double doubleValue()
  {
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JNumeric#intValue()
   */
  @Override
  public int intValue()
  {
    return (int) value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JNumeric#intValueExact()
   */
  @Override
  public int intValueExact()
  {
    int x = (int) value;
    if (x != value) // TODO: is this the best way to determine exactness?
    {
      throw new ArithmeticException("cannot convert to int: " + value);
    }
    return x;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JNumeric#longValue()
   */
  @Override
  public long longValue()
  {
    return (long) value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JNumeric#longValueExact()
   */
  @Override
  public long longValueExact()
  {
    long x = (long) value;
    if (x != value) // TODO: is this the best way to determine exactness?
    {
      throw new ArithmeticException("cannot convert to long exactly: " + value);
    }
    return x;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JNumeric#negate()
   */
  @Override
  public void negate()
  {
    value = -value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JValue#toJSON()
   */
  @Override
  public String toJson()
  {
    return value + "d"; // TODO: flag to disable suffix for JSON compatibility
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JValue#copy(com.ibm.jaql.json.type.JValue)
   */
  @Override
  public void setCopy(JsonValue jvalue) throws Exception
  {
    JsonDouble x = (JsonDouble) jvalue;
    value = x.value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JValue#getEncoding()
   */
  @Override
  public JsonEncoding getEncoding()
  {
    return JsonEncoding.DOUBLE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JValue#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Object obj)
  {
    JsonDouble that = (JsonDouble) obj;
    if (this.value < that.value)
    {
      return -1;
    }
    else if (this.value > that.value)
    {
      return +1;
    }
    else
    {
      return 0;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.json.type.JValue#longHashCode()
   */
  @Override
  public long longHashCode()
  {
    long x = Double.doubleToLongBits(value);
    return JsonLong.longHashCode(x);
  }
}