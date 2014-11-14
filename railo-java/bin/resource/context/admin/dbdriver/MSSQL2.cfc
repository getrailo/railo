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
 ---><cfcomponent extends="Driver" output="false" implements="IDriver">
	<cfset this.className="net.sourceforge.jtds.jdbc.Driver">
	<cfset this.dsn="jdbc:jtds:sqlserver://{host}:{port}/{database}">
		
	<cfset this.type.port=this.TYPE_FREE>
	<cfset this.value.host="localhost">
	<cfset this.value.port=1433>
	
	
	<cfset fields=array(
		field("Charset","charset","UTF-8",false,"(default - the character set the server was installed with) Very important setting, determines the byte value to character mapping for CHAR/VARCHAR/TEXT values. Applies for characters from the extended set (codes 128-255). For NCHAR/NVARCHAR/NTEXT values doesn't have any effect since these are stored using Unicode.")
	)>
	<cfset data=struct()>
	
    
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
		<cfif StructKeyExists(form,'custom_charset') and len(form.custom_charset) EQ 0>
        	<cfset StructDelete(form,'custom_charset',false)>
        </cfif>
	</cffunction>
    
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	<cffunction name="getClass" returntype="string" output="no"
		hint="return driver Java Class">
		<cfreturn this.className>
	</cffunction>
	
	<cffunction name="getDSN" returntype="string" output="no"
		hint="return DSN">
		<cfreturn this.dsn>
	</cffunction>
	
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "MSSQL - Microsoft SQL Server (Vendor jTDS)">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "Microsoft SQL Server Driver">
	</cffunction>

	<cffunction name="equals" returntype="string" output="no"
		hint="return if String class match this">
		<cfargument name="className" required="true">
		<cfargument name="dsn" required="true">
		<cfreturn this.className EQ arguments.className and findNoCase("sqlserver",arguments.dsn)>
	</cffunction>
	
</cfcomponent>