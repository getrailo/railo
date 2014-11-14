/**
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
 **/
<cfinterface>

	
	<cffunction name="getName" returntype="string"  output="no"
		hint="returns display name of the driver">
		
	</cffunction>
	
	<cffunction name="getDescription" returntype="string"  output="no"
		hint="returns description for the driver">
		
	</cffunction>

	
	<!---
	<cffunction name="equals" returntype="string" output="no" 
		hint="return if String class match this">
		<cfargument name="className" required="true">
		<cfargument name="dsn" required="true">
	</cffunction>
	
	<cffunction name="getType" returntype="numeric" output="no">
		<cfargument name="key" required="true" type="string">
	</cffunction>
	
	<cffunction name="getValue" returntype="string" output="no">
		<cfargument name="key" required="true" type="string">
	</cffunction>
	
	<cffunction name="getClass" returntype="string" output="no" 
		hint="return driver Java Class">
	</cffunction>
	
	<cffunction name="getDSN" returntype="string"  output="no"
		hint="return DSN">
	</cffunction>
	
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
	</cffunction>
	
	<cffunction name="onBeforeError" returntype="void" output="no">
		<cfargument name="cfcatch" required="true" type="struct">
	</cffunction>
	
	<cffunction name="init" returntype="void" output="no">
		<cfargument name="data" required="yes" type="struct">
	</cffunction>
	--->
</cfinterface>