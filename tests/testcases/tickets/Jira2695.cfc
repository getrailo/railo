<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDotNotationUpperCase">
		<cfprocessingdirective dotnotationUpperCase="false">
		<cfset local.test.susiSorglos="Susi Sorglos">
		
		<cfset assertEquals(true,compare(StructKeyList(test),'susiSorglos')==0)> 
	</cffunction>
</cfcomponent>