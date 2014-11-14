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
 ---><cffunction name="location" output="no"  returntype="void" hint="Stops execution of the current request and rdirect to a other location.">
	<cfargument name="url" type="string" required="yes" hint="URL where the call should redirect">
	<cfargument name="addToken" type="boolean" required="no" default="#true#" hint="appends client variable information to URL (true|false)">
	<cfargument name="statusCode" type="numeric" required="no" default="#302#" hint="The HTTP status code (301,302(default), 303, 304, 305, 307)">
	<cflocation attributeCollection="#arguments#">
</cffunction>