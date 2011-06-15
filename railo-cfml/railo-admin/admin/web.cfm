<cfsilent>
<cfapplication name="webadmin" 
	sessionmanagement="yes" 
	clientmanagement="no" 
	setclientcookies="yes" 
	setdomaincookies="no">
    
<cfif structKeyExists(url,'enable')>
	<cfset session.enable=url.enable>
</cfif>
    
    
<cfset cookieKey="sdfsdf789sdfsd">
<cfparam name="request.adminType" default="web">
<cfparam name="form.rememberMe" default="s">
<cfset ad=request.adminType>
<!--- Form --->
<cfif StructKeyExists(form,"login_password"&request.adminType)>
	<cfset session["password"&request.adminType]=form["login_password"&request.adminType]>
	<cfset session.railo_admin_lang=form.lang>
	<cfcookie expires="NEVER" name="railo_admin_lang" value="#session.railo_admin_lang#">
	<cfif form.rememberMe NEQ "s">
		<cfcookie expires="#DateAdd(form.rememberMe,1,now())#" name="railo_admin_pw_#ad#" value="#Encrypt(form["login_password"&ad],cookieKey)#">
        
    <cfelse>
		<cfcookie expires="Now" name="railo_admin_pw_#ad#" value="">
	</cfif>
	<cfif isDefined("cookie.railoa_dmin_lastpage") and cookie.railo_admin_lastpage neq "logout">
		<cfset url.action = cookie.railo_admin_lastpage>
	</cfif>
</cfif>
<cfset login_error="">
<!--- new pw Form --->
<cfif StructKeyExists(form,"new_password") and StructKeyExists(form,"new_password_re")>
	<cfif len(form.new_password) LT 6>
		<cfset login_error="password is to short, it must have at least 6 chars">
	<cfelseif form.new_password NEQ form.new_password_re>
		<cfset login_error="password and password retype are not equal">
	<cfelse>
		<cfadmin 
			action="updatePassword"
			type="#request.adminType#"
			newPassword="#form.new_password#">
		<cfset session["password"&request.adminType]=form.new_password>
	</cfif> 
</cfif>

<!--- cookie ---->
<cfset fromCookie=false>
<cfif not StructKeyExists(session,"password"&request.adminType) and StructKeyExists(cookie,'railo_admin_pw_#ad#')>
	<cfset fromCookie=true>
    <cftry>
		<cfset session["password"&ad]=Decrypt(cookie['railo_admin_pw_#ad#'],cookieKey)>
    	<cfcatch></cfcatch>
    </cftry>
</cfif>

<!--- Session --->
<cfif StructKeyExists(session,"password"&request.adminType)>
	<cftry>
		<cfadmin 
			action="connect"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#">
		<!--- <cfset admin=createObject("java","railo.runtime.config.ConfigWebAdmin").
		newInstance(config,session["password"&request.adminType])>
		 --->
		 <cfcatch>
		 	<cfset login_error=cfcatch.message>
			<cfset StructDelete(session,"password"&request.adminType)>
		</cfcatch>
	</cftry>
</cfif> 


<cfif not StructKeyExists(session,'railo_admin_lang')>
	<cfset session.railo_admin_lang ='en'>
</cfif>
</cfsilent>
<cfinclude template="resources/text.cfm">


<cfset request.self = request.adminType & ".cfm">
<!--- includes several functions --->
<cfinclude template="web_functions.cfm">


<!--- Load Plugins --->
<cffunction name="loadPluginLanguage" output="false">
	<cfargument name="pluginDir">
	<cfargument name="pluginName">
    
    <cfset var fileLanguage="#pluginDir#/#pluginName#/language.xml">
    <cfset var language=struct(__action:'plugin',title:ucFirst(pluginName),text:'')>
    <cfset var txtLanguage="">
    <cfset var xml="">
    
	<cfif fileExists(fileLanguage)>
		<cffile action="read" file="#fileLanguage#" variable="txtLanguage" charset="utf-8">
		<cfxml casesensitive="no" variable="xml"><cfoutput>#txtLanguage#</cfoutput></cfxml>
        <cfif isDefined('xml.xmlRoot.XmlAttributes.action')>
        	<cfset language.__action=trim(xml.xmlRoot.XmlAttributes.action)>
        </cfif>
        <cfset xml = XmlSearch(xml, "/languages/language[@key='#lCase(session.railo_admin_lang)#']")[1]>
        
		<cfset language.title=xml.title.XmlText>
		<cfset language.text=xml.description.XmlText>
		<cfif isDefined('xml.custom')>
			<cfset var custom=xml.custom>
			<cfloop index="idx" from="1" to="#arraylen(custom)#">
				<cfset language[custom[idx].XmlAttributes.key]=custom[idx].XmlText>
			</cfloop>
		</cfif>
	</cfif>
	<cfreturn language>
</cffunction>


<cfset navigation = stText.MenuStruct[request.adminType]>

