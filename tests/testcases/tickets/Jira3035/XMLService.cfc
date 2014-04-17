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