<cfsilent> 
<cfparam name="request.disableFrame" default="false" type="boolean">
<cfparam name="request.setCFApplication" default="true" type="boolean">


<cfif request.setCFApplication>
<cfapplication name="webadmin" 
	sessionmanagement="yes" 
	clientmanagement="no" 
	setclientcookies="yes" 
	setdomaincookies="no">
</cfif>

<cfparam name="session.screenWidth" default="825">
<cfparam name="session.screenMode" default="compact">
<cfif structKeyExists(url,'screenmode')>
	<cfset session.screenmode=url.screenmode>
    <cfset session.realScreenSize=url.realScreenSize>
    <cfif session.screenmode EQ "full">
    	<cfset session.screenwidth=session.realScreenSize-260>
    <cfelse>
    	<cfset session.screenwidth=825>
    </cfif>
</cfif>



<cfif structKeyExists(url,'enable')>
	<cfset session.enable=url.enable>
</cfif>
  
<cfparam name="session.alwaysNew" default="false" type="boolean">
<cfif structKeyExists(url,'alwaysNew')>
	<cfset session.alwaysNew=url.alwaysNew EQ true>
</cfif>

<cfset cookieKey="sdfsdf789sdfsd">
<cfparam name="request.adminType" default="web">
<cfparam name="form.rememberMe" default="s">
<cfset ad=request.adminType>

<cfset login_error="">

<!--- Form --->
<cfif StructKeyExists(form,"login_password"&request.adminType)>
	<cfadmin 
        action="getLoginSettings"
        type="#request.adminType#"
   		returnVariable="loginSettings">

	<cfset loginPause=loginSettings.delay>
    	
    
    
	<cfif loginPause and StructKeyExists(application,'lastTryToLogin') and IsDate(application.lastTryToLogin) and DateDiff("s",application.lastTryToLogin,now()) LT loginPause>
    	<cfset login_error="Login disabled until #lsDateFormat(DateAdd("s",loginPause,application.lastTryToLogin))# #lsTimeFormat(DateAdd("s",loginPause,application.lastTryToLogin),'hh:mm:ss')#">
    <cfelse>
        <cfset application.lastTryToLogin=now()>
        <cfparam name="form.captcha" default="">
            
        <cfif loginSettings.captcha and structKeyExists(session,"cap") and form.captcha NEQ session.cap>
    		<cfset login_error="Invalid security code (captcha) definition">
        	
        <cfelse>        
			<cfset session["password"&request.adminType]=form["login_password"&request.adminType]>
            <cfset session.railo_admin_lang=form.lang>
            <cfcookie expires="NEVER" name="railo_admin_lang" value="#session.railo_admin_lang#">
            <cfif form.rememberMe NEQ "s">
                <cfcookie expires="#DateAdd(form.rememberMe,1,now())#" name="railo_admin_pw_#ad#" value="#Encrypt(form["login_password"&ad],cookieKey,"CFMX_COMPAT","hex")#">
            <cfelse>
                <cfcookie expires="Now" name="railo_admin_pw_#ad#" value="">
            </cfif>
            <cfif isDefined("cookie.railoa_dmin_lastpage") and cookie.railo_admin_lastpage neq "logout">
                <cfset url.action = cookie.railo_admin_lastpage>
            </cfif>
        </cfif>
		
		
        
    </cfif>
