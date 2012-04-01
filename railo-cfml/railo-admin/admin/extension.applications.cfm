<cfsetting enablecfoutputonly="yes">
<cfinclude template="extension.functions.cfm">

<!--- if pull request 35 s a fact, then this can be removed --->
<cfsavecontent variable="extraCSS"><cfoutput>
	<style type="text/css">
		/* new layout; Paul K */
		.clear { clear:both }
		div.error, div.warning, div.message {
			border:2px solid red;
			padding:5px;
			margin:10px 0px;
			font-weight:bold;
			color:red;
		}
		div.warning {
			border-color: ##FC6;
			color:##000;
		}
		div.message {
			border-color: ##0C0;
			color:##000;
		}
		.right { text-align:right; }
		.left { text-align:left; }
		.center { text-align:center }
		
		/* tables */
		.maintbl {
			width:100%;
			border-collapse:separate;
		}
		.maintbl td, .maintbl th {
			padding: 2px 5px;
			text-align:left;
			font-weight:normal;
		}
		.maintbl tbody td, .maintbl tbody th {/* like .tblContent */
			border:1px solid ##e0e0e0;
		}
		.maintbl tbody th {/* like .tblHead */
			background-color:##f2f2f2;
			color:##3c3e40;
			width: 30%;
		}
		.maintbl tfoot td {
			border:none;
		}
		
		.optionslist {border:0; border-collapse:collapse; width:auto;}
		.optionslist td, .optionslist th { padding:3px; vertical-align:top;}
		.contentlayout { border-collapse:collapse; width:100%; }
		.contentlayout td, .contentlayout th { border:0; }
		
		/* page header with H2 and other content */
		.modheader {
			margin-top: 20px;
		}
		
		/* filter form */
		.filterform {
			padding:5px;
			margin:10px 0px;
			border:1px solid ##e0e0e0;
			background-color:##f2f2f2;
			color:##3c3e40
		}
		.filterform ul {
			list-style:none;
			margin:0;
			padding:0;
		}
		.filterform li {
			width: auto;
			float:left;
			padding-right: 10px;
		}
		.filterform label {
			width: 200px;
			height:18px;
			display:block;
		}
		.filterform input.txt, .filterform select {
			width: 200px;
		}
		.filterform input.submit {
			margin-top: 20px;
		}
		
		/* extensions overview */
		.extensionlist {
			margin-bottom: 20px;
		}
		.extensionthumb {
			width:140px;
			height:100px;
			overflow: hidden;
			margin:5px 5px 0px 0px;
			float:left;
			text-align:center;
		}
		.extensionthumb a {
			display:block;
			padding:2px;
			height: 94px;
			text-decoration:none !important;
			border: 1px solid ##E0E0E0;
		}
		.extensionthumb a:hover {
			background-color:##f8f8f8;
			border-color: ##007bb7;
		}
		.extimg {
			height:50px;
		}
		textarea.licensetext {
			height:200px;
			width:100%;
			font-family:Courier New;
			font-size : 8pt;
			color:##595F73;
			border: 1px solid ##666;
		}	
	</style>
</cfoutput></cfsavecontent>
<cfhtmlhead text="#extraCSS#" />


<cfif StructKeyExists(form,'action2')>
	<cfset url.action2="install3">
</cfif>
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfif not isDefined('session.extFilter2')>
	<cfset session.extFilter.filter="">
	<cfset session.extFilter.filter2="">
	<cfset session.extFilter.category="">
	<cfset session.extFilter.name="">
	<cfset session.extFilter.provider="">
	<cfset session.extFilter2.category="">
	<cfset session.extFilter2.name="">
	<cfset session.extFilter2.provider="">
</cfif>

<!--- get providers --->
<cfadmin 
	action="getExtensionProviders"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="providers">
<cfset request.providers=providers>
    
<!--- get already installed extensions --->
<cfadmin 
    action="getExtensions"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    returnVariable="extensions">
 
<cfparam name="err" default="#struct(message:"",detail:"")#">

<!--- get all extensions from all providers --->
<cfset data="">
<cfloop query="providers">
	<cftry>
		<cfset provider=loadCFC(providers.url)>
        <cfset _apps=provider.listApplications()>
        <cfset _info=provider.getInfo()>
        <cfset _url=providers.url>
		<cfif IsSimpleValue(data)>
        	<cfset data=queryNew(_apps.columnlist&",provider,info,uid")>
        </cfif>
        
        <!--- check if all columns exist --->
        <cfloop list="#_apps.columnlist#" index="col">
			<cfif not queryColumnExists(data,col)><cfset QueryAddColumn(data,col,array())></cfif>
        </cfloop>
		
        <cfloop query="_apps">
        	<cfset QueryAddRow(data)>
            <cfloop list="#_apps.columnlist#" index="col">
            	<cfset data[col][data.recordcount]=_apps[col]>
            </cfloop>
            <cfset data.provider[data.recordcount]=_url>
            <cfset data.info[data.recordcount]=_info>
            <cfset data.uid[data.recordcount]=createId(_url,_apps.id)>
        </cfloop>
        
        
    	<cfcatch>
        	<cfif len(err.message)>
        		<cfset err.message&="<br>Can't load provider [#providers.url#]; error: [#cfcatch.message#]">
            <cfelse>
        		<cfset err.message="Can't load provider [#providers.url#]; error: [#cfcatch.message#]">
            </cfif>
        </cfcatch>
    </cftry>
</cfloop>
<cfif isQuery(data)><cfset querySort(query:data,names:"name,uid,category")></cfif>


<!--- Action --->
<cfparam name="variables.error" default="#struct(message:"",detail:"")#" />

<cftry>
	<cfswitch expression="#form.mainAction#">
		<!--- Filter --->
		<cfcase value="#stText.Buttons.filter#">
        	<cfif StructKeyExists(form,"filter")>
				<cfset session.extFilter.filter=trim(form.filter)>
            <cfelseif StructKeyExists(form,"filter2")>
				<cfset session.extFilter.filter2=trim(form.filter2)>
            <cfelseif StructKeyExists(form,"categoryFilter")>
				<cfset session.extFilter.category=trim(form.categoryFilter)>
                <cfset session.extFilter.name=trim(form.nameFilter)>
                <cfset session.extFilter.provider=trim(form.providerFilter)>
            <cfelse>
				<cfset session.extFilter2.category=trim(form.categoryFilter2)>
                <cfset session.extFilter2.name=trim(form.nameFilter2)>
                <cfset session.extFilter2.provider=trim(form.providerFilter2)>
            </cfif>
		</cfcase>
        <cfcase value="#stText.Buttons.install#">
        	<cflocation url="#request.self#?action=#url.action#&action2=install1&uid=#form.uid#" addtoken="no">
		</cfcase>
        <cfcase value="#stText.Buttons.uninstall#">
        	<cflocation url="#request.self#?action=#url.action#&action2=uninstall&uid=#form.uid#" addtoken="no">
		</cfcase>
        <cfcase value="#stText.Buttons.update#">
        	<cflocation url="#request.self#?action=#url.action#&action2=install1&uid=#form.uid#" addtoken="no">
		</cfcase>
	</cfswitch>
	
	<cfinclude template="#url.action#.#url.action2#.cfm" />
	
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>

<cfsetting enablecfoutputonly="no">
<!--- Error Output --->
<cfif err.message neq "">
	<cfset printError(err)>
</cfif>
<cfif error.message neq "">
	<cfset printError(error)>
</cfif>