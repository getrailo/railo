component extends="org.railo.cfml.test.RailoTestCase" {


	public void function testDefault() {

		param name="URL.default" type="integer" default="0";

		URL.value = URL.default;
	}	


	public void function testInt() {

		var value = 1234;
		
		param name="value" type="integer";
	}


	/**
	* @mxunit:expectedException expression
	*/
	public void function testFloat() {

		var value = 1234.5;

		param name="value" type="integer";
	}


	/**
	* @mxunit:expectedException expression
	*/
	public void function testString() {

		var value = "xyz";

		param name="value" type="integer";
	}
}