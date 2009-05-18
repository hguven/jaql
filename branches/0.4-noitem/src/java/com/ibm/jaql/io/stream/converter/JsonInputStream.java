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
package com.ibm.jaql.io.stream.converter;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ibm.jaql.io.converter.StreamToJson;
import com.ibm.jaql.io.serialization.def.DefaultFullSerializer;
import com.ibm.jaql.json.type.JsonValue;

/** Generates {@link Item}s from a binary input stream containing serialized items.
 * 
 */
public class JsonInputStream implements StreamToJson<JsonValue>
{
  private boolean   arrAcc = true;
  private boolean   isDone = false;
  private DataInput input;
  private DefaultFullSerializer serializer = DefaultFullSerializer.getInstance();

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.io.converter.StreamToItem#setInputStream(java.io.InputStream)
   */
  public void setInputStream(InputStream in)
  {
    this.input = new DataInputStream(in);
  }
  
  /* (non-Javadoc)
   * @see com.ibm.jaql.io.converter.StreamToItem#setArrayAccessor(boolean)
   */
  public void setArrayAccessor(boolean a) {
    arrAcc = a;
  }

  /* (non-Javadoc)
   * @see com.ibm.jaql.io.converter.StreamToItem#isArrayAccessor()
   */
  public boolean isArrayAccessor() {
    return arrAcc;
  }
  
  /*
   * (non-Javadoc) 
   * @see com.ibm.jaql.io.converter.StreamToItem#read(com.ibm.jaql.json.type.Item)
   */
  public JsonValue read(JsonValue v) throws IOException
  {
    if(isDone)
      return null;
    try
    {
      v = serializer.read(input, v);
      if(!arrAcc) {
        isDone = true;
      }
    }
    catch (java.io.EOFException eof)
    {
      return null;
    }
    return v;
  }
}
