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
package railo.runtime.spooler.test;

import railo.runtime.config.Config;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTaskSupport;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class TestTask extends SpoolerTaskSupport {

	private int fail;
	private String label;

	public TestTask(ExecutionPlan[] plans,String label, int fail) {
		super(plans);
		this.label=label;
		this.fail=fail;
	}

	@Override
	public String getType() {
		return "test";
	}

	public Struct detail() {
		return new StructImpl();
	}

	public Object execute(Config config) throws PageException {
		//print.out("execute:"+label+":"+fail+":"+new Date());
		if(fail-->0)throw new ExpressionException("no idea");

		return null;
	}

	public String subject() {
		return label;
	}

}
