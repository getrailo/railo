component extends="org.railo.cfml.test.RailoTestCase"	{

	variables.CR = chr(13);

	function testNamedParams() {

		var q1 = query( name: ['Igal', 'Micha', 'Gert', 'Mark'], country: ['US', 'CH', 'CH', 'UK'] );

		var params = { name: { value: 'Micha' } };

		query name="local.q2" dbtype="query" params=params { echo("

			SELECT 	*
			FROM 	q1
			WHERE	name = :name#variables.CR#
		") };
	}
	
}