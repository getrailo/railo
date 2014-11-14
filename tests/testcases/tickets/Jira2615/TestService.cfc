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
 ---><cfcomponent output="false">
	<!--- 
	<cffunction name="myFunction" access="remote" returntype="void" output="false" >
		<cfargument name="myArray" type="MyItem[]" required="true" />
	</cffunction>
--->
	
	<cffunction name="setStruct" access="remote" returntype="void" output="false" >
		<cfargument name="argStruct" type="struct" required="true" />
	</cffunction>

	<cffunction name="getStruct" access="remote" returntype="struct" output="false">
		<cfreturn {a:1}>
	</cffunction>

	<cffunction name="echoStruct" access="remote" returntype="struct" output="false">
		<cfargument name="argStruct" type="struct" required="true" />
		<cfreturn arguments.argStruct>
	</cffunction>



	<cffunction name="setMyItem" access="remote" returntype="void" output="false" >
		<cfargument name="myitem" type="MyItem" required="true" />
	</cffunction>

	<cffunction name="getMyItem" access="remote" returntype="MyItem" output="false" >
		<cfreturn new MyItem("from:getMyItem")>
	</cffunction>
	
	<cffunction name="getMyItemArray" access="remote" returntype="MyItem[]" output="false" >
		<cfreturn [new MyItem("1:getMyItemArray"),new MyItem("2:getMyItemArray")]>
	</cffunction>

	<cffunction name="getArray" access="remote" returntype="Array" output="false" >
		<cfreturn [new MyItem("1:getArray"),new MyItem("2:getArray")]>
	</cffunction>
	


	<cffunction name="getAnyArray" access="remote" returntype="any" output="false" >
		<cfreturn [new MyItem("1:getAnyArray"),new MyItem("2:getAnyArray")]>
	</cffunction>

	<cffunction name="getAny" access="remote" returntype="any" output="false" >
		<cfreturn new MyItem("getAny")>
	</cffunction>
	
</cfcomponent>