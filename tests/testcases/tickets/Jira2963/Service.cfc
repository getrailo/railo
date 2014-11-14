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
 ---><cfcomponent output="false" hint="The web service for the bug 40537 test.">

	<cffunction name="send" access="remote" returntype="void"
			output="false"
			hint="A function that accepts a typed array as an argument.">
		<cfargument name="myArray" type="MyItem" required="true"
				hint="An array of MyItem objects." />
		
	</cffunction>
	<cffunction name="sendArray" access="remote" returntype="void"
			output="false"
			hint="A function that accepts a typed array as an argument.">
		<cfargument name="myArray" type="MyItem[]" required="true"
				hint="An array of MyItem objects." />
		
	</cffunction>
	<cffunction name="sendArrayArray" access="remote" returntype="void"
			output="false"
			hint="A function that accepts a typed array as an argument.">
		<cfargument name="myArray" type="MyItem[][]" required="true"
				hint="An array of MyItem objects." />
		
	</cffunction>

	<cffunction name="echoArray" access="remote" returntype="MyItem"
			output="false"
			hint="A function that accepts a typed array as an argument.">
		<cfargument name="myArray" type="MyItem[]" required="true"
				hint="An array of MyItem objects." />
		<cfreturn new MyItem() />
	</cffunction>
	
	<cffunction name="takeStructAsType" access="remote" returntype="MyItem">
		<cfargument name="myItem" type="MyItem" required="true" />
		<cfreturn arguments.myItem />
	</cffunction>

</cfcomponent>
