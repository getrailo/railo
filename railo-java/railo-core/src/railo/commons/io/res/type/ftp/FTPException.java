

package railo.commons.io.res.type.ftp;


import java.io.IOException;

public final class FTPException extends IOException {

	public FTPException(int replyCode) {
		super("server throwed the following code "+replyCode);
	}


}
