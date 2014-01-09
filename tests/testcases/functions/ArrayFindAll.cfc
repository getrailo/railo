<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayFindAll">
		<cfscript>
var arr=["aaa","bb","aaa","ccc","AAA"];
res=arrayFindAll(arr,"aaa");
valueEquals(arraytoList(res),'1,3');
res=arrayFindAll(arr,"a");
valueEquals(arraytoList(res),'');


arr=["hello","world","susi","world"];

// UDF
res=arrayFindAll(arr,doFind);
valueEquals(arrayToList(res),"2,4");

// Closure
doFind=function (value){
	return value EQ "world";
};
res=arrayFindAll(arr,doFind);
valueEquals(arrayToList(res),"2,4");

</cfscript>
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	<cfscript>
	private function doFind(value){
		return value EQ "world";
	}
	
	</cfscript>
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>