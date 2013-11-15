<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayEach">
		<cfscript>
// UDF
var arr=["hello","world"];
request.test=[];

ArrayEach(arr,eachFilter);
valueEquals(arrayToList(request.test),'hello,world');

// Closure
arr=["hello","world"];
request.test=[];
eachFilter=function (arg1){
	arrayAppend(request.test,arg1);
};
ArrayEach(arr,eachFilter);
valueEquals(arrayToList(request.test),'hello,world');


</cfscript>
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cfscript>
	private function eachFilter(arg1){
		arrayAppend(request.test,arg1);
	}
	</cfscript>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>