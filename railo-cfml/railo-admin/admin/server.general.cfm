<cfset error.message="">
<cfset error.detail="">


<!--- Component --->
<cfadmin 
	action="getComponent"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="component">
<cfset setting.component={
	compatibility:{
		baseComponentTemplate:component.strBaseComponentTemplate,
		componentDumpTemplate:component.strComponentDumpTemplate,
		
		componentDataMemberDefaultAccess:'public',
		triggerDataMember:false,
		useShadow:true
	},
	strict:{
		baseComponentTemplate:component.strBaseComponentTemplate,
		componentDumpTemplate:component.strComponentDumpTemplate,
		
		componentDataMemberDefaultAccess:'private',
		triggerDataMember:false,
		useShadow:false
	},
	speed:{
		baseComponentTemplate:component.strBaseComponentTemplate,
		componentDumpTemplate:component.strComponentDumpTemplate,
		
		componentDataMemberDefaultAccess:'private',
		triggerDataMember:false,
		useShadow:false
	}
}>

<!--- Charset --->
<cfadmin 
	action="getCharset"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="charset">
<cfset setting.charset={
	compatibility:{
		templateCharset:charset.jreCharset,
		webCharset:'UTF-8',
		resourceCharset:charset.jreCharset
	},
	strict:{
		templateCharset:charset.jreCharset,
		webCharset:'UTF-8',
		resourceCharset:charset.jreCharset
	},
	speed:{
		templateCharset:charset.jreCharset,
		webCharset:'UTF-8',
		resourceCharset:charset.jreCharset
	}
}>

<!--- Scope --->
<cfadmin 
	action="getScope"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="scope">
<cfset setting.scope={
	compatibility:{
		sessionType:scope.sessionType,
		sessionTimeout:scope.sessionTimeout,
		applicationTimeout:scope.applicationTimeout,
		sessionManagement:scope.sessionManagement,
		clientManagement:scope.clientManagement,
		clientCookies:scope.clientCookies,
		domaincookies:scope.domaincookies,
				
		localMode:'update',
		scopeCascadingType:'standard',
		allowImplicidQueryCall:true,
		mergeFormAndUrl:false
	},
	strict:{
		sessionType:scope.sessionType,
		sessionTimeout:scope.sessionTimeout,
		applicationTimeout:scope.applicationTimeout,
		sessionManagement:scope.sessionManagement,
		clientManagement:scope.clientManagement,
		clientCookies:scope.clientCookies,
		domaincookies:scope.domaincookies,
				
		localMode:'update',
		scopeCascadingType:'strict',
		allowImplicidQueryCall:false,
		mergeFormAndUrl:false
	},
	speed:{
		sessionType:scope.sessionType,
		sessionTimeout:scope.sessionTimeout,
		applicationTimeout:scope.applicationTimeout,
		sessionManagement:scope.sessionManagement,
		clientManagement:scope.clientManagement,
		clientCookies:scope.clientCookies,
		domaincookies:scope.domaincookies,
				
		localMode:'always',
		scopeCascadingType:'strict',
		allowImplicidQueryCall:false,
		mergeFormAndUrl:true
	}
}>

<!--- Datasource --->
<cfadmin 
	action="getDatasourceSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="dbSetting">
<cfset setting.datasource={
	compatibility:{
		psq:true
	},
	strict:{
		psq:false
	},
	speed:{
		psq:false
	}
}>

<!--- customtag --->
<cfadmin 
	action="getCustomtagSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="customtag">
<cfset setting.customtag={
	compatibility:{
		deepSearch:true,
		localSearch:true,
		extensions="cfm,cfml"
	},
	strict:{
		deepSearch:false,
		localSearch:false,
		extensions="cfc,cfm"
	},
	speed:{
		deepSearch:false,
		localSearch:false,
		extensions="cfc,cfm"
	}
}>


<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="setting"
	secValue="yes">

<!--- 
Defaults --->	
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">





