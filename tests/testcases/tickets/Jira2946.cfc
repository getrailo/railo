<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="test">

		<cfset stImage = {}>
		<cfset stImage['action']     = "info">
		<cfset stImage['source']     = toBinary("R0lGODlhMQApAIAAAGZmZgAAACH5BAEAAAAALAAAAAAxACkAAAIshI+py+0Po5y02ouz3rz7D4biSJbmiabqyrbuC8fyTNf2jef6zvf+DwwKeQUAOw==")>
		<cfset stImage['structName'] = "getImageInfo">
		<cfimage attributecollection = "#stImage#"/>
	</cffunction>
</cfcomponent>