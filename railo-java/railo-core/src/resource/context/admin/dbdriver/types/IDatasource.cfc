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
<cfinterface extends="types.IDriver">


	<cffunction name="equals" returntype="boolean" output="false"
		hint="returns true if a passed class uses the same driver as this one">

		<cfargument name="className" required="true">
		<cfargument name="dsn" required="true">
		
	</cffunction>
	
	<cffunction name="getClass" returntype="string" output="no" 
		hint="returns the Java driver Class">

	</cffunction>
	
	<!--- TODO: rename getDSN to getConnectionString !--->
	<cffunction name="getDSN" returntype="string" output="no" 
		hint="returns the Connection String of the datasource">
		
	</cffunction>


</cfinterface>