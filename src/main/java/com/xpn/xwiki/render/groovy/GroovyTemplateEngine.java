/**
 * ===================================================================
 *
 * Copyright (c) 2003,2004 Ludovic Dubost, All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details, published at 
 * http://www.gnu.org/copyleft/lesser.html or in lesser.txt in the
 * root folder of this distribution.

 * Created by
 * User: Ludovic Dubost
 * Date: 19 sept. 2004
 * Time: 21:10:40
 */
package com.xpn.xwiki.render.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.InvokerHelper;

/*
 * $Id: GroovyTemplateEngine.java 444 2005-01-20 00:06:15Z ldubost $version Mar 8, 2004 2:11:00 AM $user Exp $
 *
 * Copyright 2003 (C) Sam Pullara. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain
 * copyright statements and notices. Redistributions must also contain a copy
 * of this document. 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the distribution. 3.
 * The name "groovy" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Codehaus. For
 * written permission, please contact info@codehaus.org. 4. Products derived
 * from this Software may not be called "groovy" nor may "groovy" appear in
 * their names without prior written permission of The Codehaus. "groovy" is a
 * registered trademark of The Codehaus. 5. Due credit should be given to The
 * Codehaus - http://groovy.codehaus.org/
 *
 * THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *
 */


   /**
* This simple template engine uses JSP <% %> script and <%= %> expression syntax.  It also lets you use normal groovy expressions in
* the template text much like the new JSP EL functionality.  The variable 'out' is bound to the writer that the template is being written to.
*
* @author sam
*/
   public class GroovyTemplateEngine extends TemplateEngine {

   /* (non-Javadoc)
    * @see groovy.util.TemplateEngine#createTemplate(java.io.Reader)
    */
   public Template createTemplate(Reader reader) throws CompilationFailedException, ClassNotFoundException, IOException {
       com.xpn.xwiki.render.groovy.GroovyTemplateEngine.SimpleTemplate template = new com.xpn.xwiki.render.groovy.GroovyTemplateEngine.SimpleTemplate();
       GroovyShell shell = new GroovyShell();
       String script = template.parse(reader);
       template.script = shell.parse(script);
       return template;
   }

   private static class SimpleTemplate implements Template {

       private Script script;
       private Binding binding;
       private Map map;

       /**
        * Set the binding for the template.  Keys will be converted to Strings.
        *
        * @see groovy.text.Template#setBinding(java.util.Map)
        */
       public void setBinding(final Map map) {
           this.map = map;
           binding = new Binding(map);
       }

       /**
        * Write the template document with the set binding applied to the writer.
        *
        * @see groovy.lang.Writable#writeTo(java.io.Writer)
        */
       public Writer writeTo(Writer writer) throws IOException {
           if (binding == null) binding = new Binding();
           Script scriptObject = InvokerHelper.createScript(script.getClass(), binding);
           PrintWriter pw = new PrintWriter(writer);
           scriptObject.setProperty("out", pw);
           scriptObject.run();
           pw.flush();
           return writer;
       }

       /**
        * Convert the template and binding into a result String.
        *
        * @see java.lang.Object#toString()
        */
       public String toString() {
           try {
               StringWriter sw = new StringWriter();
               writeTo(sw);
               return sw.toString();
           } catch (Exception e) {
               return e.toString();
           }
       }

       /**
        * Parse the text document looking for <% or <%= and then call out to the appropriate handler, otherwise copy the text directly
        * into the script while escaping quotes.
        *
        * @param reader
        * @return
        * @throws IOException
        */
       private String parse(Reader reader) throws IOException {
           if (!reader.markSupported()) {
               reader = new BufferedReader(reader);
           }
           StringWriter sw = new StringWriter();
           startScript(sw);
           boolean start = false;
           int c;
           while((c = reader.read()) != -1) {
               if (c == '<') {
                   c = reader.read();
                   if (c != '%') {
                       sw.write('<');
                   } else {
                       reader.mark(1);
                       c = reader.read();
                       if (c == '=') {
                           groovyExpression(reader, sw);
                       } else {
                           reader.reset();
                           groovySection(reader, sw);
                       }
                       continue;
                   }
               }
               if (c == '\"') {
                   sw.write('\\');
               }
               sw.write(c);
           }
           endScript(sw);
           String result = sw.toString();
           //System.out.println( "source text:\n" + result );
           return result;
       }

       private void startScript(StringWriter sw) {
           sw.write("/* Generated by GroovyTemplateEngine */ ");
           sw.write("out.print(\"");
       }

       private void endScript(StringWriter sw) {
           sw.write("\");\n");
       }

       /**
        * Closes the currently open write and writes out the following text as a GString expression until it reaches an end %>.
        *
        * @param reader
        * @param sw
        * @throws IOException
        */
       private void groovyExpression(Reader reader, StringWriter sw) throws IOException {
           sw.write("\");out.print(\"${");
           int c;
           while((c = reader.read()) != -1) {
               if (c == '%') {
                   c = reader.read();
                   if (c != '>') {
                       sw.write('%');
                   } else {
                       break;
                   }
               }
               sw.write(c);
           }
           sw.write("}\");out.print(\"");
       }

       /**
        * Closes the currently open write and writes the following text as normal Groovy script code until it reaches an end %>.
        *
        * @param reader
        * @param sw
        * @throws IOException
        */
       private void groovySection(Reader reader, StringWriter sw) throws IOException {
           sw.write("\");");
           int c;
           while((c = reader.read()) != -1) {
               if (c == '%') {
                   c = reader.read();
                   if (c != '>') {
                       sw.write('%');
                   } else {
                       break;
                   }
               }
               sw.write(c);
           }
           sw.write(";out.print(\"");
       }

       public Writable make() {
           return make(null);
       }

       public Writable make(final Map map) {
           return new Writable() {
               /**
                * Write the template document with the set binding applied to the writer.
                *
                * @see groovy.lang.Writable#writeTo(java.io.Writer)
                */
               public Writer writeTo(Writer writer) throws IOException {
                   Binding binding;
                   if (map == null) binding = new Binding(); else binding = new Binding(map);
                   Script scriptObject = InvokerHelper.createScript(script.getClass(), binding);
                   PrintWriter pw = new PrintWriter(writer);
                   scriptObject.setProperty("out", pw);
                   scriptObject.run();
                   pw.flush();
                   return writer;
               }

               /**
                * Convert the template and binding into a result String.
                *
                * @see java.lang.Object#toString()
                */
               public String toString() {
                   try {
                       StringWriter sw = new StringWriter();
                       writeTo(sw);
                       return sw.toString();
                   } catch (Exception e) {
                       return e.toString();
                   }
               }
           };
       }
   }
   }
