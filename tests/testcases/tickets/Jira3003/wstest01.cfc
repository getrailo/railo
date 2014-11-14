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
 ---><!---
==================================================================
WSTEST01.cfc 
==================================================================
--->
<cfcomponent displayname="wstest01"              
             namespace = "http://beans.webservices.cfc.netmover"
             serviceportname = "wstest01Service" 
             porttypename = "wstest01" 
             bindingname = "wstest01Binding">
<!---
==================================================================
function: run()
==================================================================
--->
<cffunction name="run" returnType="string" access="remote">
<cfargument name="reqParams" type="wstest01Request" required="true">
  <cfset var resp="RUN OK">
  <!--- test --->
  <cfreturn resp>
</cffunction>

</cfcomponent>