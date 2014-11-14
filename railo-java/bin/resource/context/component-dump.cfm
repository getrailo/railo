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
 ---><cfsetting enablecfoutputonly="yes">
<cfif arrayLen(getTemplatePath()) EQ 2>
	<!--- dump the component --->
	<cfdump var="#component#">
<cfelse><!---
	
	write out functions of component. done for cfmx compatibility, when a cfc is included via cfinclide the functions are available for use
---><cfscript>
 	
	trace=getTemplatePath();
	abs=trace[arrayLen(trace)-1];
	real=contractPath(abs);
	real=listTrim(real,'/');
	real=mid(real,1,len(real)-4);
	cfc=createObject('component',real);
</cfscript><cfloop collection="#cfc#" item="key"><cfset variables[key]=cfc[key]></cfloop><cfscript>
 	
	StructDelete(variables,'trace',false);
	StructDelete(variables,'abs',false);
	StructDelete(variables,'real',false);
	StructDelete(variables,'cfc',false);
	
</cfscript></cfif>