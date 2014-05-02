<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function test() localmode="true"{
		http method="get" result="local.result"  url="#createURL("Jira3020/index.cfm")#" addtoken="false"{
			httpparam type="cookie" name="cfId" value="#cookie.cfid#";
			httpparam type="cookie" name="cfToken" value="#cookie.cftoken#";
			httpparam type="cookie" name="cfiD" value="1234";
			httpparam type="cookie" name="cftokeN" value="1234";
			
		}
		qry=result.cookies;
		loop query="#qry#" {
			// invalid case for cfid
			if(compare(qry.name,"cfId")==0 || compare(qry.name,"cfiD")==0) {
				assertEquals("",qry.value);
				assertEquals(createDate(1970,1,1,"UTC"),qry.expires);
			}
			// valid case for cfid
			else if(compare(qry.name,"cfid")==0) {
				assertEquals(cookie.cfid,qry.value);
				assertEquals(true,qry.expires>now());
			}
			// invalid case for cfid
			else if(compare(qry.name,"cfToken")==0 || compare(qry.name,"cftokeN")==0) {
				assertEquals("",qry.value);
				assertEquals(createDate(1970,1,1,"UTC"),qry.expires);
			}
			// valid case for cfid
			else if(compare(qry.name,"cftoken")==0) {
				assertEquals(cookie.cftoken,qry.value);
				assertEquals(true,qry.expires>now());
			}
		}
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>