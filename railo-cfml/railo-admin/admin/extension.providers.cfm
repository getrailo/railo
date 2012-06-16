<!--- Action --->
<cfinclude template="extension.functions.cfm">

<cfset stVeritfyMessages=struct()>
<cfparam name="error" default="#struct(message:"",detail:"")#">
<cfparam name="form.mainAction" default="none">
<cfset error.message="">
<cftry>

	<cfswitch expression="#form.mainAction#">

        <cfcase value="#stText.Buttons.verify#">
        	<cfset data.urls=toArrayFromForm("url")>
        	<cfset data.rows=toArrayFromForm("row")>
            <cfloop from="1" to="#arrayLen(data.urls)#" index="idx">
            	<cfif arrayIndexExists(data.rows,idx)>
                	
                        
                    <cftry>
                        <cfadmin 
                            action="verifyExtensionProvider"
                            type="#request.adminType#"
                            password="#session["password"&request.adminType]#"
                            
                            url="#trim(data.urls[idx])#">
                            <cfset stVeritfyMessages["#data.urls[idx]#"].Label = "OK">
                        <cfcatch>
                            <cfset stVeritfyMessages["#data.urls[idx]#"].Label = "Error">
                            <cfset stVeritfyMessages["#data.urls[idx]#"].message = cfcatch.message>
                            <cfset stVeritfyMessages["#data.urls[idx]#"].detail = cfcatch.detail>
                            
                        </cfcatch>
                    </cftry> 
                </cfif>
            </cfloop>
            
		</cfcase>
        <cfcase value="#stText.Buttons.save#">
        	<cfset data.urls=toArrayFromForm("url")>
        	<cfset data.rows=toArrayFromForm("row")>
            
            <cfloop from="1" to="#arrayLen(data.urls)#" index="idx">
            	<cfif arrayIndexExists(data.rows,idx)>
                	<cfadmin 
                        action="updateExtensionProvider"
                        type="#request.adminType#"
                        password="#session["password"&request.adminType]#"
                        
                        url="#trim(data.urls[idx])#">
                </cfif>
            </cfloop>
		</cfcase>
        <cfcase value="#stText.Buttons.delete#">
        	<cfset data.urls=toArrayFromForm("url")>
        	<cfset data.rows=toArrayFromForm("row")>
            
            <cfloop from="1" to="#arrayLen(data.urls)#" index="idx">
            	<cfif arrayIndexExists(data.rows,idx)>
                	<cfadmin 
                        action="removeExtensionProvider"
                        type="#request.adminType#"
                        password="#session["password"&request.adminType]#"
                        
                        url="#trim(data.urls[idx])#">
                </cfif>
            </cfloop>
		</cfcase>
        <cfcase value="#stText.Buttons.install#">
            <cfif StructKeyExists(form,"row") and StructKeyExists(data,"ids") and ArrayIndexExists(data.ids,row)>
            	<cflocation url="#request.self#?action=#url.action#&action2=install1&provider=#data.hashProviders[row]#&app=#data.ids[row]#" addtoken="no">
            </cfif>
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>

<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq stText.Buttons.verify>
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>


<!--- 
Error Output --->
<cfset printError(error)>

<cfadmin 
	action="getExtensionProviders"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="providers">

<cfset hasAccess=true>


<cfset infos={}>
<cfloop query="providers">
		<cftry>
		<cfset provider=loadCFC(providers.url)>
    	<cfset infos[providers.url]=provider.getInfo()>
    	<cfcatch></cfcatch>
    </cftry>
</cfloop>



<!--- 
list all mappings and display necessary edit fields --->

<cfoutput>
<cfset stText.ext.prov.title="Title">
<cfset stText.ext.prov.mode="Mode">

<cfset doMode=false>
<cfloop query="providers"><cfif StructKeyExists(infos,providers.url) and StructKeyExists(infos[providers.url],"mode") and trim(infos[providers.url].mode) EQ "develop"><cfset doMode=true></cfif></cfloop>

<cfset columns=doMode?5:4>

<table class="tbl" width="100%">
 	<colgroup>
        <col width="10">
    </colgroup>
<tr>
	<td colspan="#columns#">#stText.ext.prov.IntroText#</td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
	<tr>
		<td><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"><cfmodule template="tp.cfm"  width="10" height="1"></td>
		<th scope="row">#stText.ext.prov.url#</th>
		<th scope="row">#stText.ext.prov.title#</th>
		<cfif doMode><th scope="row">#stText.ext.prov.mode#</th></cfif>
		<th scope="row">#stText.Settings.DBCheck#</th>
	</tr>
    
	<cfloop query="providers">
    <tr>
        <!--- checkbox ---->
        <td><table border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td><cfif not providers.isReadOnly><input type="checkbox" class="checkbox" name="row_#providers.currentrow#" value="#providers.currentrow#"></cfif></td>
        </tr>
        </table></td>
        
        <!--- url --->
        <td height="30" title="#providers.url#" nowrap>
            <input type="hidden" name="url_#providers.currentrow#" value="#providers.url#">#providers.url#
         </td>
         
         
         <cfset hasData= StructKeyExists(infos,providers.url)>
         
        <!--- title --->
        <td height="30" nowrap>
			<cfif hasData and StructKeyExists(infos[providers.url],"image")><cfset dn=getDumpNail(infos[providers.url].image,100,30)><cfif len(dn)><img src="#dn#" border="0"/>&nbsp;&nbsp;</cfif></cfif>
			<cfif hasData and StructKeyExists(infos[providers.url],"title") and len(trim(infos[providers.url].title))>#infos[providers.url].title#<cfelse>&nbsp;</cfif></td>
        <!--- mode --->
        <cfif doMode><td height="30" nowrap><cfif hasData><cfif StructKeyExists(infos[providers.url],"mode") and len(trim(infos[providers.url].mode))>#infos[providers.url].mode#<cfelse>production</cfif></cfif></td></cfif>
        <!--- check --->
        <td nowrap valign="middle" align="center">
            <cfif StructKeyExists(stVeritfyMessages, providers.url)>
                #stVeritfyMessages[providers.url].label#
                <cfif stVeritfyMessages[providers.url].label neq "OK">
                    &nbsp;<img src="resources/img/red-info.gif.cfm" 
                        width="9" 
                        height="9" 
                        border="0" 
                        title="#stVeritfyMessages[providers.url].message##Chr(13)#"
                        alt="#stVeritfyMessages[providers.url].message##Chr(13)#">
                </cfif>
            <cfelse>
                &nbsp;				
            </cfif>
        </td>
        
    </tr>
    </cfloop>
    
    
    
<cfif hasAccess>
	<tr>
		<td><table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row_#providers.recordcount+1#" value="#providers.recordcount+1#"></td>
		</tr>
		</table></td>
		
		<td nowrap colspan="#columns-1#"><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="url_#providers.recordcount+1#" value="" required="no"  style="width:100%">
            <br /><div class="comment">&nbsp;&nbsp;#stText.ext.prov.urlDesc#</div></td>
	</tr>
</cfif>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="8" line=true>
	<tr>
		<td colspan="#columns#">
		 <table border="0" cellpadding="0" cellspacing="0">
		 <tr>
			<td><cfmodule template="tp.cfm"  width="8" height="1"></td>		
			<td><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="10"></td>
			<td></td>
		 </tr>
		 <tr>
			<td></td>
			<td valign="top"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="14"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="54" height="1"></td>
			<td>&nbsp;
			<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.save#">
			<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.verify#">
			<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Delete#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfif>
</cfform>
</cfoutput>
</table>