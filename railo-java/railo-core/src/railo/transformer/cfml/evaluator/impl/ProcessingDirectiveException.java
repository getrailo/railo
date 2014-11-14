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
package railo.transformer.cfml.evaluator.impl;

import railo.runtime.exp.TemplateException;
import railo.transformer.util.CFMLString;

public final class ProcessingDirectiveException extends TemplateException {

	private String charset;
	private Boolean writeLog;
	private Boolean dotNotationUpperCase;

	public ProcessingDirectiveException(CFMLString cfml, String charset,Boolean dotNotationUpperCase, Boolean writeLog) {
		super(cfml, createMessage(cfml,charset,writeLog));
		this.charset=charset;
		this.writeLog=writeLog;
		this.dotNotationUpperCase=dotNotationUpperCase;
	}

	private static String createMessage(CFMLString cfml, String charset,boolean writeLog) {
		StringBuffer msg=new StringBuffer();
		if(!(cfml.getCharset()+"").equalsIgnoreCase(charset))
			msg.append("change charset from ["+cfml.getCharset()+"] to ["+charset+"].");
		
		if(cfml.getWriteLog()!=writeLog)
			msg.append("change writelog from ["+cfml.getWriteLog()+"] to ["+writeLog+"].");
		
		return msg.toString();
	}

	public String getCharset() {
		return charset;
	}

	public Boolean getDotNotationUpperCase() {
		return dotNotationUpperCase;
	}
	public Boolean getWriteLog() {
		return writeLog;
	}

}
