<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function testTypeCheckingOnAppCFC(){
		http method="get" result="local.result" url="#createURL("Jira1460/index.cfm?typeChecking=true")#" addtoken="false";
		assertEquals(500,result.status_code);
	}
	public void function testTypeCheckingOffAppCFC(){
		http method="get" result="local.result" url="#createURL("Jira1460/index.cfm?typeChecking=false")#" addtoken="false";
		assertEquals(200,result.status_code);
	}
	
	
	
	public void function testTypeCheckingOffAppTag(){
		var defaultSetting=getApplicationSettings().typeChecking;
		try{
			application action="update" typeChecking="false";
			a("susi");
			b();
		}
		finally {
			application action="update" typeChecking="#defaultSetting#";
		}
		
		//assertEquals(200,result.status_code);
	}
	
	
	public void function testTypeCheckingOnAppTag(){
		var defaultSetting=getApplicationSettings().typeChecking;
		try{
			application action="update" typeChecking="true";
			var err=false;
			try {
				a("susi");
			}
			catch(local.e){err=true;}
			assertEquals(true,err);
			
			var err=false;
			try {
				b();
				fail("must throw:casting exception");
			}
			catch(local.e){err=true;}
			assertEquals(true,err);
			
		}
		finally {
			application action="update" typeChecking="#defaultSetting#";
		}
		
		//assertEquals(200,result.status_code);
	}
	
	private void function a(boolean b){
		
	}
	private void function b() returntype="boolean" {
		return "susi";
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>