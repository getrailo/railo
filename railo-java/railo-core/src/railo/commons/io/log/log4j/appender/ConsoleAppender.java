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
package railo.commons.io.log.log4j.appender;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;

public class ConsoleAppender extends WriterAppender implements AppenderState {
	
	public ConsoleAppender() {
	}

	public ConsoleAppender(Layout layout) {
		setLayout(layout);
	}
	
	public ConsoleAppender(PrintWriter pw,Layout layout) {
		setWriter(pw);
		setLayout(layout);
	}

	public ConsoleAppender(PrintStream ps,Layout layout) {
		setWriter(new PrintWriter(ps));
		setLayout(layout);
	}



	@Override
	public boolean isClosed() {
		return closed;
	}
	
	@Override
	public synchronized void close() {
		if(isClosed()) return;
		this.closed = true;
		writeFooter();
		// reset();
	}
}