<cfset plugins=array()>
<cfif StructKeyExists(session,"password"&request.adminType)>
	<cftry>
    <cfadmin 
	    action="getPluginDirectory"
	    type="#request.adminType#"
	    password="#session["password"&request.adminType]#"
	    returnVariable="pluginDir">	
	<cfset mappings['/railo_plugin_directory/']=pluginDir>
	<cfapplication action="update" mappings="#mappings#">
	
    <cfif navigation[arrayLen(navigation)].action neq "plugin">
    	
        <cfset plugin=struct(
            label:"Plugins",
            children:plugins,
            action:"plugin"
        )>
    	<cfset navigation[arrayLen(navigation)+1]=plugin>
    	
        <cfset sctNav={}>
        <cfloop array="#navigation#" index="item">
        	<cfset sctNav[item.action]=item>
        </cfloop>
    	
    
        <cfdirectory directory="#plugindir#" action="list" name="plugindirs" recurse="no">
        <cfloop query="plugindirs">
            <cfif plugindirs.type EQ "dir">
                <cfset _lang=loadPluginLanguage(pluginDir,plugindirs.name)>
                <cfset _act=_lang.__action>
				<cfset StructDelete(_lang,"__action",false)>
				
				<cfset application.pluginLanguage[session.railo_admin_lang][plugindirs.name]=_lang>
                
                <cfset item=struct(
                    label:_lang.title,
                    action:plugindirs.name,
                    _action:'plugin&plugin='&plugindirs.name
                )>
                <cfset sctNav[_act].children[arrayLen(sctNav[_act].children)+1]=item>
            </cfif>
        </cfloop>
    </cfif>
    	<cfcatch></cfcatch>
    </cftry>
    
</cfif>

<cfsavecontent variable="arrow"><cfmodule template="img.cfm" src="arrow.gif" width="4" height="7" /></cfsavecontent>

<cfscript>

isRestrictedLevel=server.ColdFusion.ProductLevel EQ "community" or server.ColdFusion.ProductLevel EQ "professional";
isRestricted=isRestrictedLevel and request.adminType EQ "server";


// Navigation
// As a Set of Array and Structures, so that it is sorted


context=''; 
// write Naviagtion
strNav='';
current.label="Overview";
if(isDefined("url.action"))current.action=url.action;
else current.action="overview";

for(i=1;i lte arrayLen(navigation);i=i+1) {
	stNavi = navigation[i];
	hasChildren=StructKeyExists(stNavi,"children");
	
	
	subNav="";
	if(hasChildren) {
		for(iCld=1; iCld lte ArrayLen(stNavi.children); iCld=iCld+1) {
			stCld = stNavi.children[iCld];
			isActive=current.action eq stNavi.action & '.' & stCld.action or (current.action eq 'plugin' and stCld.action EQ url.plugin);
			if(isActive) {
				current.label = stNavi.label & ' - ' & stCld.label;
			}
			
			if(not toBool(stCld,"hidden") and (not isRestricted or toBool(stCld,"display"))) {
				if (isActive) {
					sClass = "navsub_active";
				}
				else {
					sClass = "navsub";
				}
				if(structKeyExists(stCld,'_action'))_action=stCld._action;
				else _action=stNavi.action & '.' & stCld.action;
				
				subNav = subNav & '<div class="navsub">'&arrow&'<a class="#sClass#" href="' & request.self & '?action=' & _action & '"> ' & stCld.label & '</a></div>';
			}
		}
	}
	strNav = strNav &'';
	hasChildren=hasChildren and len(subNav) GT 0;
	if(not hasChildren) {
		if(toBool(stNavi,"display"))strNav = strNav & '<div class="navtop"><a class="navtop" href="' & request.self & '?action=' & stNavi.action & '">' & stNavi.label & '</a></div>';
	}
	else {
		strNav = strNav & '<div class="navtop">' & stNavi.label & '</div>'&subNav& "";
	}
	strNav = strNav ;
}


function toBool(sct,key) {
	if(not StructKeyExists(sct,key)) return false;
	return sct[key];
}
function getRemoteClients() {
	if(not isDefined("form._securtyKeys")) return array();
	return form._securtyKeys;

}
request.getRemoteClients=getRemoteClients;
</cfscript>

<cfif not StructKeyExists(session,"password"&request.adminType)>
		<cfadmin 
			action="hasPassword"
			type="#request.adminType#"
			returnVariable="hasPassword">
	<cfif hasPassword>
		<cfmodule template="admin_layout.cfm" width="480" title="Login" onload="doFocus()">
			<cfif login_error NEQ ""><span class="CheckError"><cfoutput>#login_error#</cfoutput></span><br></cfif>
			<cfinclude template="login.cfm">
		</cfmodule>
	<cfelse>
		<cfmodule template="admin_layout.cfm" width="480" title="New Password">
			<cfif login_error NEQ ""><span class="CheckError"><cfoutput>#login_error#</cfoutput></span><br></cfif>
			<cfinclude template="login.new.cfm">
		</cfmodule>
	</cfif>
	
<cfelse>
	<cfsavecontent variable="content">
			<cfinclude template="#current.action#.cfm">
	</cfsavecontent>
	<cfmodule  template="admin_layout.cfm" width="960" navigation="#strNav#" right="#context#" title="#current.label#">
		<cfoutput>#content#</cfoutput>
	</cfmodule>
</cfif>
<cfif current.action neq "overview">
	<cfcookie name="railo_admin_lastpage" value="#current.action#" expires="NEVER">
</cfif>