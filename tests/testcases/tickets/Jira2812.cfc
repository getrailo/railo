component extends="org.railo.cfml.test.RailoTestCase"	{


	function testLowerIfAllUppercase() {

		assertEquals( compare( ucFirst( "SUSI SORGOLIS", true, true ), "Susi Sorgolis" ), 0 );
		assertEquals( compare( ucFirst( "SUSI Q. SORGOLIS", true, true ), "Susi Q. Sorgolis" ), 0 );
		assertEquals( compare( ucFirst( "Susi Q. Sorgolis", true, true ), "Susi Q. Sorgolis" ), 0 );
		assertEquals( compare( ucFirst( "susi q. sorgolis", true, true ), "Susi Q. Sorgolis" ), 0 );
		assertEquals( compare( ucFirst( "Ronald McDonald", true, true ), "Ronald McDonald" ), 0 );		
		assertEquals( compare( ucFirst( "ronald mcDonald", true, true ), "Ronald McDonald" ), 0 );		
		assertEquals( compare( ucFirst( "ronald mcdonald", true, true ), "Ronald Mcdonald" ), 0 );		
	}


	function testOldFunctionality() {

		assertEquals( compare( ucFirst( "railo technologies" ), "Railo technologies" ), 0 );
		assertEquals( compare( ucFirst( "railo technologies", true ), "Railo Technologies" ), 0 );
		assertEquals( compare( ucFirst( "railo 		technologies", true ), "Railo Technologies" ), 0 );
		assertEquals( compare( ucFirst( "the 		railo   company", true ), "The Railo Company" ), 0 );
		assertEquals( compare( ucFirst( "michael offner-streit", false ), "Michael offner-streit" ), 0 );
		assertEquals( compare( ucFirst( "michael			offner-streit", true ), "Michael Offner-Streit" ), 0 );
		assertEquals( compare( ucFirst( "international  business 		machines (i.b.m.)", true ), "International Business Machines (I.B.M.)" ), 0 );
		assertEquals( compare( ucFirst( "jon doe  jr.", true ), "Jon Doe Jr." ), 0 );
	}
	
}