<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	

	public void function testTagAttrApplication_cfc1(){
		http method="get" result="local.result" url="#createURL("Jira2763/index.cfm?trim=true")#" addtoken="false";
		assertEquals("-a-",trim(result.filecontent));
	}
	public void function testTagAttrApplication_cfc2(){
		http method="get" result="local.result" url="#createURL("Jira2763/index.cfm?trim=false")#" addtoken="false";
		assertEquals("- a -",trim(result.filecontent));
	}
	
	public void function testTagAttrCFApplication1(){
		application action="update" tag="#{savecontent:{trim:false}}#";
		savecontent variable="local.c" {
			echo(" a ");
		}
		assertEquals(" a ",c);
	}
	
	public void function testTagAttrCFApplication2(){
		application action="update" tag="#{savecontent:{trim:true}}#";
		savecontent variable="local.c" {
			echo(" a ");
		}
		assertEquals("a",c);
	}
	
	public void function testTagAttrCFApplication3(){
		application action="update" tag="#{cfsavecontent:{trim:true}}#";
		savecontent variable="local.c" {
			echo(" a ");
		}
		assertEquals("a",c);
	}
	
	public void function testTagAttrCFApplication4(){
		application action="update" tag="#{cfsavecontent:{trim:false}}#";
		savecontent variable="local.c" {
			echo(" a ");
		}
		assertEquals(" a ",c);
	}
	
	
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>