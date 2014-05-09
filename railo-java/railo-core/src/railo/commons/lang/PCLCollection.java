package railo.commons.lang;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import railo.aprint;
import railo.print;
import railo.commons.io.res.Resource;
import railo.runtime.MappingImpl;
import railo.runtime.PageSourceImpl;
import railo.runtime.instrumentation.InstrumentationFactory;
import railo.runtime.type.util.StructUtil;

/**
 * Directory ClassLoader
 */
public final class PCLCollection {
    
    private final Resource directory;
    private final ClassLoader resourceCL;

	private final int maxBlockSize;
	private final MappingImpl mapping;
	private final LinkedList<PCLBlock> pclBlocks=new LinkedList<PCLBlock>();
	private LinkedList<PCLBlock> cfms=new LinkedList<PCLBlock>();
	private PCLBlock componenetBlock;
	private PCLBlock templateBlock;
	private Map<String,PCLBlock> index=new HashMap<String, PCLBlock>();

    /**
     * Constructor of the class
     * @param directory
     * @param parent
     * @throws IOException
     */
    public PCLCollection(MappingImpl mapping,Resource directory, ClassLoader resourceCL, int maxBlockSize) throws IOException {
    	// check directory
    	if(!directory.exists())
            directory.mkdirs();
        
    	if(!directory.isDirectory())
            throw new IOException("resource "+directory+" is not a directory");
        if(!directory.canRead())
            throw new IOException("no access to "+directory+" directory");
        
    	this.directory=directory;
    	this.mapping=mapping;
        //this.pcl=systemCL;
        this.resourceCL=resourceCL;
        componenetBlock=new PCLBlock(directory, resourceCL);
        pclBlocks.add(componenetBlock);
        templateBlock=new PCLBlock(directory, resourceCL);
        cfms.add(templateBlock);
        this.maxBlockSize=100;//maxBlockSize;
    }
    

    private PCLBlock current(boolean isCFC) {
    	if((isCFC?componenetBlock.count():templateBlock.count())>=maxBlockSize) {
    		synchronized (isCFC?pclBlocks:cfms) {
    			if(isCFC) {
    				componenetBlock=new PCLBlock(directory, resourceCL);
    				pclBlocks.add(componenetBlock);
    			}
    			else {
    				templateBlock=new PCLBlock(directory, resourceCL);
    				cfms.add(templateBlock);
    			}
			}
    	}
		return isCFC?componenetBlock:templateBlock;
	}
    
    

    public synchronized Class<?> loadClass(String name, byte[] barr, boolean isCFC) throws ClassNotFoundException, UnmodifiableClassException   {
    	// if class is already loaded flush the classloader and do new classloader
    	PCLBlock cl = index.get(name);
    	// update
    	if(cl!=null) {
    		Class<?> old = cl.loadClass(name);
    		InstrumentationFactory.getInstrumentation(mapping.getConfig()).redefineClasses(new ClassDefinition(old,barr));
            aprint.e("redefined::s:"+old.getName());
            return old;
    	}
    	
    	// insert
    	PCLBlock c = current(isCFC);
    	index.put(name, c);
    	return c.loadClass(name, barr);
    }

    public synchronized Class<?> getClass(PageSourceImpl ps) throws ClassNotFoundException {
    	String name=ps.getClazz();
    	PCLBlock cl = index.get(name);
    	if(cl==null) {
    		cl=current(ps.isComponent());
    		Class<?> clazz = cl.loadClass(name);
        	index.put(name, cl);
        	return clazz;
    	}
    	print.e("name:"+name);
    	return cl.loadClass(name);	
    }

    public synchronized InputStream getResourceAsStream(String name) {
        return current(false).getResourceAsStream(name);
    }

	public long count() {
		return index.size();
	}
	
	/**
	 * shrink the classloader elements
	 * @return how many page have removed from classloaders
	 */

	public synchronized int shrink(boolean force){
		int before=index.size();
		
		// CFM
		int flushCFM=0;
		while(cfms.size()>1) {
			flush(cfms.poll());
			flushCFM++;
		}
    	
		// CFC
		if(force && flushCFM<2 && pclBlocks.size()>1) {
			flush(oldest(pclBlocks));
			if(pclBlocks.size()>1)flush(pclBlocks.poll());
		}
		//print.o("shrink("+mapping.getVirtual()+"):"+(before-index.size())+">"+force+";"+(flushCFM));
    	return before-index.size();
	}

	private static PCLBlock oldest(LinkedList<PCLBlock> queue) {
		int index=NumberUtil.randomRange(0,queue.size()-2);
		return queue.remove(index);
		//return queue.poll();
	}


	private void flush(PCLBlock cl) {
		mapping.clearPages(cl);
		StructUtil.removeValue(index,cl);
		//System.gc(); gc is in Controller call, to make sure gc is only called once 
	}
}
