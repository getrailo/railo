component extends="org.railo.cfml.test.RailoTestCase"	{


	function testShortImpl() {

		var uuid = createUUID(true);
		var len  = len(uuid);

		assert( len >= 22 && len <= 23 );
	}


	function testOldImpl() {

		var arrUuidParts = listToArray( createUUID(), '-' );

		assert( arrUuidParts.len() == 4 );
		assert( len( arrUuidParts[1] ) == 8 );
		assert( len( arrUuidParts[2] ) == 4 );
		assert( len( arrUuidParts[3] ) == 4 );
		assert( len( arrUuidParts[4] ) == 16);
	}
	
}