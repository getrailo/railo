package railo.runtime.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

// TODO kann man nicht auf context ebene

/**
 * Pool to Handle Tags
 */
public final class TagHandlerPool {
	private Map map=new HashMap();
	//private static Data[] datas=new Data[100];
	
	/**
	 * return a tag to use from a class
	 * @param tagClass
	 * @return Tag
	 * @throws PageException
	 */
	public Tag use(String tagClass) throws PageException {
        Object o=map.get(tagClass);
		if(o==null) {
			Data d=new Data(tagClass);
			map.put(tagClass,d);
			return d.get();
		}
        return ((Data)o).get();
	}

	/**
	 * free a tag for reusing
	 * @param tag
	 * @throws ExpressionException
	 */
	public void reuse(Tag tag) {
		((Data)map.get(tag.getClass().getName())).free();
	}
	
	/**
	 * class to handle one tag
	 */
	private class Data {
		Class tagClass;
		Tag[] tags;
		int count=0;
		
		/**
		 * constructor of the class
		 * @param tagClass
		 * @throws PageException
		 */
		private Data(String tagClass) throws PageException {
			try {
				this.tagClass=ClassUtil.loadClass(tagClass);//Class.orName(tagClass);
			} 
			catch (ClassException e) {
				throw Caster.toPageException(e);
			}
			tags=new Tag[]{_getNewTag()};
		}
        
        private Tag _getNewTag() throws PageException {
            try {
                return (Tag) tagClass.newInstance();
            } catch (Exception e) {
                throw Caster.toPageException(e);
            }
        }

		/**
		 * free one tag
		 * @throws ExpressionException
		 */
		private void free() {
            //print.ln("free"+count);
            if(count==0)return;//throw new ExpressionException("there is no tag to get free");
			tags[--count].release();
            //if(tags.length-count>10)contract();
		}

        /**
		 * @return returns one tag from the data class
		 * @throws PageException
		 */
		private Tag get() throws PageException {
			if(tags.length==count) grow();
			return tags[count++];
		}

		/**
		 * grow the inner tag array
		 * @throws PageException
		 */
		private void grow() throws PageException {
			Tag[] nt=new Tag[tags.length+1];
			for(int i=0;i<tags.length;i++)nt[i]=tags[i];
			nt[tags.length]=_getNewTag();
			tags=nt;
		}

        private void contract() {
            //SystemOut.printDate(.getOut(),"Contract Tag Pool "+tagClass);
            Tag[] nt=new Tag[count];
            for(int i=0;i<nt.length;i++)nt[i]=tags[i];
            tags=nt;
        }

	}
    /*
    public static void main(String[] args) throws ExpressionException, PageException {
        long start;
        TagHandlerPool thp=new TagHandlerPool();
        
        start=System.currentTimeMillis();
        for(int i=0;i<1000000;i++) {
            thp.reuse(thp.use(Mail.class));
          
        }
        print.ln(System.currentTimeMillis()-start);
        
    }*/
}