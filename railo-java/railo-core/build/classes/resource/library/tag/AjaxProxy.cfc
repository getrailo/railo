<cfcomponent extends="railo.core.ajax.AjaxBase">

	<cfset variables.instance.proxyHelper = createObject('component','railo.core.ajax.AjaxProxyHelper').init() />
	<cfset variables.instance.ajaxBinder = createObject('component','railo.core.ajax.AjaxBinder').init() />

	<!--- Meta data --->
	<cfset this.metadata.attributetype="fixed">
    <cfset this.metadata.attributes={
		cfc:		{required:false,type:"string",default:""},
		jsclassname:{required:false,type:"string",default:""},		
		bind:		{required:false,type:"string",default:""},
		onError:	{required:false,type:"string",default:""},
		onSuccess:	{required:false,type:"string",default:""},
		extends:	{required:false,type:"boolean",default:false},
		methods:	{required:false,type:"string",default:""}
	}>
         
    <cffunction name="init" output="no" returntype="void"
      hint="invoked after tag is constructed">
    	<cfargument name="hasEndTag" type="boolean" required="yes">
      	<cfargument name="parent" type="component" required="no" hint="the parent cfc custom tag, if there is one">
      	<cfset super.init() />
  	</cffunction> 
    
    <cffunction name="onStartTag" output="no" returntype="boolean">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">
  
		<!--- check --->
    	<cfset var hasCFC=len(trim(attributes.cfc))>
    	<cfset var hasBind=len(trim(attributes.bind))>
        <cfif hasCFC and hasBind>
        	<cfthrow message="you can not use attribute [cfc] and attribute [bind] at the same time">
        <cfelseif not hasCFC and not hasBind>
        	<cfthrow message="you must define at least one of the following attributes [cfc,bind]">
        </cfif>
				
        <cfif hasCFC>
        	<cfif len(trim(attributes.onError))>
        		<cfthrow message="in this context attribute [onError] is not allowed">
        	<cfelseif len(trim(attributes.onSuccess))>
        		<cfthrow message="in this context attribute [onSuccess] is not allowed">	
        	</cfif>
        	<cfset doCFC(argumentCollection:arguments)>
        <cfelse>
        	<cfif len(trim(attributes.jsclassname))>
        		<cfthrow message="in this context attribute [jsclassname] is not allowed">
        	<cfelseif len(trim(attributes.methods))>
        		<cfthrow message="in this context attribute [methods] is not allowed">	
        	</cfif>
        	<cfset doBind(argumentCollection:arguments)>
        </cfif>
        
        <cfreturn false>
    </cffunction>

    <cffunction name="doCFC" output="no" returntype="void">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">
   		
   		<cfset var ph = getProxyHelper() />	
		<cfset var js = "" />	
	
		<!--- 
			CONVERT CFC PATH TO REALTIVE PATH.
			Relative path need to be craeted and passed to js proxy object to perform ajax calls.
			Es: mypath.components.mycfc  TO /mypath/components/mycfc.cfc  
		--->
		<cfset cfcPath = ph.classToPath(attributes.cfc) />
		
		<!--- get the cfc metadatas filtered by remote access only --->
		<cfset meta = ph.parseMetaData(attributes.cfc,attributes.methods,attributes.extends) />

		<cfsavecontent variable="js">
			<cfoutput>									
			<script type="text/javascript">
			var _Railo_#attributes.jsclassname# = Railo.ajaxProxy.init('#cfcPath#','#attributes.jsClassName#');
			<cfloop array="#meta.functions#" index="method"><cfset args = ph.getArguments(method.parameters)/><cfset argsJson = ph.argsToJsMode(args)/>_Railo_#attributes.jsclassname#.prototype.#method.name# = function(#args#){return Railo.ajaxProxy.invokeMethod(this,'#method.name#',{#argsJson#});};
			</cfloop>
			</script>		
            </cfoutput>		
        </cfsavecontent>
		<cfset writeHeader(js,'_Railo_#attributes.jsclassname#') />

	</cffunction>
    
    <cffunction name="doBind" output="no" returntype="void">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">
		
		<cfset bind = getAjaxBinder().parseBind(bindExpr=attributes.bind,listener=attributes.onSuccess,errorHandler=attributes.onError) />
		<cfset rand = "_Railo_Bind_#randRange(1,99999999)#" />

		<cfsavecontent variable="js">
			<cfoutput>									
			<script type="text/javascript">
			#rand# = function(){
				Railo.Bind.register('_Railo_Bind_#randRange(1,99999999)#',#serializeJson(bind)#);
			}		
			Railo.Events.subscribe(#rand#,'onLoad');	
			</script>		
            </cfoutput>		
        </cfsavecontent>
		<cfset writeHeader(js,'#rand#') />

	</cffunction>

	
	<!--- Private --->

	<!---getProxyHelper--->
	<cffunction name="getProxyHelper" output="false" returntype="ajaxProxyHelper" access="private">
		<cfreturn variables.instance.proxyHelper />
	</cffunction>
	
	<!--- getAjaxBinder --->
	<cffunction name="getAjaxBinder" output="false" returntype="ajaxBinder" access="private">
		<cfreturn variables.instance.ajaxBinder />    
	</cffunction>
		
</cfcomponent>