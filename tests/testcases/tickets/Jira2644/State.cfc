component persistent="true" table="State2644" {

	property name="stateCode" length="40" ormtype="string" fieldtype="id" generator="assigned" column="stateCode";
	property name="countryCode" length="2" ormtype="string" fieldtype="id" generator="assigned" column="countryCode";
	
	property name='susi' ormtype='string'; 
	
	function dump(){
		writedump(var:variables,label:'variables');
		writedump(var:this,label:'this');
	}
}