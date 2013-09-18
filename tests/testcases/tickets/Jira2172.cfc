<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cfscript>
	function setup(){
		dir=expandPath("{temp-directory}");
	}
	
	private function setOut(){
		var System=createObject('java','java.lang.System');
		var out=System.out;
		var path=dir&"f"&gettickCount()&".txt";
		var fos=createObject('java','java.io.FileOutputStream').init(path);
		var ps=createObject('java','java.io.PrintStream').init(fos);
		System.setOut(ps);
		return {out:out,ps:ps,path=path};
	}
	
	private function getOut(struct data){
		var System=createObject('java','java.lang.System');
		System.setOut(data.out);
		data.ps.close();
		var c= fileread(data.path);
		filedelete(data.path);
		return c;
	}
	</cfscript>
	
	
	<cffunction name="testCallersLocalScope" output="yes">
		<cfset var callersLocalScope = "XcallersLocalScope" />
		
		<!--- callersLocalScope --->
		<cfset var data=setOut()>
		<cftry>
			<cfdump output="console" format="text" eval=callersLocalScope  />
			<cffinally>
					<cfset content=trim(getOut(data))>
			</cffinally>
		</cftry>
		<cfset assertEquals("callersLocalScope string XcallersLocalScope",content)>
		
	</cffunction>
	
	<cffunction name="testLocalCallersLocalScope" output="yes">
		<cfset var callersLocalScope = "XcallersLocalScope" />
		
		<!--- local.callersLocalScope --->
		<cfset var data=setOut()>
		<cftry>
			<cfdump output="console" format="text" eval=local.callersLocalScope  />
			<cffinally>
					<cfset content=trim(getOut(data))>
			</cffinally>
		</cftry>
		<cfset assertEquals("local.callersLocalScope string XcallersLocalScope",content)>
		
	</cffunction>
	
	<cffunction name="testCallersArgScope" output="yes">
		<cfargument name="callersArgScope" default="XcallersArgScope" />
		
		<!--- callersArgScope --->
		<cfset var data=setOut()>
		<cftry>
			<cfdump output="console" format="text" eval=callersArgScope  />
			<cffinally>
					<cfset content=trim(getOut(data))>
			</cffinally>
		</cftry>
		<cfset assertEquals("callersargscope string xcallersargscope",content)>
	</cffunction>
	
	<cffunction name="testArgumentsCallersArgScope" output="yes">
		<cfargument name="callersArgScope" default="XcallersArgScope" />
		
		<!--- arguments.callersArgScope --->
		<cfset var data=setOut()>
		<cftry>
			<cfdump output="console" format="text" eval=arguments.callersArgScope  />
			<cffinally>
					<cfset content=trim(getOut(data))>
			</cffinally>
		</cftry>
		<cfset assertEquals("arguments.callersargscope string xcallersargscope",content)>
	</cffunction>
	
	
	<cffunction name="testCallersVarScope" output="yes">
		<cfset variables.callersVarScope="XcallersVarScope">
		
		<!--- callersVarScope --->
		<cfset var data=setOut()>
		<cftry>
			<cfdump output="console" format="text" eval=callersVarScope  />
			<cffinally>
					<cfset content=trim(getOut(data))>
			</cffinally>
		</cftry>
		<cfset assertEquals("callersVarScope string xcallersVarScope",content)>
	</cffunction>
	
	
	<cffunction name="testVariablesCallersVarScope" output="yes">
		<cfset variables.callersVarScope="XcallersVarScope">
		
		<!--- variables.callersVarScope --->
		<cfset var data=setOut()>
		<cftry>
			<cfdump output="console" format="text" eval=variables.callersVarScope  />
			<cffinally>
					<cfset content=trim(getOut(data))>
			</cffinally>
		</cftry>
		<cfset assertEquals("variables.callersVarScope string xcallersVarScope",content)>
	</cffunction>
	
	
</cfcomponent>