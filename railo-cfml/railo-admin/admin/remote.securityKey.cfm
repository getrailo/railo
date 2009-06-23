<cfif structKeyExists(form,'mainAction')>
	<cfset error.message="">
    <cfset error.detail="">
    <!--- actions --->
    <cftry>
        <cfif form.mainAction EQ stText.Buttons.reset>
        	<cfadmin 
                action="resetId"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#">
				<!--- remoteClients="#request.getRemoteClients()#" --->
        </cfif>
    
        <cfcatch>
            <cfset error.message=cfcatch.message>
            <cfset error.detail=cfcatch.Detail>
        </cfcatch>
    </cftry>
    
	<!--- redirect --->
    <cfif cgi.request_method EQ "POST" and error.message EQ "">
        <cflocation url="#request.self#?action=#url.action#" addtoken="no">
    </cfif>
    
    <!--- error ---->
    <cfset printError(error)>
</cfif>





<cfoutput>
# sttext.remote.securityKeyTitleDesc#

<br /><br /><br /><br />
<center><h2 style="width:350px;padding:10px;background-color:white;border-color:##595F73;border-style:solid;border-width:1px;" align="center">
	#getRailoId()[request.adminType].securityKey#
</h2>

<cfform action="#request.self#?action=#url.action#" method="post">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.reset#">
</cfform>

</center>
</cfoutput>


