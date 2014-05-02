component extends="org.railo.cfml.test.RailoTestCase"	{


	function testISO8601() {

		var dt = createDateTime(2013, 12, 24, 1, 22, 33);

		var dtiso8601 = dateTimeFormat( dt, "ISO8601" );

		var date = listFirst( dtiso8601, 'T' );
		var time = listLast(  dtiso8601, 'T' );

		var dateParts = listToArray( date, '-' );

		assert( len( dateParts[1] ) == 4 );
		assert( len( dateParts[2] ) == 2 );
		assert( len( dateParts[3] ) == 2 );

		var timeParts = listToArray( listFirst( time, 'Zz+-' ), ':' );

		assert( len( timeParts[1] ) == 2 );		
		assert( len( timeParts[2] ) == 2 );		
		assert( len( timeParts[3] ) == 2 );		
	}
	
}