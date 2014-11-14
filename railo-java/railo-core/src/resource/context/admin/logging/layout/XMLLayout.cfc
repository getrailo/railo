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
 ---><cfcomponent extends="Layout">
	
    <cfset fields=array(
		field("Location Info","locationinfo","no",true,"If it is set to ""no"" this means there will be no location information output by this layout. If the the option is set to ""yes"", then the file name and line number of the statement at the origin of the log statement will be output.","radio","yes,no")
		,field("Properties","properties","no",true,"Sets whether MDC key-value pairs should be output.","radio","yes,no")
		
		)>
    
	<cffunction name="getClass" returntype="string" output="false">
    	<cfreturn "org.apache.log4j.xml.XMLLayout">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="false">
    	<cfreturn "XML">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfreturn "The output of the XML Layout consists of a series of log4j:event elements as defined in the log4j.dtd. It does not output a complete well-formed XML file. The output is designed to be included as an external entity in a separate file to form a correct XML file.">
    </cffunction>
    
</cfcomponent>