</cfif>
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
		<cfset session["password"&ad]=Decrypt(cookie['railo_admin_pw_#ad#'],cookieKey,"CFMX_COMPAT","hex")>
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
        <cfset language.__position=0>
        <cfif isDefined('xml.xmlRoot.XmlAttributes.action')>
        	<cfset language.__action=trim(xml.xmlRoot.XmlAttributes.action)>
        	<cfset language.__position=StructKeyExists(xml.xmlRoot.XmlAttributes,"position")?xml.xmlRoot.XmlAttributes.position:0>
        </cfif>
        <cfset xml = XmlSearch(xml, "/languages/language[@key='#lCase(session.railo_admin_lang)#']")[1]>
        
		<cfset language.__group=StructKeyExists(xml,"group")?xml.group.XmlText:UCFirst(language.__action)>
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
	
    <cfset hasPlugin=false>
    <cfloop array="#navigation#" index="el">
    	<cfif el.action EQ "plugin"><cfset hasPlugin=true></cfif>
    </cfloop>
    	
	<cfif not hasPlugin or (structKeyExists(session,"alwaysNew") and session.alwaysNew)>
    	<cfif not hasPlugin>
        <cfset plugin=struct(
            label:"Plugins",
            children:plugins,
            action:"plugin"
        )>
    	<cfset navigation[arrayLen(navigation)+1]=plugin>
        </cfif>
    	
        <cfset sctNav={}>
        <cfloop array="#navigation#" index="item">
        	<cfset sctNav[item.action]=item>
        </cfloop>
    	
        <cfdirectory directory="#plugindir#" action="list" name="plugindirs" recurse="no">
        <cfloop query="plugindirs">
            <cfif plugindirs.type EQ "dir">
                <cfset _lang=loadPluginLanguage(pluginDir,plugindirs.name)>
                <cfset _act=_lang.__action>
				<cfset _group=_lang.__group>
				<cfset _pos=_lang.__position>
				<cfset StructDelete(_lang,"__action",false)>
				
				<cfset application.pluginLanguage[session.railo_admin_lang][plugindirs.name]=_lang>
                
                <cfset item=struct(
                    label:_lang.title,
                    action:plugindirs.name,
                    _action:'plugin&plugin='&plugindirs.name
                )>
                
                <cfif not StructKeyExists(sctNav,_act)>
                	<cfset sctNav[_act]=struct(
						label:_group,
						children:[],
						action:_act
					)>
                    <cfif _pos GT 0 and _pos LTE arrayLen(navigation)>
                    	<cfscript>
						for(i=arrayLen(navigation)+1;i>_pos;i--){
							navigation[i]=navigation[i-1];
						}
                        navigation[_pos]=sctNav[_act];
						</cfscript>
                    <cfelse>
                    	<cfset navigation[arrayLen(navigation)+1]=sctNav[_act]>
                    </cfif>
                    
                </cfif>
                
                <cfset children=sctNav[_act].children>
                <cfset isUpdate=false>
                <cfloop from="1" to="#arrayLen(children)#" index="i">
                	<cfif children[i].action EQ item.action>
                    	<cfset children[i]=item>
                        <cfset isUpdate=true>
            </cfif>
        </cfloop>
                <cfif not isUpdate>
                	<cfset children[arrayLen(children)+1]=item>
    </cfif>
    
</cfif>
        </cfloop>
    </cfif>
    	<cfcatch><cfrethrow></cfcatch>
    </cftry>

</cfif>
<cfsavecontent variable="arrow"><cfmodule template="img.cfm" src="arrow.gif" width="4" height="7" /></cfsavecontent>
<cfif structKeyExists(url,"action") and url.action EQ "plugin" && not structKeyExists(url,"plugin")>
	<cflocation url="#request.self#" addtoken="no">
</cfif>
<cfscript>

isRestrictedLevel=server.ColdFusion.ProductLevel EQ "community" or server.ColdFusion.ProductLevel EQ "professional";
isRestricted=isRestrictedLevel and request.adminType EQ "server";


// Navigation
// As a Set of Array and Structures, so that it is sorted


context=''; 
// write Naviagtion
current.label="Overview";
if(isDefined("url.action"))current.action=url.action;
else current.action="overview";

