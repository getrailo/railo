<cfcomponent>

	<cffunction name="loadInstallCFC" output="no" returntype="struct">
    	<cfargument name="path" type="string">
        
 		<cfset var virtual='/install'>
        <cfset var mappings[virtual]=path>
        <cfapplication action="update" mappings="#mappings#">
        
        <cfset var key=hash(path&GetFileInfo(path).lastmodified)>
        
        <cfif StructKeyExists(session,'extMan')>
        	<cfif StructKeyExists(session.extMan,key)>
                <cfreturn session.extMan[key]>
            </cfif>
        <cfelse>
        	<cfset session.extMan=struct()>
        </cfif>
        
        
		
        <cfset session.extMan[key]=createObject('component',virtual&"/Install")>
        <cfif StructKeyExists(session.extMan[key],'init')>
        	<cfset session.extMan[key].init(path)>
        </cfif>
        <cfreturn session.extMan[key]>
    </cffunction>
    
	<cffunction name="createConfig" output="no" returntype="struct">
    	
        <cfset var key="">
        <cfset var name="">
        <cfset var step="">
        
		<cfset var config=struct()>
		<cfset config.mixed=struct()>
        <cfset config.steps=array()>
        
        <cfif StructKeyExists(form,'fields')>
            <cfloop index="key" list="#form.fields#">
                <cfif len(key) gt 3 and left(key,3) EQ "dyn">
                    <cfset name=mid(key,1+find('_',key),10000)>
                    <cfset step=mid(key,4,find('_',key)-4)>
                    <cfset config.steps[step][name]="">
                    <cfset config.mixed[name]="">
                </cfif>
            </cfloop>
        </cfif>
        
        
        <cfloop item="key" collection="#form#">
            <cfif len(key) gt 3 and left(key,3) EQ "dyn">
                <cfset name=mid(key,1+find('_',key),10000)>
                <cfset step=mid(key,4,find('_',key)-4)>
                <cfset config.steps[step][name]=form[key]>
                <cfset config.mixed[name]=form[key]>
            </cfif>
        </cfloop>
        
        <cfreturn config>
    </cffunction>

	<cffunction name="createUIDFolder" output="no"
    	hint="create a new step cfc">
    	<cfargument name="uid" type="string">
        
        <cfset var info="">
        <cfset var data.directory="">
        <cfadmin 
            action="getExtensionInfo"
            type="#request.adminType#"
            password="#session["password"&request.adminType]#"
            returnVariable="info">
        <cfset data.directory=info.directory>
        
        <!--- create directory --->
		<cfset var dest=data.directory>
        <cfif not DirectoryExists(dest)>
            <cfdirectory directory="#dest#" action="create" mode="777">
        </cfif>
        
        <!--- uid --->
        <cfset dest=dest&"/"&arguments.uid>
        <cfif not DirectoryExists(dest)>
            <cfdirectory directory="#dest#" action="create" mode="777">
        </cfif>
        
        <cfreturn dest>
    </cffunction>
    
	<!---<cffunction name="createAppFolder" output="no"
    	hint="create a new step cfc">
    	<cfargument name="provider" type="string">
    	<cfargument name="app" type="string">
        
        <cfset var info="">
        <cfset var data.directory="">
        <cfadmin 
            action="getExtensionInfo"
            type="#request.adminType#"
            password="#session["password"&request.adminType]#"
            returnVariable="info">
        <cfset data.directory=info.directory>
        
        <!--- create directory --->
		<cfset var dest=data.directory>
        <cfif not DirectoryExists(dest)>
            <cfdirectory directory="#dest#" action="create" mode="777">
        </cfif>
        
        <!--- provider folder --->
        <cfset dest=dest&"/"&arguments.provider>
        <cfif not DirectoryExists(dest)>
            <cfdirectory directory="#dest#" action="create" mode="777">
        </cfif>
        
        <!--- app folder --->
        <cfset dest=dest&"/"&arguments.app>
        <cfif not DirectoryExists(dest)>
            <cfdirectory directory="#dest#" action="create" mode="777">
        </cfif>
        <cfreturn dest>
    </cffunction>--->
	
	<cffunction name="copyAppFile" output="no"
    	hint="create a new step cfc">
    	<cfargument name="app" type="any">
    	<cfargument name="dest" type="string">
        
        <cfsetting requesttimeout="1000000">
        <cfset var rtn=struct()>
        <cfset var serialNumber="">
		<cfset var destFile=arguments.dest&"/"&arguments.app.version&".rep">
        <cftry>
        	<cfadmin 
                    action="getSerial"
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    returnVariable="serialNumber">
            <cfif len(serialNumber)>
				<cfset serialNumber=Encrypt(serialNumber,"wfsfr456","cfmx_compat","Hex")>
			</cfif>
        	<cfcatch>
            <cfset serialNumber="">
            </cfcatch>
        </cftry>
		<cfif not FileExists(destFile)>
        
			<!--- static download --->
            <cfif isDefined('app.download') and len(trim(app.download))>
                <cffile action="copy" source="#app.download#" destination="#destFile#" mode="777">
            <cfelse>
            	<cfset var rtn=request.getDownloadDetails(hash(app.provider),request.admintype,getRailoId()['server'].id,getRailoId()['web'].id,app.id,serialNumber)>
                <cfif isDefined('rtn.url') and len(rtn.url)>
                	<cffile action="copy" source="#rtn.url#" destination="#destFile#" mode="777">
                    <cfset rtn.message="">
				</cfif>
            </cfif>
        </cfif>
        <cfset rtn.destFile=destFile>
        <cfif not StructKeyExists(rtn,'error')><cfset rtn.error=false></cfif>
        <cfif not StructKeyExists(rtn,'message')><cfset rtn.message=""></cfif>
        
        <cfreturn rtn>
    </cffunction>
    



    
	<cffunction name="translateXML" output="no"
    	hint="create a new step cfc">
    	<cfargument name="install" type="component" hint="install cfc from app">
    	<cfargument name="config" type="struct" hint="struct of existing config data">
    	<cfargument name="xmlConfig" type="xml" hint="config xml file loaded">
        
        
        <cfset var step="">
        <cfset var stepIndex="">
        <cfset var stepItem="">
        <cfset var stepAttrs="">
        
		<cfset var group="">
        <cfset var groupIndex="">
        <cfset var groupItem="">
        <cfset var groupAttrs="">
        
        
        <cfset var extForm=createObject('component','ExtensionConfig')>
		<cfset var attr=xmlConfig.xmlAttributes>
		
		<!--- dynamic --->
		<cfif StructKeyExists(attr,'dynamic')>
            <cfset install[attr['dynamic']](extForm,config)>
        </cfif>


		<!--- loop steps --->
		<cfloop from="1" to="#arrayLen(xmlConfig.XmlChildren)#" index="stepIndex">
			<!--- read group data --->
    		<cfset stepItem=xmlConfig.XmlChildren[stepIndex]>
    		<cfset stepAttrs=xmlConfig.XmlChildren[stepIndex].xmlAttributes>
	
			<!--- Ignore the info block in the config --->
			<cfif stepItem.XMLName EQ "info">
				<cfcontinue>
			</cfif>
    
			<cfif not StructKeyExists(stepAttrs,'label')><cfset stepAttrs.label=""></cfif>
    		<cfif not StructKeyExists(stepAttrs,'description')><cfset stepAttrs.description=""></cfif>
    		<cfset step=extForm.createStep(label:stepAttrs.label,description:stepAttrs.description)>

			<!--- dynamic --->
			<cfif StructKeyExists(stepAttrs,'dynamic')>
                <cfset install[stepAttrs['dynamic']](step,config)>
                <cfcontinue>
            </cfif>



			<!--- loop groups --->
    		<cfloop from="1" to="#arrayLen(stepItem.XmlChildren)#" index="groupIndex">
        
        		<!--- read group data --->
        		<cfset groupItem=stepItem.XmlChildren[groupIndex]>
        		<cfset groupAttrs=stepItem.XmlChildren[groupIndex].xmlAttributes>
        
				<cfif not StructKeyExists(groupAttrs,'label')><h2><cfset groupAttrs.label=""></cfif>
                <cfif not StructKeyExists(groupAttrs,'description')><cfset groupAttrs.description=""></cfif>
                <cfset group=step.createGroup(label:groupAttrs.label,description:groupAttrs.description)>
                
                <!--- dynamic --->
				<cfif StructKeyExists(groupAttrs,'dynamic')>
                    <cfset install[groupAttrs['dynamic']](group,config)>
                    <cfcontinue>
                </cfif>
                
                
                <!--- loop item  --->
                <cfset var item="">
                <cfset var itemAttrs="">
                <cfset var itemText="">
                
                <cfloop from="1" to="#arrayLen(groupItem.XmlChildren)#" index="idx">
                    <cfset itemAttrs=groupItem.XmlChildren[idx].xmlAttributes>
                    <cfset var text=groupItem.XmlChildren[idx].XmlText>
                    <cfset var children=groupItem.XmlChildren[idx].XmlChildren>
                    <cfset var doBR=true>
                    
                    
                    
                    <!--- full-dynamic --->
                    <cfif StructKeyExists(itemAttrs,'full-dynamic')>
                        <cfset install[itemAttrs['full-dynamic']](group,config)>
                        <cfcontinue>
                    </cfif>
                    
                    <!--- type --->
                    <cfset type="text">
                    <cfif StructKeyExists(itemAttrs,'type')>
                        <cfset type=itemAttrs.type>
                    </cfif>
                    
                    <!--- value --->
                    <cfset var value="">
                    <cfif not len(text)>
                        <cfset value="">
                    <cfelseif left(trim(text),9) EQ 'evaluate:'>
                        <cfset value=evaluate(mid(trim(text),10,10000))>
                    <cfelse>
                        <cfset value=trim(text)>
                    </cfif>
                    
                    <!--- name --->
                    <cfset name="">
                    <cfif StructKeyExists(itemAttrs,'name')><cfset name=itemAttrs.name></cfif>
                    <!--- label --->
                    <cfset label="">
                    <cfif StructKeyExists(itemAttrs,'label')><cfset label=itemAttrs.label></cfif>
                    <!--- selected --->
                    <cfset selected=
                            (structKeyExists(itemAttrs,'selected') and itemAttrs.selected) or 
                            (structKeyExists(itemAttrs,'checked') and itemAttrs.checked)>
                    <!--- description --->
                    <cfset description="">
                    <cfif StructKeyExists(itemAttrs,'description')><cfset description=itemAttrs.description></cfif>
                    
                    <cfset item=group.createItem(type:type,name:name,value:value,label:label,selected:selected,description:description)>
                    
                    <!--- dynamic --->
                    <cfif StructKeyExists(itemAttrs,'dynamic')>
                    	
                        <cfset v="">
                        <cfif StructKeyExists(config,itemAttrs.name)>
                            <cfset v=config[itemAttrs.name]>
                        </cfif>
                        
                        <cfset install[itemAttrs.dynamic](item,v)>
                        <cfcontinue>
                    </cfif>
                    <cfset var idy="">
                    <cfloop from="1" to="#arrayLen(children)#" index="idy">
        				<cfset translateOption(item,children[idy])>
                    </cfloop>
                </cfloop>
            </cfloop>
        </cfloop>
        
        
        
        
        
        
    	<cfreturn extForm>
    </cffunction>
	<cffunction name="translateOption" access="private"
    	hint="create a new step cfc">
    	<cfargument name="item">
    	<cfargument name="xmlOption">
        
		<!--- label --->
        <cfset var label="">
        <cfif structKeyExists(xmlOption.XmlAttributes,'label')>
            <cfset label=xmlOption.XmlAttributes.label>
        <cfelse>
            <cfset label=xmlOption.XmlText>
        </cfif>
		<!--- description --->
        <cfset var description="">
        <cfif structKeyExists(xmlOption.XmlAttributes,'description')>
            <cfset description=xmlOption.XmlAttributes.description>
        <cfelse>
            <cfset description=xmlOption.XmlText>
        </cfif>
        <!--- vakue --->
        <cfset var value="">
        <cfif structKeyExists(xmlOption.XmlAttributes,'value')>
            <cfset value=xmlOption.XmlAttributes.value>
        <cfelse>
            <cfset value=xmlOption.XmlText>
        </cfif>
        
        <!--- selected --->
        <cfset var selected=
                (structKeyExists(xmlOption.XmlAttributes,'selected') and xmlOption.XmlAttributes.selected) or 
                (structKeyExists(xmlOption.XmlAttributes,'checked') and xmlOption.XmlAttributes.checked)>
       	
		<!--- create option --->
       	<cfset item.createOption(value:value,label:label,description:description,selected:selected)>
	</cffunction>
    
    
</cfcomponent>