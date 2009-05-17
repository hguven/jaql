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
package com.ibm.jaql.lang.expr.core;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

import com.ibm.jaql.json.type.JsonArray;
import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.json.util.JsonIterator;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.core.Var;
import com.ibm.jaql.lang.core.VarMap;
import com.ibm.jaql.lang.expr.path.PathFieldValue;
import com.ibm.jaql.util.Bool3;

/** A variable.
 * 
 */
public class VarExpr extends Expr
{
  Var var;

  /**
   * @param var
   */
  public VarExpr(Var var)
  {
    super(NO_EXPRS);
    this.var = var;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#isConst()
   */
  @Override
  public boolean isConst()
  {
    // TODO: do this?
    //    if( var.value != null )
    //    {
    //      return true;
    //    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#isNull()
   */
  @Override
  public Bool3 isNull()
  {
    if (var.expr == null)
    {
      return Bool3.UNKNOWN;
    }
    if (var.value != null)
    {
      return Bool3.valueOf(var.value == null);
    }
    else
    {
      return var.expr.isNull();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#isArray()
   */
  @Override
  public Bool3 isArray()
  {
    if (var.expr == null)
    {
      return Bool3.UNKNOWN;
    }
    if (var.value != null)
    {
      JsonValue v = var.value;
      return Bool3.valueOf(v == null || v instanceof JsonArray);
    }
    else
    {
      return var.expr.isArray();
    }
  }

  //  public VarExpr(Env env, String varName)
  //  {
  //    this(env.inscope(varName));
  //  }
  //
  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#decompile(java.io.PrintStream,
   *      java.util.HashSet)
   */
  public void decompile(PrintStream exprText, HashSet<Var> capturedVars)
      throws Exception
  {
    exprText.print(var.name);
    capturedVars.add(var);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#clone(com.ibm.jaql.lang.core.VarMap)
   */
  public VarExpr clone(VarMap varMap)
  {
    Var v = varMap.get(var);
    return new VarExpr(v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#eval(com.ibm.jaql.lang.core.Context)
   */
  public JsonValue eval(Context context) throws Exception
  {
    return var.getValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#iter(com.ibm.jaql.lang.core.Context)
   */
  public JsonIterator iter(Context context) throws Exception
  {
    return var.getIter();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#replaceVar(com.ibm.jaql.lang.core.Var,
   *      com.ibm.jaql.lang.core.Var)
   */
  public void replaceVar(Var oldVar, Var newVar)
  {
    if (oldVar == var)
    {
      var = newVar;
      subtreeModified();
    }
  }
  
  /**
   * Replace all uses of $oldVar with $recVar.fieldName
   * 
   * @param oldVar
   * @param recVar
   * @param fieldName
   * @return
   */
  public Expr replaceVar(Var oldVar, Var recVar, String fieldName)
  {
    if (oldVar == var)
    {
      Expr proj = PathFieldValue.byName(recVar, fieldName);
      this.replaceInParent(proj);
      return proj;
    }
    return this;
  }

  /**
   * @return
   */
  public final Var var()
  {
    return var;
  }

  /**
   * @param var
   */
  public void setVar(Var var)
  {
    this.var = var;
  }
  
  @Override
  public void getVarUses(Var var, ArrayList<Expr> uses)
  {
    if( this.var == var )
    {
      uses.add(this);
    }
  }

}