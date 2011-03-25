<cfcomponent>
	
    
    <cfset state="stopped">
	
	<cffunction name="init" access="public" output="no" returntype="void">
		<cfargument name="id" required="false" type="string">
		<cfargument name="config" required="false" type="struct">
		<cfargument name="listener" required="false" type="component">
    	<cfset variables.id=id>
        <cfset variables.config=config>
        <cfset variables.listener=listener>
        
        <cflog text="init" type="information" file="DirectoryWatcher">
        
	</cffunction>


	<cffunction name="start" access="public" output="no" returntype="void">
		<cfwhile state EQ "stopping">
        	<cfset sleep(10)>
        </cfwhile>
        <cfset variables.state="running">
        
        
        <cflog text="start" type="information" file="DirectoryWatcher">
		<cfset var funcNames={add:config.addFunction, change:config.changeFunction, delete:config.deleteFunction}>
        
        
		<!--- check --->
        <cfif not DirectoryExists(config.directory)>
        	<cflog text="Directory [#config.directory#] does not exists or is not a directory" type="Error" file="DirectoryWatcher">
        </cfif>
        <cfif not StructKeyExists(config,"recurse")>
        	<cfset config.recurse=false>
        </cfif>
        
        
        <cfset var files=loadFiles(config.directory, config.recurse, config.extensions)>
        <!--- first execution --->
        <cfwhile variables.state EQ "running">
        	<cftry>
				<cfset var coll=compareFiles(files,funcNames,config.directory, config.recurse, config.extensions)>
                <cfset files=coll.data>
                <cfset var name="">
                <cfset var funcName="">
                <cfloop collection="#coll.diff#" item="name">
                    <cfset funcName=coll.diff[name].action>
                    <cfif len(trim(funcName))><cfset variables.listener[funcName](coll.diff[name])></cfif>
                </cfloop>
                <cfcatch>
                	<cflog text="#cfcatch.message#" type="Error" file="DirectoryWatcher">
                </cfcatch>
            </cftry>
            <cfif variables.state NEQ "running">
            	<cfbreak>
            </cfif>
            <cfset sleep(config.interval)>
    	</cfwhile>
        <cfset variables.state="stopped">
        
	</cffunction>
    
    
	<cffunction name="loadFiles" access="private" output="no" returntype="struct">
    	<cfargument name="directory" type="string" required="yes">
    	<cfargument name="recurse" type="boolean" required="no" default="#false#">
    	<cfargument name="extensions" type="string" required="no" default="*">
    	
        <cfset var dir="">
        <cfset variables._filter=cleanExtensions(arguments.extensions)>
        <cfdirectory directory="#arguments.directory#" action="list" name="dir" filter="#filter#" recurse="#arguments.recurse#">
        <cfset var sct={}>
        <cfloop query="dir">
        	<cfif dir.type EQ "file">
				<cfset sct[dir.directory&server.separator.file&dir.name]=createElement(dir)>
            </cfif>
        </cfloop>
        <cfreturn sct>
    </cffunction>

	
    <cffunction name="compareFiles" access="private" output="no" returntype="struct">
    	<cfargument name="last" type="struct" required="yes">
    	<cfargument name="funcNames" type="struct" required="yes">
    	<cfargument name="directory" type="string" required="yes">
    	<cfargument name="recurse" type="boolean" required="no" default="#false#">
    	<cfargument name="extensions" type="string" required="no" default="*">
    	
        <cfset var dir="">
        <cfset variables._filter=cleanExtensions(arguments.extensions)>
        <cfdirectory directory="#arguments.directory#" action="list" name="dir" filter="#filter#" recurse="#arguments.recurse#">
        <cfset var sct={}>
        <cfset var diff={}>
        <cfset var name="">
        <cfset var tmp="">
        <cfloop query="dir">
        	<cfif dir.type EQ "file">
            	<cfset name=dir.directory&server.separator.file&dir.name>
				<cfset sct[name]=createElement(dir)>
				<cfif StructKeyExists(last,name)>
                	<cfif dir.dateLastModified NEQ last[name].dateLastModified>
                    	<cfset tmp=createElement(dir)>
                        <cfset tmp.action=funcNames.change>
						<cfset diff[name]=tmp>
					</cfif>
				<cfelse>
                	<cfset tmp=createElement(dir)>
                    <cfset tmp.action=funcNames.add>
					<cfset diff[name]=tmp>
				</cfif>
            </cfif>
        </cfloop>
        
        <cfloop collection="#last#" item="name">
        	<cfif not StructKeyExists(sct,name)>
            	<cfset last[name].action=funcNames.delete>
            	<cfset diff[name]=last[name]>
            </cfif>
        </cfloop>
       
        <cfreturn {data:sct,diff:diff}>
    </cffunction>
    
    
    
    <cffunction name="createElement" access="private" output="no" returntype="struct">
    	<cfargument name="dir" type="query" required="yes">
        <cfreturn {dateLastModified:dir.dateLastModified, size:dir.size, name:dir.name, directory:dir.directory,id:variables.id}>
    </cffunction>
    

	<cffunction name="stop" access="public" output="no" returntype="void">
    	<cflog text="stop" type="information" file="DirectoryWatcher">
		<cfset variables.state="stopping">
	</cffunction>

	<cffunction name="restart" access="public" output="no" returntype="void">
		<cfif state EQ "running"><cfset stop()></cfif>
        <cfset start()>
	</cffunction>

	<cffunction name="getState" access="public" output="no" returntype="string">
		<cfreturn state>
	</cffunction>

	<cffunction name="sendMessage" access="public" output="no" returntype="string">
		<cfargument name="data" required="false" type="struct">
		<cfreturn "ERROR: sendMessage not supported">
	</cffunction>
    
    
	<cffunction name="cleanExtensions" access="private" output="no" returntype="array">
		<cfargument name="extensions" required="true" type="string">
		<cfset arguments.extensions=trim(arguments.extensions)>
        <cfif len(arguments.extensions) EQ 0 or arguments.extensions EQ "*"><cfreturn []></cfif>
        <cfset var ext="">
        <cfset var arr=[]>
        <cfloop list="#arguments.extensions#" index="ext">
        	<cfset ext=trim(ext)>
            <cfif ext EQ "*"><cfreturn []></cfif>
            
            <!--- remove *. --->
			<cfif left(ext,2) EQ "*.">
				<cfset ext=trim(mid(ext,3,len(ext)))>
            <cfelseif left(ext,1) EQ ".">
				<cfset ext=trim(mid(ext,2,len(ext)))>
            </cfif>
            
            <cfset ArrayAppend(arr,ext)>
        </cfloop>
        <cfreturn arr>
	</cffunction>
    
    <cffunction name="filter" output="no" access="private">
        <cfargument name="path">
        <cfset var ext="">
        <cfif arrayLen(variables._filter) EQ 0><cfreturn true></cfif>
        <cfloop array="#variables._filter#" index="ext">
            <cfif right(arguments.path,len(ext)+1) EQ "."&ext>
                <cfreturn true>
            </cfif>
        </cfloop>
        <cfreturn false>
    </cffunction>

    
    

</cfcomponent>