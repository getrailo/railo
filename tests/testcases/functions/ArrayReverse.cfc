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
 ---><cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayReverse" localMode="modern">
 
<!--- begin old test code --->
<cfif server.ColdFusion.ProductName EQ "railo">
<cfset arr1=arrayNew(1)>
<cfset ArrayAppend( arr1, 1 )>
<cfset ArrayAppend( arr1, 2 )>
<cfset ArrayAppend( arr1, 3 )>
<cfset arr1[6]="6">

<cfset arr=arrayReverse(arr1)>
<cf_valueEquals left="#arrayLen(arr)#" startWith="6">

<cf_valueEquals left="#arr[1]#" startWith="6">
<cf_valueEquals left="#arr[4]#" startWith="3">
<cf_valueEquals left="#arr[5]#" startWith="2">
<cf_valueEquals left="#arr[6]#" startWith="1">

</cfif>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>