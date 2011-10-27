
<cfset error.message="">
<cfset error.detail="">
<cfset hasAccess=true>

<cfif request.admintype EQ "web">
	<cflocation url="#request.self#" addtoken="no">
</cfif>



<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">

<cfif StructKeyExists(form,"host")>
	<cfset session.certHost=form.host>
	<cfset session.certPort=form.port>
</cfif>

<cfparam name="session.certHost" default="">
<cfparam name="session.certPort" default="443">
<cfset _host=session.certHost>
<cfset _port=session.certPort>



<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
    
		<cfcase value="#stText.services.certificate.install#">
			<cfadmin 
                type="#request.adminType#"
				password="#session["password"&request.adminType]#"
                action="updatesslcertificate" host="#form.host#" port="#form.port#">
			
		
		</cfcase>	
    </cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>
<!--- 
Create Datasource --->
<cfoutput>


<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>



<table class="tbl" width="740">
<colgroup>
    <col width="150">
    <col width="590">
</colgroup>
<tr>
	<td colspan="2">
#stText.services.certificate.desc#
	</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">

<tr>
	<td class="tblHead" width="150">#stText.services.certificate.host#</td>
	<td class="tblContent">
    	<cfinput type="text" name="host" value="#_host#" style="width:200px" required="yes"><br />
		<span class="comment">#stText.services.certificate.hostDesc#</span><br>
		
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.services.certificate.port#</td>
	<td class="tblContent">
    	<cfinput type="text" name="port" value="#_port#" style="width:40px" required="yes" validate="integer"><br />
		<span class="comment">#stText.services.certificate.portDesc#</span><br>
		
	</td>
</tr>


<tr>
	<td colspan="2">
		<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.services.certificate.list#">
		<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.services.certificate.install#">
		<input class="submit" type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>

</cfform>
</table>
<cfif len(_host) and len(_port)><br><br>

<cftry>
<cfadmin 
                type="#request.adminType#"
				password="#session["password"&request.adminType]#"
	action="getsslcertificate" host="#_host#"port="#_port#" returnvariable="qry">



<h2>#replace(stText.services.certificate.result,'{host}',_host)#</h2>
<cfif qry.recordcount>
<table class="tbl" width="740">
<tr>
	<td class="tblHead">#stText.services.certificate.subject#</td>
	<td class="tblHead">#stText.services.certificate.issuer#</td>
</tr>
<cfloop query="qry">
<tr>
	<td class="tblContent">#qry.subject#</td>
	<td class="tblContent">#qry.issuer#</td>
</tr>
</cfloop>
</table>
<cfelse>
	<br /><p class="CheckError">#stText.services.certificate.noCert#</p>

</cfif>
	<cfcatch>
    	<br /><p class="CheckError">#cfcatch.message#</p>
    </cfcatch>
</cftry>


</cfif>


</cfoutput>
