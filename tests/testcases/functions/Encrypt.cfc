<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testEncrypt" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(
	left="#trim(decrypt(encrypt("hallo welt","stringkey"),"stringkey"))#" 
	,right="hallo welt")>
	

	<cfset key=generateSecretKey("AES")>
<cfset valueEquals(
		left="#trim(decrypt(encrypt("hallo welt",key,"AES"),key,"AES"))#" 
		,right="hallo welt")>

	<cfset key=generateSecretKey("BLOWFISH")>
<cfset valueEquals(
		left="#trim(decrypt(encrypt("hallo welt",key,"BLOWFISH"),key,"BLOWFISH"))#" 
		,right="hallo welt")>

	<cfset key=generateSecretKey("DES")>
<cfset valueEquals(
		left="#trim(decrypt(encrypt("hallo welt",key,"DES"),key,"DES"))#" 
		,right="hallo welt")>

	<cfset key=generateSecretKey("DESEDE")>
<cfset valueEquals(
		left="#trim(decrypt(encrypt("hallo welt",key,"DESEDE"),key,"DESEDE"))#" 
		,right="hallo welt")>
	
	<cftry>
	<cfset key="susi">
<cfset valueEquals(
		left="#trim(decrypt(encrypt("hallo welt",key,"AES"),key,"AES"))#" 
		,right="hallo welt")>
		<cfset fail("must throw:The key specified is not a valid key for this encryption: Invalid AES key length: 24.")>
		<cfcatch></cfcatch>
	</cftry>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>