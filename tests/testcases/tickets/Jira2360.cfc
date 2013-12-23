component extends="org.railo.cfml.test.RailoTestCase"	{


	function testBIF() {

		var q = query( "name": ["Micha", "Gert", "Tanja", "Mark", "Igal"], "country": ["CH", "CH", "CH", "UK", "US"] );

		var expected = {"name": "Tanja", "country": "CH"};

		assertEquals( queryRowData(q, 3), expected );
	}
	

	function testMember() {

		var q = query( "name": ["Micha", "Gert", "Tanja", "Mark", "Igal"], "country": ["CH", "CH", "CH", "UK", "US"] );

		var expected = {"name": "Micha", "country": "CH"};

		assertEquals( q.rowData(1), expected );
	}


	/**
	* @mxunit:expectedException Expression
	*/
	function testOutOfBounds() {

		var q = query( "name": ["Micha", "Gert", "Tanja", "Mark", "Igal"], "country": ["CH", "CH", "CH", "UK", "US"] );

		var expected = {"name": "Tanja", "country": "CH"};

		assertEquals( q.rowData(6), expected );
	}
	
}