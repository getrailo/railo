<!--- 
Defaults --->
<cfset error.message="">
<cfset error.detail="">
<cfparam name="form.mainAction" default="none">

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Update#">
			<cfset data.label=toArrayFromForm("label")>
			<cfset data.hash=toArrayFromForm("hash")>
            
			<cfloop index="idx" from="1" to="#arrayLen(data.label)#">
				<cfif len(trim(data.label[idx]))>
                	<cfadmin 
                    action="updateLabel"
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    
                    label="#data.label[idx]#"
                    hash="#data.hash[idx]#">
                 </cfif>
            </cfloop>
		</cfcase>
	</cfswitch>
	<cfcatch>
	
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>

<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction NEQ "none">
	<cflocation url="#request.self#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>





<cfset pool["Par Eden Space"]="The pool from which memory is initially allocated for most objects.">
<cfset pool["Par Survivor Space"]="The pool containing objects that have survived the garbage collection of the Eden space.">
<cfset pool["CMS Old Gen"]="The pool containing objects that have existed for some time in the survivor space.">
<cfset pool["CMS Perm Gen"]="The pool containing all the reflective data of the virtual machine itself, such as class and method objects.">
<cfset pool["Code Cache"]="The HotSpot Java VM also includes a code cache, containing memory that is used for compilation and storage of native code.">


<cfset pool["Eden Space"]=pool["Par Eden Space"]>
<cfset pool["PS Eden Space"]=pool["Par Eden Space"]>

<cfset pool["Survivor Space"]=pool["Par Survivor Space"]>
<cfset pool["PS Survivor Space"]=pool["Par Survivor Space"]>

<cfset pool["Perm Gen"]=pool["CMS Perm Gen"]>

<cfset pool["Tenured Gen"]=pool["CMS Old Gen"]>
<cfset pool["PS Old Gen"]=pool["CMS Old Gen"]>


<cffunction name="printMemory" output="yes">
	<cfargument name="usage" type="query" required="yes">
	
    <cfset height=6>
    <cfset width=100>
       	<table cellpadding="0" cellspacing="0">
        <cfloop query="usage">
        	<cfset _used=int(width/usage.max*usage.used)>
        	<cfset _free=width-_used> 
            
			<cfset pused=int(100/usage.max*usage.used)>
        	<cfset pfree=100-pused> 
            
            
            
        	<tr>  
   				<td>
            	<table class="tbl" height="#height#" width="#width#">
                
                <tr>
                	<td colspan="2"><cfmodule template="tp.cfm" height="1" width="#width#" /></td>
                </tr>
                <tr>
                    <td  colspan="2"><b>#usage.name#</b><cfif StructKeyExists(pool,usage.name)><br /><span class="comment">#pool[usage.name]#</span></cfif></td>
                </tr>
                <tr>
                    <td class="tblHead" style="background-color:##eee2d4" height="#height#" width="#_used#" title="#int(usage.used/1024)#kb (#pused#%)"><cfmodule template="tp.cfm" height="#height#" width="#_used#" /></td>
                    <td class="tblContent" style="background-color:##d6eed4" height="#height#" width="#_free#" title="#int((usage.max-usage.used)/1024)#kb (#pfree#%)"><cfmodule template="tp.cfm" height="#height#" width="#_free#" /></td>
                </tr>
                </table>
                </td>
             </tr>
    	</cfloop>
        </table>
</cffunction>

<cfset total=query(
	name:["Total"],
	type:[""],
	used:[server.java.totalMemory-server.java.freeMemory],
	max:[server.java.totalMemory],
	init:[0]
)>




















<cfoutput>






<div style="width:740px">
#stText.Overview.introdesc[request.adminType]#
</div>
<br />



<table class="tbl" width="740">
<tr>
<td valign="top">
<table class="tbl" width="300">
<tr>
	<td colspan="2"><h2>#stText.Overview.Info#</h2></td>
</tr>

    <cfadmin 
        action="getInfo"
        type="#request.adminType#"
        password="#session["password"&request.adminType]#"
        returnVariable="info">
<cfif request.adminType EQ "web">
<tr>
	<td class="tblHead" width="150">#stText.Overview.label#</td>
	<td class="tblContent">#info.label#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.hash#</td>
	<td class="tblContent">#info.hash#</td>
</tr>
</cfif>


<tr>
	<td class="tblHead" width="150">#stText.Overview.Version#</td>
	<td class="tblContent">Railo #server.railo.version# #server.railo.state#</td>
</tr>
<cfif StructKeyExists(server.railo,'versionName')>
<tr>
	<td class="tblHead" width="150">#stText.Overview.VersionName#</td>
	<td class="tblContent"><a href="#server.railo.versionNameExplanation#" target="_blank">#server.railo.versionName#</a></td>
