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
package railo.runtime.thread;

import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTaskSupport;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class ChildSpoolerTask extends SpoolerTaskSupport {

	private ChildThreadImpl ct;

	public ChildSpoolerTask(ChildThreadImpl ct,ExecutionPlan[] plans) {
		super(plans);
		this.ct=ct;
	}

	@Override
	public Struct detail() {
		StructImpl detail = new StructImpl();
		detail.setEL("template", ct.getTemplate());
		return detail;
	}

	public Object execute(Config config) throws PageException {
		PageException pe = ct.execute(config);
		if(pe!=null) throw pe;
		return null;
	}

	public String getType() {
		return "cfthread";
	}

	public String subject() {
		return ct.getTagName();
	}

}