strNav ="";
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
				/*if (isActive) {
					sClass = "navsub_active";
				}
				else {
					sClass = "navsub";
				}*/
				if(structKeyExists(stCld,'_action'))_action=stCld._action;
				else _action=stNavi.action & '.' & stCld.action;
				
				subNav = subNav & '<li><a '&(isActive?'class="menu_active"':'class="menu_inactive"')&' href="' & request.self & '?action=' & _action & '"> ' & stCld.label & '</a></li>';
				//subNav = subNav & '<div class="navsub">'&arrow&'<a class="#sClass#" href="' & request.self & '?action=' & _action & '"> ' & stCld.label & '</a></div>';
			}
		}
	}
	strNav = strNav &'';
	hasChildren=hasChildren and len(subNav) GT 0;
	if(not hasChildren) {
		if(toBool(stNavi,"display"))strNav = strNav & '<li><a href="' & request.self & '?action=' & stNavi.action & '">' & stNavi.label & '</a></li>';
		//if(toBool(stNavi,"display"))strNav = strNav & '<div class="navtop"><a class="navtop" href="' & request.self & '?action=' & stNavi.action & '">' & stNavi.label & '</a></div>';
	}
	else {
		strNav = strNav & '<li><a href="##">' & stNavi.label & '</a><ul>'&subNav& "</ul></li>";
		//strNav = strNav & '<div class="navtop">' & stNavi.label & '</div>'&subNav& "";
	}
	//strNav = strNav ;
}
strNav ='<ul id="menu">'& strNav&'</ul>' ;

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



<script src="../jquery.js.cfm"></script>
<script src="../jquery.blockUI.js.cfm"></script>
<cfsavecontent variable="strNav">
<script>
function initMenu() {
	$('#menu ul').show();
	$('#menu li a').click(
  		function() {
    		$(this).next().slideToggle('normal');
  		}
	);
}

function initMenu2() {
  $('#menu ul').hide();
  $('#menu ul:first').show();
  $('#menu li a').click(
    function() {
      var checkElement = $(this).next();
      if((checkElement.is('ul')) && (checkElement.is(':visible'))) {
        return false;
        }
      if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
        $('#menu ul:visible').slideUp('normal');
        checkElement.slideDown('normal');
        return false;
        }
      }
    );
  }

var disableBlockUI=false;

// {form:_form,name:_input.name,value:v,error:err.error};
function customError(errors){ 
	if(!errors || errors.length==0) return;
	var err;
	var form=errors[0].form;
	var el;
	var clazz;
	var input;

	// remove error from last round
	try{
  		for(var i=0;i<form.elements.length;i++){
    		input=form.elements[i];
    		el=$(input);
    		clazz=el.attr("class");
    		if(clazz && clazz=="InputError") {
      			el.removeClass();
      			el=$("#msg_"+input.name);
      			el.remove();
    		}
  		}
  	}
  	catch(err){
		alert(err)
	}

	// create new error
  	for(var i=0;i<errors.length;i++){
    	err=errors[i];
    	var input=form[err.name];
    	var _input=$(input);
    	if(i==0) _input.focus();
    	_input.addClass("InputError");
    	_input.after('<span id="msg_'+err.name+'" class="commentError"><br/>'+err.error+'</span>');
  	}
  	disableBlockUI=true;
}

function createWaitBlockUI(){
  var _blockUI=function() { 
      if(!disableBlockUI)
      $.blockUI(
        { 
          message:<cfoutput>"#JSStringFormat(stText.general.wait)#"</cfoutput>,
          css: { 
              border: 'none', 
              padding: '15px', 
              backgroundColor: '#000', 
              '-webkit-border-radius': '10px', 
              '-moz-border-radius': '10px', 
              opacity: .5, 
              color: '#fff' ,
              fontSize : "18pt"
            },
          fadeIn: 1000 
        }
      ); 
    }
  return _blockUI;
}

$(document).ready(function() { 
  initMenu();
    
    __blockUI=function() {
      setTimeout(createWaitBlockUI(),1000);
    
    }

  $('.submit,.menu_inactive,.menu_active').click(__blockUI);
    }); 


</script>
<cfoutput>#strNav#</cfoutput>
</cfsavecontent>





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
				<cfif not FindOneOf("\/",current.action)><cfinclude template="#current.action#.cfm"><cfelse><cfset current.label="Error">invalid action definition</cfif>
	</cfsavecontent>
	<cfif request.disableFrame>
    	<cfoutput>#content#</cfoutput>
    <cfelse>
    	<cfmodule template="admin_layout.cfm" width="960" navigation="#strNav#" right="#context#" title="#current.label#">
			<cfoutput>#content#</cfoutput>
        </cfmodule>
    </cfif>
    
</cfif>
<cfif current.action neq "overview">
	<cfcookie name="railo_admin_lastpage" value="#current.action#" expires="NEVER">
</cfif>