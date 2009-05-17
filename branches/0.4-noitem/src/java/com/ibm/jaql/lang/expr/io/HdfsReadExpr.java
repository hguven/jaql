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
package com.ibm.jaql.lang.expr.io;

import com.ibm.jaql.io.Adapter;
import com.ibm.jaql.json.type.JsonString;
import com.ibm.jaql.lang.core.Env;
import com.ibm.jaql.lang.expr.core.ConstExpr;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.JaqlFn;
import com.ibm.jaql.lang.expr.core.MacroExpr;
import com.ibm.jaql.lang.expr.core.NameValueBinding;
import com.ibm.jaql.lang.expr.core.RecordExpr;

/**
 * 
 */
@JaqlFn(fnName = "hdfsRead", minArgs = 1, maxArgs = 2)
public class HdfsReadExpr extends MacroExpr
{

  /**
   * @param exprs
   */
  public HdfsReadExpr(Expr[] exprs)
  {
    super(exprs);
  }

  /**
   * @param expr
   */
  public HdfsReadExpr(Expr expr)
  {
    this(new Expr[]{expr});
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.MacroExpr#expand(com.ibm.jaql.lang.core.Env)
   */
  @Override
  public Expr expand(Env env) throws Exception
  {
    NameValueBinding tField = new NameValueBinding(Adapter.TYPE_NAME,
        new ConstExpr(new JsonString("hdfs")));
    NameValueBinding lField = new NameValueBinding(Adapter.LOCATION_NAME,
        exprs[0]);
    Expr rec = new RecordExpr(new Expr[]{tField, lField});
    if (exprs.length == 2)
    {
      NameValueBinding oField = new NameValueBinding(Adapter.INOPTIONS_NAME,
          exprs[1]);
      rec.addChild(oField);
    }
    return new ReadFn(rec);
  }
}