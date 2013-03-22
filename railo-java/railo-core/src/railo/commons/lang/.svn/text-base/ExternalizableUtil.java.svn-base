package railo.commons.lang;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ExternalizableUtil {

	public static String readString(ObjectInput in) throws ClassNotFoundException, IOException {
		return (String) in.readObject();
	}

	public static void writeString(ObjectOutput out, String str) throws IOException {
		if(str==null) out.writeObject("") ;
		else out.writeObject(str);
	}

	public static Boolean readBoolean(ObjectInput in) throws IOException {
		int b=in.readInt();
		if(b==-1) return null;
		return b==1?Boolean.TRUE:Boolean.FALSE;
	}

	public static void writeBoolean(ObjectOutput out,Boolean b) throws IOException {
		if(b==null) out.writeInt(-1) ;
		else out.writeInt(b.booleanValue()?1:0);
	}
}
