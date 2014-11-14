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


component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		
	}
	
	
	public void function test(){
	}

	
	public void function testThreads(){
	
		thread name="l1" {
			thread name="l2" {
				thread name="l3" {
					thread name="l4" {
						thread name="l5" {
						
						}
						thread action="join" names="l5";
					}
					thread action="join" names="l4";
				}
				thread action="join" names="l3";
			}
			thread action="join" names="l2";
		}
		thread action="join" names="l1";
		//dump(cfthread)
		//abort;
		assertEquals("COMPLETED",l1.STATUS);
		
		
		
	}
	
} 