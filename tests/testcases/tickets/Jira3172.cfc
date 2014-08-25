<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	variables.str="#chr(1051)##chr(1101)##chr(1075)##chr(1099)##chr(1088)##chr(1099)#";
	//public function setUp(){}

	public void function testISO88595(){
		http method="get" result="local.result" url="#createURL("Jira3172/iso_8859_5.cfm")#" charset="iso-8859-5" addtoken="false";
		assertEquals("#str#-#str#",trim(result.filecontent));
	}

	public void function testUTF8(){
		http method="get" result="local.result" url="#createURL("Jira3172/utf_8.cfm")#" charset="UTF-8" addtoken="false";
		assertEquals("#str#-#str#",trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>