<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		
        <cfcase value="#stText.Buttons.Update#">
        	<!--- component --->
        	<cfadmin action="updateComponent"
                type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				attributeCollection="#setting.component[form.mode]#"
                remoteClients="#request.getRemoteClients()#"
				>
            <!--- charset --->
            <cfadmin action="updateCharset"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                attributeCollection="#setting.charset[form.mode]#"
                remoteClients="#request.getRemoteClients()#">
            <!--- scope --->
			<cfadmin 
				action="updateScope"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				attributeCollection="#setting.scope[form.mode]#"
				remoteClients="#request.getRemoteClients()#">
            <!--- datasource --->
            <cfadmin 
				action="updatePSQ"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				attributeCollection="#setting.datasource[form.mode]#"
				remoteClients="#request.getRemoteClients()#">
            <!--- customtag --->
            <cfadmin 
                action="updateCustomTagSetting"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
				attributeCollection="#setting.customtag[form.mode]#"
                remoteClients="#request.getRemoteClients()#">


			
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
<cfset colorCompatibility="green">
<cfset colorSpeed="orange">
<cfset colorStrict="purple">
<cfset style="padding:2px 10px 2px 10px;">
<cffunction name="doStyle">
	<cfargument name="value">
	<cfargument name="group">
	<cfargument name="name">
    
    
    <cfset var compat=setting[group].compatibility[name]>
    <cfset var speed=setting[group].speed[name]>
    <cfset var strict=setting[group].strict[name]>
    <cfset var color="">
	<cfif value EQ compat>
    	<cfset color=colorCompatibility>
    <cfelseif value EQ speed>
    	<cfset color=colorSpeed>
    <cfelseif value EQ strict>
    	<cfset color=colorStrict>
    </cfif>
    
    
	<cfreturn 'border-color:#color#;#style#'>
</cffunction>



#stText.setting.general[request.adminType]#



<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">

<table class="tbl" width="700">
<!---- data member default access --->

        <tr>
        	<td class="tblContent" style="border-color:#colorCompatibility#;" width="10"><cfinput type="radio" name="mode" id="mode" value="compatibility"></td>
            <td class="tblContent" style="border-color:#colorCompatibility#;#style#"><b>#stText.setting.general.compatibility#</b><br />
            <span class="comment">#stText.setting.general.compatibilityDesc#</span></td>
        </tr>
        <tr>
        	<td class="tblContent" style="border-color:#colorStrict#;" width="10"><cfinput type="radio" name="mode" id="mode" value="strict"></td>
            <td class="tblContent" style="border-color:#colorStrict#;#style#"><b>#stText.setting.general.strict#</b><br />
            <span class="comment">#stText.setting.general.strictDesc#</span></td>
        </tr>
        <tr>
        	<td class="tblContent" style="border-color:#colorSpeed#;" width="10"><cfinput type="radio" name="mode" id="mode" value="speed"></td>
            <td class="tblContent" style="border-color:#colorSpeed#;#style#"><b>#stText.setting.general.speed#</b>
            <br /><span class="comment">#stText.setting.general.speedDesc#</span></td>
        </tr>
        
        
<tr>
	<td colspan="2">
		
      <input class="submit" type="submit" class="submit" name="mainAction" id="mainAction" value="#stText.Buttons.update#">
		<input class="submit" type="reset" class="reset" name="cancel" id="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</table>






<!------------------------------
			COMPONENT 
------------------------------->
<h2>#stText.setting.general.component#</h2>
#stText.Components[request.adminType]#
<table class="tbl" width="700">
<!---- data member default access --->
<cfset access=component.componentDataMemberDefaultAccess>
<tr>
	<td class="tblHead" width="150">#stText.Components.DataMemberAccessType#</td>
	<td class="tblContent" style="#doStyle(access,'component','componentDataMemberDefaultAccess')#">
    	<b>#stText.Components['DMAT'& access]#</b>
        <br /><span class="comment">#stText.Components.DataMemberAccessTypeDescription#</span>
	</td>
</tr>
<!--- Trigger Data Member --->
<tr>
	<td class="tblHead" width="150">#stText.Components.triggerDataMember#</td>
	<td class="tblContent"  style="#doStyle(component.triggerDataMember,'component','triggerDataMember')#">
    	<b>#yesNoFormat(component.triggerDataMember)#</b>
        <br /><span class="comment">#stText.Components.triggerDataMemberDescription#</span>
	</td>
</tr>
<!--- Use Shadow --->
<tr>
	<td class="tblHead" width="150">#stText.Components.useShadow#</td>
	<td class="tblContent"  style="#doStyle(component.useShadow,'component','useShadow')#">
		<b>#yesNoFormat(component.useShadow)#</b>
		<br /><span class="comment">#stText.Components.useShadowDescription#</span><br>
      	
	</td>
</tr>
</table>





<!------------------------------
			CHARSET 
------------------------------->
<h2>#stText.setting.general.charset#</h2>
#stText.charset[request.adminType]#

<table class="tbl" width="700">
<!--- Template --->
<tr>
	<td class="tblHead" width="150">#stText.charset.templateCharset#</td>
	<td class="tblContent" style="#doStyle(charset.templateCharset,'charset','templateCharset')#">
    	<b>#charset.templateCharset#</b><br />
		<span class="comment">#stText.charset.templateCharsetDescription#</span><br />
	</td>
</tr>

