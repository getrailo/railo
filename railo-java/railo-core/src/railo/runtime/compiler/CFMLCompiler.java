package railo.runtime.compiler;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.TemplateException;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;


public interface CFMLCompiler {

    /**
     * compiles a CFML source file
     * @param source cfml source file
     * @param tld tag library deskriptor
     * @param fld function library deskriptor
     * @param classRootDir target directory for generated classes
     * @param className name of the class will be generated
     * @throws TemplateException
     * @throws IOException
     */
    public abstract byte[] compile(ConfigImpl config, PageSource source,
            TagLib[] tld, FunctionLib[] fld, Resource classRootDir, String className)
            throws TemplateException, IOException, railo.runtime.exp.TemplateException;

}