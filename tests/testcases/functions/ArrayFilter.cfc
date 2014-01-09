<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayFilter">
		<cfscript>
// UDF

var arr=["hello","world"];
var arr2=ArrayFilter(arr,helloFilter);

valueEquals(arrayToList(arr),'hello,world');
valueEquals(arrayToList(arr2),'hello');


// closure 
var clo=function (arg1){
	return FindNoCase("hello",arg1);
};
arr2=ArrayFilter(arr,clo);
valueEquals(arrayToList(arr),'hello,world');
valueEquals(arrayToList(arr2),'hello');


// string filter (not supported by ACF)
/*if(server.ColdFusion.ProductName EQ "Railo") {
	arr2=ArrayFilter(arr,"he*");
	valueEquals(arrayToList(arr),'hello,world');
	valueEquals(arrayToList(arr2),'hello');
}*/
</cfscript>
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	<cfscript>
	private boolean function helloFilter(arg1){
		return FindNoCase("hello",arg1);
	}
	</cfscript>
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>