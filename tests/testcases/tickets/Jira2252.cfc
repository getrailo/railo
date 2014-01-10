<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testQoQDistinct(){
		
		local.qry=query(
		'optionvalue':[60,53,52,55,51,54,29,69,57,58,62,34,35,33,68],
		'optiontext':['IIL Test Category ','Paris','Tokio','London','New York','München','Objekte','mra','Delta-Kategorie (für Rechtetests)','Gamma-Kategorie (für Rechtetests)','Rightstest Ausschlussobjekt','Sub2','subsub11','Sub1','<h1>Highlights</h1><script>alert(''catAlert'')</script>'],
		'lft':[3,5,7,9,11,13,15,16,18,20,22,24,25,28,31],
		'rgt':[4,6,8,10,12,14,30,17,19,21,23,27,26,29,32],
		'parent_ID':[2,2,2,2,2,2,2,29,29,29,29,29,34,29,2],
		'lvl':[2,2,2,2,2,2,2,3,3,3,3,3,4,3,2],
		'categorytext_IDtxt':['IIL Test Category ','Paris','Tokio','London','New York','München','Objekte','mra','Delta-Kategorie (für Rechtetests)','Gamma-Kategorie (für Rechtetests)','Rightstest Ausschlussobjekt','Sub2','subsub11','Sub1','<h1>Highlights</h1><script>alert(''catAlert'')</script>'],'parent_IDtxt':['Seiten- und Objekt-Kategorien','Seiten- und Objekt-Kategorien','Seiten- und Objekt-Kategorien','Seiten- und Objekt-Kategorien','Seiten- und Objekt-Kategorien','Seiten- und Objekt-Kategorien','Seiten- und Objekt-Kategorien','Objekte','Objekte','Objekte','Objekte','Objekte','Sub2','Objekte','Seiten- und Objekt-Kategorien'],
		'hasaccessrights':[0,1,1,1,0,1,1,0,0,1,0,0,1,0,0]);
		
		query name="qry" dbtype="query" {
			echo("SELECT DISTINCT optionvalue, optiontext, lft, rgt, lvl FROM qry ORDER BY lft");
		}
		
		last=-1;
		loop query="#qry#" {
			assertEquals(true,last<qry.lft);
			last=qry.lft;
		}
	}
} 
</cfscript>