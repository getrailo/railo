<!--- 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ---><cffunction name="dump" output="yes" returntype="void" 
	hint="Outputs the elements, variables and values of most kinds of CFML objects. Useful for debugging. You can display the contents of simple and complex variables, objects, components, user-defined functions, and other elements."><!---
	---><cfargument 
    
    name="var" type="object" required="no" hint="Variable to display."><cfargument 
    name="expand" type="boolean" required="no" hint="expands views"><cfargument 
    name="format" type="string" required="no" hint="specify the output format of the dump, the following formats are supported:
- simple: - a simple html output (no javascript or css)
- text (default output=""console""): plain text output (no html)
- html (default output=""browser""): regular output with html/css/javascript
- classic: classic view with html/css/javascript"><cfargument 
	name="hide" type="string" required="no" hint="hide column or keys."><cfargument 
    name="keys" type="numeric" required="no" hint="For a structure, number of keys to display."><cfargument 
    name="label" type="string" required="no" hint="header for the dump output."><cfargument 
    name="metainfo" type="boolean" required="no" hint="Includes information about the query in the cfdump results."><cfargument 
    name="output" type="string" required="no" hint="Where to send the results:
- console: the result is written to the console (System.out).
- browser (default): the result is written the the browser response stream."><cfargument 
	name="show" type="string" required="no" hint="show column or keys."><cfargument 
    name="showUDFs" type="boolean" required="no" hint="show UDFs in cfdump output."><cfargument 
    name="top" type="numeric" required="no" hint="The number of rows to display."><cfargument 
    name="abort" type="boolean" required="no" hint="stops further processing of the request."><cfargument 
    name="eval" type="string" required="no" hint="name of the variable to display, also used as label, when no label defined."><!---

    ---><cfdump attributeCollection="#arguments#" contextlevel="3"><!---
---></cffunction>