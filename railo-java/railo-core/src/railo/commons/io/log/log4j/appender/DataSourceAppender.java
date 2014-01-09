package railo.commons.io.log.log4j.appender;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

import railo.runtime.db.DataSource;

public class DataSourceAppender extends AppenderSkeleton implements Appender {
	
	private DataSource ds;

	public DataSourceAppender(DataSource ds){
		this.ds=ds;
		
	}
	
	@Override
	public void close() {
		
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}