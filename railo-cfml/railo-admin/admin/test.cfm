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
 ---><cffunction name="out" output="no" returntype="string">
	<cfargument name="namespace">
    <cfargument name="sct">
    
	<cfset var keys=StructKeyArray(sct)>
    <cfset ArraySort(keys,'textNocase')>
    <cfset var el="">
    <cfset var str="">
    
    <cfloop array="#keys#" index="key">
    	<cfset el=sct[key]>
        <cfif isStruct(el)>
        	<cfset str&=out(namespace&key& ".",el)>
        <cfelseif isArray(el)>
        	<xcfset out(namespace&key& ".",el)>
        <cfelseif isSimpleValue(el)>
            <cfset str&='	<custom key="#lCase(namespace)##lCase(key)#">#HTMLEditFormat(el)#</custom>
'>
		</cfif>
    </cfloop>
    <cfreturn str>
    
</cffunction>
<cfoutput>
<language key="#lCase(session.railo_admin_lang)#">
#out('',stText)#
</language>
</cfoutput>