</tr>
</cfif>
<tr>
	<td class="tblHead" width="150">#stText.Overview.ReleaseDate#</td>
	<td class="tblContent">#lsDateFormat(server.railo['release-date'])#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.CFCompatibility#</td>
	<td class="tblContent">#replace(server.ColdFusion.ProductVersion,',','.','all')#</td>
</tr>



<tr>
	<td width="150" colspan="2">&nbsp;</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.config#</td>
	<td class="tblContent">#info.config#</td>
</tr>


<cfif request.adminType EQ "web">
<tr>
	<td class="tblHead" width="150">#stText.Overview.webroot#</td>
	<td class="tblContent">#info.root#</td>
</tr>

<cfadmin 
	action="getTLDs"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="tlds">
<cfadmin 
	action="getFLDs"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="flds">

<cfif isQuery(tlds)>
	<cfset tlds=listToArray(valueList(tlds.displayname))>
</cfif>
<cfif isQuery(flds)>
	<cfset flds=listToArray(valueList(flds.displayname))>
</cfif>
</cfif>

<tr>
	<td class="tblHead" width="150">#stText.Overview.OS#</td>
	<td class="tblContent">#server.OS.Name# (#server.OS.Version#)<cfif structKeyExists(server.os,"archModel")> #server.os.archModel#bit</cfif></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.remote_addr#</td>
	<td class="tblContent">#cgi.remote_addr#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.server_name#</td>
	<td class="tblContent">#cgi.server_name#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.overview.servletContainer#</td>
	<td class="tblContent">#server.servlet.name#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.overview.railoID#</td>
	<td class="tblContent">#getRailoId().server.id#</td>
</tr>
<cfif request.adminType EQ "web">
<tr>
	<td class="tblHead" width="150">#stText.Overview.InstalledTLs#</td>
	<td class="tblContent">
		<cfloop index="idx" from="1" to="#arrayLen(tlds)#">
			- #tlds[idx]# <!--- ( #iif(tlds[idx].type EQ "cfml",de('railo'),de('jsp'))# ) ---><br>
		</cfloop>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.InstalledFLs#</td>
	<td class="tblContent">
		<cfloop index="idx" from="1" to="#arrayLen(flds)#">
			- #flds[idx]#<br>
		</cfloop>
	</td>
</tr>

<tr>
	<td class="tblHead" width="150">#stText.Overview.DateTime#</td>
	<td class="tblContent">
		#lsdateFormat(now())#
		#lstimeFormat(now())#
	</td> 
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.ServerTime#</td>
	<td class="tblContent">
		
		#lsdateFormat(date:now(),timezone:"jvm")#
		#lstimeFormat(time:now(),timezone:"jvm")#
	</td> 
</tr></cfif>
<tr>
	<td class="tblHead" width="150">Java</td>
	<td class="tblContent">
		<!--- <cfset serverNow=createObject('java','java.util.Date')> --->
		#server.java.version# (#server.java.vendor#)<cfif structKeyExists(server.java,"archModel")> #server.java.archModel#bit</cfif>
	</td> 
</tr>
<tr>
	<td class="tblHead" width="150">Classpath</td>
	<td class="tblContent">
    	
	<div class="tblContent" style="font-family:Courier New;font-size : 7pt;overflow:auto;width:400;height:100px;border-style:solid;border-width:1px;padding:0px">
    <cfset arr=getClasspath()>
    <cfloop from="1" to="#arrayLen(arr)#" index="line">
    <span style="background-color:###line mod 2?'d2e0ee':'ffffff'#;display:block;padding:1px 5px 1px 5px ;">#arr[line]#</span>
    </cfloop>
   </div>
	</td> 
</tr>
</table>


</td>
<td valign="top">
<cftry>
<cfsavecontent variable="memoryInfo">

<table class="tbl">

<tr>
	<td><h2>Memory Usage</h2></td>
</tr>
<tr>
	<td class="tblHead" width="150">Heap</td>
</tr>
<tr>
	<td class="tblContent">
        <cfset printMemory(getmemoryUsage("heap"))>
    </td>
</tr>
<tr>
	<td class="tblHead" width="150">Non-Heap</td>
</tr>
<tr>
	<td class="tblContent">
        <cfset printMemory(getmemoryUsage("non_heap"))>
    </td>
</tr>
</cfsavecontent>
#memoryInfo#
<cfcatch></cfcatch>
</cftry>
</table>

</td>
</tr>
</table>



<br><br>


<cfif request.admintype EQ "server">
<cfadmin 
	action="getContexts"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="rst">

<table class="tbl" width="740">

