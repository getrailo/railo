<cfdirectory directory="../factory/img" action="list" name="imgs">
<cfset bins=struct()>
<cfloop query="imgs">
	<cfif findnoCase(".gif",imgs.name) or findnoCase(".swf",imgs.name) >
		<cffile action="readbinary" file="../factory/img/#imgs.name#" variable="data">
		
		
	<cfoutput><cfsavecontent variable="content">
		#chr(60)#cfapplication name="webadmin" 
		sessionmanagement="yes" 
		clientmanagement="no" 
		setclientcookies="yes" 
		setdomaincookies="no">#chr(60)#cfsetting showdebugoutput="no">#chr(60)#cfcontent type="image/gif" content="###serialize(data)###">
	</cfsavecontent></cfoutput>
	<cfdump eval="expandPath('../resources/img/#imgs.name#.cfm')">
		<cffile action="write" addnewline="no" file="../resources/img/#imgs.name#.cfm" output="#content#" fixnewline="no">
	</cfif>
</cfloop>


<!---


<cfif  not fileExists(expandPath("resources/img/#attributes.src#.cfm")) and fileExists(expandPath("resources/img/#attributes.src#"))>
	
	<cffile action="readbinary" file="#expandPath("resources/img/#attributes.src#")#" variable="data">
	<cfoutput><cfsavecontent variable="content">
		#chr(60)#cfapplication name="webadmin" 
		sessionmanagement="yes" 
		clientmanagement="no" 
		setclientcookies="yes" 
		setdomaincookies="no">
	#chr(60)#cfsetting showdebugoutput="no">
		#chr(60)#cfcontent type="image/gif" content="#serialize(data)#">
	</cfsavecontent></cfoutput>
	
	<cffile action="write" addnewline="no" file="#expandPath("resources/img/#attributes.src#")#.cfm" output="#content#" fixnewline="no">
</cfif>
--->














