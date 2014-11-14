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