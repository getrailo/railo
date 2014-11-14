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
 ---><cfcomponent displayname="Application" output="true" hint="Handle the application.">
	<cfset THIS.Name = "Test" />
	<cfset THIS.ApplicationTimeout = CreateTimeSpan( 0, 0, 1, 0 ) />
	<cfset THIS.SessionManagement = true />
	<cfset THIS.SetClientCookies = true />
	<!--- ORM Stuff --->
	<cfset this.ormenabled = "true"> 
	<cfset this.datasource = "mysql">
	<cfset this.ormsettings = {} />
	<cfset this.ormsettings.cfclocation = "orm" />
	<cfset this.ormsettings.dbcreate = "update" />
	<cfset this.ormsettings.savemapping = true />
	 
	<cffunction name="OnApplicationStart" access="public" returntype="boolean" output="false" hint="Fires when the application is first created.">
		<cfreturn true />
	</cffunction>
	 
	 
	<cffunction name="OnSessionStart" access="public" returntype="void" output="false" hint="Fires when the session is first created.">
		<cfreturn />
	</cffunction>
	 
	 
	<cffunction name="OnRequestStart" access="public" returntype="boolean" output="false" hint="Fires at first part of page processing.">
		<cfargument name="TargetPage" type="string" required="true" />
		<cfreturn true />
	</cffunction>
	 
	 
	<cffunction name="OnRequest" access="public" returntype="void" output="true" hint="Fires after pre page processing is complete.">
		<cfargument name="TargetPage" type="string" required="true" />
		<cfinclude template="#ARGUMENTS.TargetPage#" />
		<cfreturn />
	</cffunction>
	 
	 
	<cffunction name="OnRequestEnd" access="public" returntype="void" output="true" hint="Fires after the page processing is complete.">
	 
		<cfreturn />
	</cffunction>
	 
	 
	<cffunction name="OnSessionEnd" access="public" returntype="void" output="false" hint="Fires when the session is terminated.">
		<cfargument	name="SessionScope"	type="struct" required="true" />
		<cfargument name="ApplicationScope" type="struct" required="false" default="#StructNew()#" />
	 
		<cfreturn />
	</cffunction>
	 
	 
	<cffunction name="OnApplicationEnd" access="public" returntype="void" output="false" hint="Fires when the application is terminated.">
		<cfargument name="ApplicationScope" type="struct" required="false" default="#StructNew()#" />
	 
		<cfreturn />
	</cffunction>
</cfcomponent>