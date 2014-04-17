<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="tagFunctionNon" access="private">
		<cfreturn getTickCount()>
	</cffunction>
	<cffunction name="tagFunctionCreateTimespan" cachedWithin="#createTimespan(0,0,0,0,10)#" access="private">
		<cfreturn getTickCount()>
	</cffunction>
	<cffunction name="tagFunctionDouble" cachedWithin="0.0000001" access="private">
		<cfreturn getTickCount()>
	</cffunction>
	<cffunction name="tagFunctionRequest" cachedWithin="request" access="private">
		<cfreturn getTickCount()>
	</cffunction>
	<cfscript>
	private function scriptFunctionNon() {
		return getTickCount();
	}
	private function scriptFunctionCreateTimespan() cachedWithin="#createTimespan(0,0,0,0,10)#"{ 
		return getTickCount();
	}
	private function scriptFunctionDouble() cachedWithin="0.0000001"{
		return getTickCount();
	}
	private function scriptFunctionRequest() cachedWithin="request"{
		return getTickCount();
	}
	
	function testTagFunctionNon(){
		var initVal=tagFunctionNon();
		sleep(1);
		assertEquals(false,initVal == tagFunctionNon());
	}
	
	function testScriptFunctionNon(){
		var initVal=scriptFunctionNon();
		sleep(1);
		assertEquals(false,initVal == scriptFunctionNon());
	}
	
	
	function testScriptFunctionCreateTimespan(){
		var initVal=scriptFunctionCreateTimespan();
		sleep(5);
		assertEquals(true,initVal == scriptFunctionCreateTimespan());
		sleep(10);
		assertEquals(false,initVal == scriptFunctionCreateTimespan());
	}
	
	function testTagFunctionCreateTimespan(){
		var initVal=tagFunctionCreateTimespan();
		sleep(5);
		assertEquals(true,initVal == tagFunctionCreateTimespan());
		sleep(10);
		assertEquals(false,initVal == tagFunctionCreateTimespan());
	}
	
	
	function testTagFunctionDouble(){
		var initVal=tagFunctionDouble();
		sleep(5);
		assertEquals(true,initVal == tagFunctionDouble());
		sleep(10);
		assertEquals(false,initVal == tagFunctionDouble());
	}
	function testScriptFunctionDouble(){
		var initVal=scriptFunctionDouble();
		sleep(5);
		assertEquals(true,initVal == scriptFunctionDouble());
		sleep(10);
		assertEquals(false,initVal == scriptFunctionDouble());
	}
	
	
	function testScriptFunctionRequest(){
		var initVal=scriptFunctionRequest();
		sleep(5);
		assertEquals(true,initVal == scriptFunctionRequest());
	}
	function testTagFunctionRequest(){
		var initVal=tagFunctionRequest(argumentcollection:{});
		sleep(5);
		assertEquals(true,initVal == tagFunctionRequest());
	}
	</cfscript>
	
</cfcomponent>