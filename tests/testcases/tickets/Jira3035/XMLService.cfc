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
<CFCOMPONENT output="false">
	
	<CFFUNCTION name="returnXMLWithoutAbort" 
		hint="" 
		access="remote" 
		output="false" 
		returntype="xml">

		<CFSET sXML = '<?xml version="1.0" encoding="UTF-8"?>
<Root>
	<anelement>With some content</anelement>
</Root>'>
		
		<CFCONTENT type="application/xml; charset=UTF-8">
		<CFRETURN sXML />
		
	</CFFUNCTION>


	<CFFUNCTION name="returnXMLWithAbort" 
		hint="" 
		access="remote" 
		output="true" 
		returntype="xml">

		<CFSET sXML = '<?xml version="1.0" encoding="UTF-8"?>
<Root>
	<anelement>With some content</anelement>
</Root>'>
		
		<CFCONTENT type="application/xml; charset=UTF-8" reset="true">#sXML#<cfabort>


		<CFRETURN sXML />
		
	</CFFUNCTION>


	<CFFUNCTION name="returnXMLWithReturnFormat" 
		hint="" 
		access="remote" 
		output="true" 
		returntype="xml" returnformat="json">

		<CFSET sXML = '<?xml version="1.0" encoding="UTF-8"?>
<Root>
	<anelement>With some content</anelement>
</Root>'>
		
		<CFCONTENT type="application/xml; charset=UTF-8">


		<CFRETURN sXML />
		
	</CFFUNCTION>

	
</CFCOMPONENT>