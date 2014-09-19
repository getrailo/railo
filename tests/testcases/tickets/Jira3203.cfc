<cfscript>
component extends="org.railo.cfml.test.RailoTestCase" accessors=true	{
	property string StringProperty;
    this.setStringProperty("test");

	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){
		var props=getComponentMetaData(new Jira3203()).properties;
		assertEqualsCase(props[1].name,"StringProperty");
	}
} 
</cfscript>