<tr>
	<td colspan="4"><h2>#stText.Overview.contexts.title#</h2></td>
</tr>
<tr>
	<td class="tblHead" width="100">#stText.Overview.contexts.label#</td>
	<td class="tblHead" width="150">#stText.Overview.contexts.url#</td>
	<td class="tblHead" width="220">#stText.Overview.contexts.webroot#</td>
	<td class="tblHead" width="220">#stText.Overview.contexts.config_file#</td>
</tr>
<cfform onerror="customError" action="#request.self#" method="post">
<cfloop query="rst">
<input type="hidden" name="hash_#rst.currentrow#" id="hash_#rst.currentrow#" value="#rst.hash#"/>
<tr>
	<td class="tblContent" width="100"><input type="text" style="width:100px" name="label_#rst.currentrow#" id="label_#rst.currentrow#" value="#rst.label#"/></td>
	<td class="tblContent" width="150"><cfif len(rst.url)><a target="_blank" href="#rst.url#/railo-context/admin/web.cfm">#rst.url#</a></cfif></td>
	<td class="tblContent"><input type="text" style="width:220px" name="path_#rst.currentrow#" id="path_#rst.currentrow#" value="#rst.path#" readonly="readonly"/></td>
	<td class="tblContent"><input type="text" style="width:220px" name="cf_#rst.currentrow#" id="cf_#rst.currentrow#" value="#rst.config_file#" readonly="readonly"/></td>
</tr>
</cfloop>

<tr>
	<td colspan="4">
		<input class="submit" type="submit" class="submit" name="mainAction" id="mainAction" value="#stText.Buttons.Update#">
		<input class="submit" type="reset" class="reset" name="cancel" id="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>


</cfform>
</table><br /><br />
</cfif>
 


<table class="tbl">
<tr>
	<td colspan="2"><h2>#stText.Overview.Support#</h2></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.Professional#</td>
	<td class="tblContent"><a href="http://www.getrailo.com/index.cfm/services/support/" target="_blank">Support by Railo Technologies</a></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.Mailinglist_en#</td>
	<td class="tblContent"><a href="http://groups.google.com/group/railo" target="_blank">groups.google.com.railo</a></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.Mailinglist_de#</td>
	<td class="tblContent"><a href="http://de.groups.yahoo.com/group/railo/" target="_blank">de.groups.yahoo.com.railo</a></td>
</tr>
<tr>
	<td class="tblHead" width="150">Linked in</td>
	<td class="tblContent"><a href="http://www.linkedin.com/e/gis/71368/0CF7D323BBC1" target="_blank">Linked in</a></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.issueTracker#</td>
	<td class="tblContent"><a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank">jira.jboss.org.railo</a></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Overview.blog#</td>
	<td class="tblContent"><a href="http://www.railo-technologies.com/blog/" target="_blank">railo-technologies.com.blog</a></td>
</tr>

</table>
<!---
<cfif request.admintype EQ "server">
	<h2>#stText.Overview.LanguageSupport#</h2>
	<cfinclude template="overview.uploadNewLangFile.cfm">
	<table class="tbl">
		<tr>
			<td class="tblHead" width="150">#stText.Overview.ShortLabel#</td>
			<td class="tblHead" width="400">#stText.Overview.LanguageName#</td>
		</tr>
		<cfset stLangs = readLanguages()>
		<cfset aLangs = structKeyArray(stLangs)>
		<cfset arraySort(aLangs, "text")>
		<cfloop array="#aLangs#" index="sKey">
			<tr>
				<td class="tblContent" width="150">#sKey#</td>
				<td class="tblContent" width="400">#stLangs[sKey]#</td>
			</tr>
		</cfloop>
		<tr>
			<td class="tblHead">#stText.Overview.AddNewLanguage#</td>
			<form action="#cgi.script_name#?#cgi.query_string#" method="post" enctype="multipart/form-data">
			<td class="tblContent"><input type="File" name="newLangFile" id="newLangFile"><br>
			<input type="submit" value="#stText.Overview.Submit#"></td>
			</form>
		</tr>
	</table>
</cfif>
--->
</cfoutput>

<cffunction name="readLanguages" output="No" returntype="struct">
	<cfdirectory name="local.getLangs" directory="resources/language/" action="list" mode="listnames" filter="*.xml">
	<cfset var stRet = {}>
	<cfloop query="getLangs">
		<cffile action="read" file="resources/language/#getLangs.name#" variable="local.sContent">
		<cfset local.sXML = XMLParse(sContent)>
		<cfset stRet[sXML.language.XMLAttributes.Key] = sXML.language.XMLAttributes.label>
	</cfloop>
	<cfreturn stRet>
</cffunction>