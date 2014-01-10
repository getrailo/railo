<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function testApplicationCFCLocale(){
		http method="get" result="local.result" url="#createURL("Jira2630/cfc/locale.cfm")#?locale=de_ch" addtoken="false" {}
		assertEquals('german (swiss)-german (swiss)-02.01.2000',trim(result.filecontent));
	
		http method="get" result="local.result" url="#createURL("Jira2630/cfc/locale.cfm")#?locale=en_us" addtoken="false" {}
		assertEquals('english (us)-english (us)-jan 2, 2000',trim(result.filecontent));
	}
	public void function testApplicationCFMLocale(){
		http method="get" result="local.result" url="#createURL("Jira2630/cfm/locale.cfm")#?locale=de_ch" addtoken="false" {}
		assertEquals('german (swiss)-german (swiss)-02.01.2000',trim(result.filecontent));
	
		http method="get" result="local.result" url="#createURL("Jira2630/cfm/locale.cfm")#?locale=en_us" addtoken="false" {}
		assertEquals('english (us)-english (us)-jan 2, 2000',trim(result.filecontent));
	}
	
	public void function testApplicationCFCTimeZone(){
		http method="get" result="local.result" url="#createURL("Jira2630/cfc/timezone.cfm")#?timezone=gmt" addtoken="false" {}
		assertEquals("gmt-gmt-{ts '2000-01-02 03:04:05'}",trim(result.filecontent));
		
		http method="get" result="local.result" url="#createURL("Jira2630/cfc/timezone.cfm")#?timezone=cet" addtoken="false" {}
		assertEquals("cet-cet-{ts '2000-01-02 04:04:05'}",trim(result.filecontent));
		
		http method="get" result="local.result" url="#createURL("Jira2630/cfc/timezone.cfm")#?timezone=pst" addtoken="false" {}
		assertEquals("pst-pst-{ts '2000-01-01 19:04:05'}",trim(result.filecontent));
	
	}
	
	public void function testApplicationCFCTimeZone(){
		http method="get" result="local.result" url="#createURL("Jira2630/cfm/timezone.cfm")#?timezone=gmt" addtoken="false" {}
		assertEquals("gmt-gmt-{ts '2000-01-02 03:04:05'}",trim(result.filecontent));
		
		http method="get" result="local.result" url="#createURL("Jira2630/cfm/timezone.cfm")#?timezone=cet" addtoken="false" {}
		assertEquals("cet-cet-{ts '2000-01-02 04:04:05'}",trim(result.filecontent));
		
		http method="get" result="local.result" url="#createURL("Jira2630/cfm/timezone.cfm")#?timezone=pst" addtoken="false" {}
		assertEquals("pst-pst-{ts '2000-01-01 19:04:05'}",trim(result.filecontent));
	
	}
	
	
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>