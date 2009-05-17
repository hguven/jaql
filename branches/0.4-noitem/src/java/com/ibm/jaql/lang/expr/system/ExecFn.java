/*
 * Copyright (C) IBM Corp. 2009.
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
package com.ibm.jaql.lang.expr.system;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.UndeclaredThrowableException;

import com.ibm.jaql.json.type.JsonString;
import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.json.util.JsonIterator;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.IterExpr;
import com.ibm.jaql.lang.expr.core.JaqlFn;


@JaqlFn(fnName = "exec", minArgs = 2, maxArgs = 2)
public class ExecFn extends IterExpr
{
  /**
   * exprs
   * 
   * @param exprs
   */
  public ExecFn(Expr[] exprs)
  {
    super(exprs);
  }

  /**
   * @param expr: shell( [*<*>]? stdin, string cmd ) -> [*<*>]
   */
  public ExecFn(Expr expr0, Expr expr1)
  {
    super(expr0, expr1);
  }

  protected JsonIterator iter;
  protected Throwable error;
  protected Process proc;
  
  /**
   * 
   */
  @Override
  public JsonIterator iter(final Context context) throws Exception
  {
    final JsonString cmd = (JsonString)exprs[1].eval(context);
    if( cmd == null )
    {
      return JsonIterator.NIL;
    }
    iter = exprs[0].iter(context);
    if( iter.isNull() )
    {
      return JsonIterator.NIL;
    }
    proc = Runtime.getRuntime().exec(cmd.toString());
    // TODO: add thread pool to context
    InputThread inputThread = new InputThread();
    ErrorThread errorThread = new ErrorThread();
    final JsonString str = new JsonString();
    try
    {
      InputStream is = proc.getInputStream();
      final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      errorThread.start();
      inputThread.start();
      return new JsonIterator(str)
      {
        @Override
        public boolean moveNext() throws Exception
        {
          try
          {
            String s = reader.readLine();
            if( s == null )
            {
              reader.close();
              int rc = proc.waitFor();
              if( rc != 0 )
              {
                System.err.println("non-zero exit code from process ["+cmd+"]: "+rc);
              }
              return false;
            }
            str.set(s);
            return true; // currentValue == str
          }
          catch (Throwable e)
          {
            if( error == null )
            {
              error = e;
            }
            proc.destroy();
            if( error instanceof Exception )
            {
              throw (Exception)error;
            }
            throw new UndeclaredThrowableException(error);
          }
        }
      };
    }
    catch (Throwable e)
    {
      if( error == null )
      {
        error = e;
      }
      proc.destroy();
      if( error instanceof Exception )
      {
        throw (Exception)error;
      }
      throw new UndeclaredThrowableException(error);
    }
  }
  
  protected class InputThread extends Thread
  {
    @Override
    public void run()
    {
      try
      {
        OutputStream os = proc.getOutputStream();
        PrintStream out = new PrintStream(new BufferedOutputStream(os));
        for (JsonValue sv : iter)
        {
          // TODO:  force jstrings here? add i/o layer here? add serialize function that has i/o layer?
          JsonString s = (JsonString)sv;
          if( s != null )
          {
            out.println(s.toString());
          }
        }
        out.close();
      }
      catch (Throwable e)
      {
        if( error == null )
        {
          error = e;
        }
        proc.destroy();
      }
    }
  }

  protected class ErrorThread extends Thread
  {
    @Override
    public void run()
    {
      try
      {
        InputStream is = proc.getErrorStream();
        byte[] buffer = new byte[1024];
        int n;
        while( (n = is.read(buffer)) >= 0 )
        {
          System.err.write(buffer,0,n); // TODO: use logging? 
        }
        System.err.flush();
        is.close();
      }
      catch (Throwable e)
      {
        if( error == null )
        {
          error = e;
        }
        proc.destroy();
      }
    }
  }
}