/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test1(){
		var test='Testwort';
		assertEquals("VGVzdHdvcnQ=",ToBase64(test));
		assertEquals(test,toBinary(ToBase64(test))&"");
	}
	public void function test2(){
		var text="Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field, as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this.";
		var aesKey = "O6s4vNSCVL3z+cjRVNg0PA==";
		var b64CryptText2 = "BeKc405I9oeApAEQ2DQVNbeLBxe9GTTNFaALfQrTk5B+BAuVvs7SYDY518AQ7mwVxyNB2RTtsbYDbaJtV6ZWYPOpKQoM+23Vv6XH8nnxwAadZ8lf/VpI93yrY5/px6WMhE/4qpQHm1KzXSjTZjMYfVGidWlDylJLRHKfz8Sv/e9Q5aZ4sGI5FqmVoHhgX6hyhmZi/OD3HOaJE/lCUi/uZm7Fh8HEeVZ0+cWrqCB95syowc1trm73iS8Wh3ehGB71IKFwqCBGQ9j82UIfojDg3OvMGnZghauuzqGkwE3LaafYc/3D+CqL8aEOD8k9zFDeq51/ZG2WhNBgGrXlGKSLCWqiI/XAZM2dhp7YhZgDz00GEEjsXHzgCumWsiJD4OKN2caGK9UGEommVaUb7USVnM/devG8CSSU6IVl0pdDCPv9u3z5w4hiY+kTtoV8NIm8UNUM+5qWnom2+FQA/F7Wajz8l7WQkheHMJAMYCc6Z1RImuuN7d3aJJyHGoZvT4XiaSN2qNxtc7s+fOoVkKd0bD4xJVwL5JLe5fbm1fBwA9HZ/9flgHNyLyly969x51hRUF4q1GyRhKtDgHhfXlZ2tVPCd9bBgSwgHfCCqokwWoqrb7UyCQJ2YVRkMTm0t2a1p9F7gSlnRzlTZiJnvl6x17q98PHnUM04YUzR4c0Wx3WiO116lMaEJ6MiNtsndvBx5AKeCdjQHiSHgEmf+IOCpCMwWQYUs3dN+qO9tmyt+k4=";
		var plainText = decrypt(b64CryptText2, aesKey, "AES", "Base64");
		assertEquals(text,plaintext);
	}
} 
</cfscript>