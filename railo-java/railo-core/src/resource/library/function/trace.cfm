<cffunction name="trace" returntype="void" hint="Displays and logs debugging data about the state of an application at the time this function executes. 
Tracks runtime logic flow, variable values, and execution time. Displays output at the end of the request or in the debugging section at the end of the request."><!---
	---><cfargument name="var" type="string" required="no" hint="The name of a simple or complex variable to display. Useful for displaying a temporary value, or a value that does not display on any CFM page."><!---
	---><cfargument name="text" type="string" required="no" hint="string, which can include simple variable, but not complex variables such as arrays."><!---
	---><cfargument name="type" type="string" required="no" default="Information"  hint="Corresponds to the cflog type attribute:
- Information
- Warning
- Error
- Fatal Information"><!---
	---><cfargument name="category" type="string" required="no"  hint="string name for identifying trace groups"><!---
	---><cfargument name="inline" type="boolean" required="no" default="#false#" hint="if true displays trace code in line on the page in the location of the trace function, 
addition to the debugging information output."><!---
	---><cfargument name="abort" type="boolean" required="no" default="#false#" hint="stops further processing of the request."><!---
	---><cftrace attributeCollection="#arguments#"><!---
	---></cffunction>