<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="test">
		
		<cfset foo = function(){return true;}> <!---fine--->
		<cfset foo = function(){return true;} > <!--- not fine --->
		<cfset foo = function(){return true;} /><!--- not fine --->
		<cfset foo = function(){return true;}/> <!--- fine--->
		
		<cfset foo = /* hello world*/ function(){return true;} /* hello world*/ > <!--- not fine --->
	</cffunction>
</cfcomponent>