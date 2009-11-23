<cfsilent>
<cfapplication name="webadmin" 
	sessionmanagement="yes" 
	clientmanagement="no" 
	setclientcookies="yes" 
	setdomaincookies="no">
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

<cfinclude template="resources/text.cfm">
<!--- Includes the Menu --->
<cfinclude template="resources/#session.railo_admin_lang#/res_menu.cfm">
</cfsilent>
<cfset request.self = request.adminType & ".cfm">
<!--- includes several functions --->
<cfinclude template="web_functions.cfm">


<!--- Load Plugins --->
<cfset plugins=array()>
<cfif StructKeyExists(session,"password"&request.adminType)>
<cftry><cfadmin 
    action="getPluginDirectory"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    returnVariable="pluginDir">	
    
<cfset mappings['/railo_plugin_directory/']=pluginDir>
<cfapplication action="update" mappings="#mappings#">


	<cfdirectory directory="#plugindir#" action="list" name="plugindirs" recurse="no">
    
	<cfcatch >
		<cfset plugindirs=queryNew('name')>
	</cfcatch>
</cftry>

<cfloop query="plugindirs">
	<cfif plugindirs.type EQ "dir">
		<cfset varTitle="application.pluginlanguage[session.railo_admin_lang][plugindirs.name].title">
		<cfset item=struct(
			label:iif(isDefined(varTitle),(varTitle),de(plugindirs.name)),
			action:plugindirs.name,
			_action:'plugin&plugin='&plugindirs.name
		)>
		<cfset plugins[arrayLen(plugins)+1]=item>
	</cfif>
</cfloop>
<cfset plugin=struct(
	label:"Plugin",
	children:plugins,
	action:"plugin"
)>
</cfif>
<cfscript>

isRestrictedLevel=server.ColdFusion.ProductLevel EQ "community" or server.ColdFusion.ProductLevel EQ "professional";
isRestricted=isRestrictedLevel and request.adminType EQ "server";


// Navigation
// As a Set of Array and Structures, so that it is sorted
navigation = stText.MenuStruct;
if(arrayLen(plugins))navigation[arrayLen(navigation)+1]=plugin;

context=''; 
// write Naviagtion
strNav='';
arrow='<img src="resources/img/arrow.gif.cfm"  width="4" height="7" />';
current.label="Overview";
if(isDefined("url.action"))current.action=url.action;
else current.action="overview";

for(i=1;i lte arrayLen(navigation);i=i+1) {
	stNavi = navigation[i];
	hasChildren=StructKeyExists(stNavi,"children");
	if(current.action EQ stNavi.action) {
		current.label=stNavi.label;
	}
	
	
	subNav="";
	if(hasChildren) {
		for(iCld=1; iCld lte ArrayLen(stNavi.children); iCld=iCld+1) {
			stCld = stNavi.children[iCld];
			if(current.action EQ stNavi.action & '.' & stCld.action) {
				current.label = stNavi.label & ' - ' & stCld.label;
			}
			
			if(not toBool(stCld,"hidden") and (not isRestricted or toBool(stCld,"display"))) {
				if (current.action eq stNavi.action & '.' & stCld.action) {
					sClass = "navsub_active";
				} else {
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
		if(toBool(stNavi,"display"))strNav = strNav & '<div class="navtop">ssssss<a class="navtop" href="' & request.self & '?action=' & stNavi.action & '">' & stNavi.label & '</a></div>';
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
