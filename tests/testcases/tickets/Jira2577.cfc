component extends="org.railo.cfml.test.RailoTestCase" {


	public void function testRandom() {
		
		Randomize( getTickCount() );

		var series1 = [];
		for ( i=0; i<5; i++ )
			series1.append( RandRange( 1000, 2000 ) );

		var series2 = [];
		for ( i=0; i<5; i++ )
			series2.append( RandRange( 1000, 2000 ) );

		assertNotEquals( series1.toList(), series2.toList() );		
	}


	public void function testSeed() {
		
		Randomize( 2577 );

		var series1 = [];
		for ( i=0; i<5; i++ )
			series1.append( RandRange( 1000, 2000 ) );

		Randomize( 2577 );

		var series2 = [];
		for ( i=0; i<5; i++ )
			series2.append( RandRange( 1000, 2000 ) );

		Randomize( getTickCount() );	// reset seed for future requests

		assertEquals( series1.toList(), series2.toList() );		
	}

}