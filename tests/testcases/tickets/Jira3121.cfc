<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		setLocale("hu_hu");
	}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testOrderedQuery(){

		local.src = Query(
							"id": [1,2,3,4,5],
							"name": ["Computer", "Mouse", "Áccented name", "Keyboard", "Ánother accented"],
							"category": [1,2,1,1,1]);

		query dbType="query" name="local.trg"  {
			echo("
						SELECT DISTINCT *
						FROM src
						WHERE category = 1
						ORDER BY name");
		}
	}
	public void function testUnOrderedQuery(){
		local.src = Query(
							"id": [4,5,1,3,2],
							"name": ["Áccented name", "Ánother accented", "Computer", "Keyboard", "Mouse"],
							"category": [1,1,1,1,2]);
		query dbType="query" name="local.trg"  {
			echo("
						SELECT DISTINCT *
						FROM src
						WHERE category = 1");
		}
	}
} 
</cfscript>