<!--- Web --->
<tr>
	<td class="tblHead" width="150">#stText.charset.webCharset#</td>
	<td class="tblContent" style="#doStyle(charset.webCharset,'charset','webCharset')#">
		<b>#charset.webCharset#</b><br />
		<span class="comment">#stText.charset.webCharsetDescription#</span><br />
	</td>
</tr>

<!--- Resource --->
<tr>
	<td class="tblHead" width="150">#stText.charset.resourceCharset#</td>
	<td class="tblContent" style="#doStyle(charset.resourceCharset,'charset','resourceCharset')#">
		<b>#charset.resourceCharset#</b><br />
		<span class="comment">#stText.charset.resourceCharsetDescription#</span><br />
	</td>
</tr>
</table>




<!------------------------------
			SCOPE 
------------------------------->
<h2>#stText.setting.general.scope#</h2>
#stText.scopes[request.adminType]#

<table class="tbl" width="700">
<!--- scope cascading --->
<tr>
	<td class="tblHead" width="150">#stText.Scopes.Cascading#</td>
	<td class="tblContent" style="#doStyle(scope.scopeCascadingType,'scope','scopeCascadingType')#">
		<b>#ucFirst(stText.Scopes[scope.scopeCascadingType])#</b>
        <br /><span class="comment">#stText.Scopes.CascadingDescription#</span>
	</td>
</tr>
<!--- cascade to result --->
<tr>
	<td class="tblHead" width="150">#stText.Scopes.CascadeToResultSet#</td>
	<td class="tblContent" style="#doStyle(scope.allowImplicidQueryCall,'scope','allowImplicidQueryCall')#">
		<b>#yesNoFormat(scope.allowImplicidQueryCall)#</b>
		<br /><span class="comment">#stText.Scopes.CascadeToResultSetDescription#</span>
	</td>
</tr>
<!--- Merge URL and Form --->
<tr>
	<td class="tblHead" width="150">#stText.Scopes.mergeUrlForm#</td>
	<td class="tblContent" style="#doStyle(scope.mergeFormAndUrl,'scope','mergeFormAndUrl')#">
		<b>#yesNoFormat(scope.mergeFormAndUrl)#</b>
		<br /><span class="comment">#stText.Scopes.mergeUrlFormDescription#</span>
	</td>
</tr>
<!--- Local Mode --->
<tr>
	<td class="tblHead" width="150">#stText.Scopes.LocalMode#</td>
	<td class="tblContent" style="#doStyle(scope.localMode,'scope','localMode')#">
		<b>#scope.localMode#</b>
        <br /><span class="comment">#stText.Scopes.LocalModeDesc#</span>
	</td>
</tr>
</table>




<!------------------------------
			DATASOURCE 
------------------------------->
<h2>#stText.setting.general.datasource#</h2>
#stText.Settings.DatasourceSettings#
<table class="tbl" width="700">
<!--- PSQ --->
<tr>
	<td class="tblHead" width="150">#stText.Settings.PreserveSingleQuotes#</td>
	<td class="tblContent" style="#doStyle(dbSetting.psq,'datasource','psq')#">
	<b>#yesNoFormat(dbSetting.psq)#</b>
	<br /><span class="comment">#stText.Settings.PreserveSingleQuotesDescription#</span></td>
	
</tr>

</table>




<!------------------------------
			CUSTOM TAGS 
------------------------------->
<h2>#stText.setting.general.customtag#</h2>
#stText.CustomTags.CustomtagSetting#
<table class="tbl" width="700">
<!--- Deep Search --->
<tr>
	<td class="tblHead" width="150">#stText.CustomTags.customTagDeepSearch#</td>
	<td class="tblContent" style="#doStyle(customtag.deepsearch,'customtag','deepsearch')#">
    <b>#yesNoFormat(customtag.deepsearch)#</b><br />
	<span class="comment">#stText.CustomTags.customTagDeepSearchDesc#</span></td>
</tr>
<!--- Local Search --->
<tr>
	<td class="tblHead" width="150">#stText.CustomTags.customTagLocalSearch#</td>
	<td class="tblContent" style="#doStyle(customtag.localsearch,'customtag','localsearch')#">
	<b>#yesNoFormat(customtag.localsearch)#</b><br />
	<span class="comment">#stText.CustomTags.customTagLocalSearchDesc#</span></td>
	
</tr>
<!--- Extension --->
<cfset value=ArrayToList(customtag.extensions)>
<tr>
	<td class="tblHead" width="150">#stText.CustomTags.extensions#</td>
	<td class="tblContent" style="#doStyle(value,'customtag','extensions')#">
    	<b>#value#</b><br />
        <span class="comment">#stText.CustomTags.extensionsDesc#</span>
    
    
    </td>
</tr>
</table>
</cfform>
</cfoutput>
<br><br>