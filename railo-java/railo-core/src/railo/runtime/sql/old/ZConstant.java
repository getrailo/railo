
package railo.runtime.sql.old;




//         ZExp

public final class ZConstant
 implements ZExp
{

 public ZConstant(String s, int i) {
	 //if(s.indexOf("12:00:00")!=-1)print.ds("init:"+s);
     type_ = -1;
     val_ = null;
     val_ = new String(s);
     type_ = i;
 }

 public String getValue()
 {
     return val_;
 }

 public int getType()
 {
     return type_;
 }

 public String toString()
 {
     if(type_ == 3)
         return '\'' + val_ + '\'';
     return val_;
 }

 public static final int UNKNOWN = -1;
 public static final int COLUMNNAME = 0;
 public static final int NULL = 1;
 public static final int NUMBER = 2;
 public static final int STRING = 3;
 int type_;
 String val_;
}