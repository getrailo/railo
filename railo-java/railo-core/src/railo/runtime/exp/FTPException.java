package railo.runtime.exp;

import org.apache.commons.net.ftp.FTPClient;

import railo.runtime.config.Config;
import railo.runtime.op.Caster;

public class FTPException extends ApplicationException {


	private int code;
	private String msg;

	public FTPException(String action, FTPClient client) {
		super("action ["+action+"] from tag ftp failed", client.getReplyString());
		//setAdditional("ReplyCode",Caster.toDouble(client.getReplyCode()));
		//setAdditional("ReplyMessage",client.getReplyString());
		code = client.getReplyCode();
		msg = client.getReplyString();
	}

	@Override
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock cb = super.getCatchBlock(config);
		cb.setEL("Cause", msg);
		cb.setEL("Code", Caster.toDouble(code));
		return cb;
	}
}
