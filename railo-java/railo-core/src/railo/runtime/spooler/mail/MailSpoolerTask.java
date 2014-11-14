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
package railo.runtime.spooler.mail;

import javax.mail.internet.InternetAddress;

import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.net.mail.MailException;
import railo.runtime.net.smtp.SMTPClient;
import railo.runtime.op.Caster;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.ExecutionPlanImpl;
import railo.runtime.spooler.SpoolerTaskSupport;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ArrayUtil;

public class MailSpoolerTask extends SpoolerTaskSupport {
	private static final ExecutionPlan[] EXECUTION_PLANS = new ExecutionPlan[]{
		new ExecutionPlanImpl(1,60),
		new ExecutionPlanImpl(1,5*60),
		new ExecutionPlanImpl(1,3600),
		new ExecutionPlanImpl(2,24*3600),
	};
	
	
	private SMTPClient client;

	public MailSpoolerTask(ExecutionPlan[] plans,SMTPClient client) {
		super(plans);
		this.client=client;
	}
	public MailSpoolerTask(SMTPClient client) {
		this(EXECUTION_PLANS,client);
	}
	

	@Override
	public String getType() {
		return "mail";
	}

	public String subject() {
		return client.getSubject();
	}
	
	public Struct detail() {
		StructImpl sct = new StructImpl();
		sct.setEL("subject", client.getSubject());
		
		if(client.hasHTMLText())sct.setEL("body", StringUtil.max(client.getHTMLTextAsString(),1024,"..."));
		else if(client.hasPlainText())sct.setEL("body", StringUtil.max(client.getPlainTextAsString(),1024,"..."));
		
		sct.setEL("from", toString(client.getFrom()));
		
		InternetAddress[] adresses = client.getTos();
		sct.setEL("to", toString(adresses));

		adresses = client.getCcs();
		if(!ArrayUtil.isEmpty(adresses))sct.setEL("cc", toString(adresses));

		adresses = client.getBccs();
		if(!ArrayUtil.isEmpty(adresses))sct.setEL("bcc", toString(adresses));
		
		return sct;
	}

	private static String toString(InternetAddress[] adresses) {
		if(adresses==null) return "";
		
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<adresses.length;i++) {
			if(i>0)sb.append(", ");
			sb.append(toString(adresses[i]));
		}
		return sb.toString();
	}
	private static String toString(InternetAddress address) {
		if(address==null) return "";
		String addr = address.getAddress();
		String per = address.getPersonal();
		if(StringUtil.isEmpty(per)) return addr;
		if(StringUtil.isEmpty(addr)) return per;
		
		
		return per+" ("+addr+")";
	}
	public Object execute(Config config) throws PageException {
		try {
			client._send((ConfigWeb)config);
		} 
		catch (MailException e) {
			throw Caster.toPageException(e);
		}
		return null;
	}

}
