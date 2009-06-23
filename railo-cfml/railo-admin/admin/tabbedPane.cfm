<cfif thistag.executionMode EQ "start">
<cfsilent>
<cfparam name="request.tpnames" type="struct" default="#struct()#">

	<cfif structkeyExists(request.tpnames,attributes.name)>
		<cfthrow message="ambigous tabbedPane name #attributes.name#">
	</cfif>
	<cfset request.tpnames[attributes.name]=1>
	<cfset actionName=attributes.name&"_tab">


<cfparam name="attributes.name" default="">
<cfif isDefined('url.'&actionName)>
	<cfset cTab=url[actionName]>
<cfelse>
	<cfset cTab=attributes.default>
</cfif>
<cfset attributes.ctab=ctab>
<cfset request._ctab=ctab>
<cfset baseurl=request.self>
<cfset baseurl=cgi.query_string>


<cfif isDefined('url.#actionName#')>
	
	<cfset qs="">
	<cfloop collection="#url#" item="key">
		<cfif key NEQ actionName><cfset qs=qs&key&"="&url[key]&"&"></cfif>
	</cfloop>
	<cfset baseurl=request.self&"?"&qs&"#actionName#=">
<cfelseif len(cgi.query_string)>
	<cfset baseurl=request.self&"?"&cgi.query_string&"&#actionName#=">
<cfelse>
	<cfset baseurl=request.self&"?#actionName#=">
</cfif>

</cfsilent>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td>

<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<!--- Füller --->
	<td valign="bottom">
<table border="0" cellpadding="2" cellspacing="0" width="100%">
<cfoutput>
<tr>
	<td width="4" class="tabtop"><img width="4" src="" height="1"></td>
</tr>
</table>

<cfset wasActive=false>
<cfset count=0>
<cfloop collection="#attributes.tabs#" item="key">
<cfset count=count+1>
	<!--- Inactiv --->
	<td valign="bottom">
	<cfif key NEQ ctab>
		
		<td valign="bottom">
		<table border="0" cellpadding="2" cellspacing="0">
		<tr>
			<td class="inactivTab" style="border-width:1px #iif(structCount(attributes.tabs) EQ count,'1','0')#px 1px #iif(not wasActive,'1','0')#px;"><a class="inactivTab" href="#baseurl##key#">#attributes.tabs[key]#</a></td>
		</tr>
		</table>
		</td>	
		<cfset wasActive=false>
	<cfelse>	
<td valign="bottom">
<table border="0" cellpadding="2" cellspacing="0">
<tr>
	<td class="activTab"><a class="activTab" href="#baseurl##key#">#attributes.tabs[key]#</a></td>
</tr>
</table>
</td>	
	<cfset wasActive=true>
	</cfif>
	
</cfloop>

	
	<!--- Füller --->
	<td valign="bottom" width="100%">
<table border="0" cellpadding="2" cellspacing="0" width="100%">
<tr>
	<td width="100%" class="tabtop"><img width="100%" src="" height="1"></td>
</tr>
</table>
	</td>
</tr>
</table>

</cfoutput>




	</td>
</tr>
<tr>
	<td class="tab">
<br>


<cfelse>

			</td>
</tr>

</table>
</cfif>


	
