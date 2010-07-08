<cfcomponent extends="railo.core.ajax.AjaxBase">

	<cfset variables.instance.ajaxBinder = createObject('component','railo.core.ajax.AjaxBinder').init() />

	<!--- Meta data --->
	<cfset this.metadata.attributetype="fixed">
    <cfset this.metadata.attributes={
		id:			{required:false,type:"string",default:""},
		bindOnLoad: {required:false,type:"boolean",default:true},		
		bind:		{required:false,type:"string",default:""},
		onBindError:{required:false,type:"string",default:""},
		tagName:	{required:false,type:"string",default:"div"}
	}/>
         
    <cffunction name="init" output="no" returntype="void" hint="invoked after tag is constructed">
    	<cfargument name="hasEndTag" type="boolean" required="yes">
      	<cfargument name="parent" type="component" required="no" hint="the parent cfc custom tag, if there is one">
		<cfset variables.hasEndTag = arguments.hasEndTag />
		<cfset super.init() />
  	</cffunction> 
    
    <cffunction name="onStartTag" output="yes" returntype="boolean">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">				

		<!--- check --->
    	<cfset var hasBind=len(trim(attributes.bind))>
    	<cfset var hasBindError=len(trim(attributes.onBindError))>
 				
        <cfif hasBindError>
        	<cfif not len(trim(attributes.bind))>
        		<cfthrow message="in this context attribute [onBindError] is not allowed">
        	</cfif>
        </cfif>
		<cfset doBind(argumentCollection=arguments)/>
		<cfoutput><#attributes.tagname# id="#attributes.id#"></cfoutput>
		<cfif not variables.hasEndTag>
			<cfoutput></#attributes.tagname#></cfoutput>
		</cfif>
	    <cfreturn variables.hasEndTag>   
	</cffunction>

    <cffunction name="onEndTag" output="yes" returntype="boolean">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">				
  		<cfargument name="generatedContent" type="string">						
			#arguments.generatedContent#</#attributes.tagname#>
		<cfreturn false/>	
	</cffunction>
	
	<!---doBind--->		   
    <cffunction name="doBind" output="no" returntype="void">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">
		
		<cfset var js = "" />				
		<cfset var bind = getAjaxBinder().parseBind(attributes.bind) />
		
		<cfif not structKeyExists(attributes,'id') or not len(trim(attributes.id))>
			<cfset attributes.id = 'railo_#randRange(1,99999999)#'/>
		</cfif>
		<cfset bind['bindTo'] = attributes.id />	
		<cfset bind['listener'] = "Railo.Ajax.innerHtml" />
		<cfset bind['errorHandler'] = attributes.onBindError />
		<cfset rand = "_Railo_Bind_#randRange(1,99999999)#" />
		<cfsavecontent variable="js"><cfoutput>
		<script type="text/javascript">
		#rand# = function(){
			Railo.Bind.register('#attributes.id#',#serializeJson(bind)#,#attributes.bindOnLoad#);
		}		
		Railo.Events.subscribe(#rand#,'onLoad');	
		</script>		
		</cfoutput></cfsavecontent> 
		<cfset writeHeader(js,'#rand#') />				
	</cffunction>

	<!--- Private --->	
	<!--- getAjaxBinder --->
	<cffunction name="getAjaxBinder" output="false" returntype="ajaxBinder" access="private">
		<cfreturn variables.instance.ajaxBinder />    
	</cffunction>
		
</cfcomponent>