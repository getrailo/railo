<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testIdConversion(){
	
		content = {'name':'Susi'};
		mongo = MongoDBConnect("test");
		mongo['test'].insert(content);
		
		
		//db.test2.insert({susi:"Sorglos"});

		
		//Get by Name
		var id = mongo['test'].findOne({'name':'Susi'}, {'_id':1});
		
		//Get by Id : fails
		var byid = mongo['test'].findOne({'_id':id});
		
		
	}
} 
</cfscript>