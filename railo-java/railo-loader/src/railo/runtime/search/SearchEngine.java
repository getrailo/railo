package railo.runtime.search;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import railo.commons.io.res.Resource;
import railo.runtime.config.Config;
import railo.runtime.type.Query;

/**
 * interface for a Search Engine
 */
public interface SearchEngine {

    /**
     * overwrite allowed
     */
    public static final boolean ALLOW_OVERWRITE = true;

    /**
     * overwrite denied
     */
    public static final boolean DENY_OVERWRITE = false;

    /**
     * constructor of the class
     * @param config 
     * @param searchDir directory where the railo xml file is
     * @param log 
     * @throws IOException
     * @throws SAXException
     * @throws SearchException
     */
    public abstract void init(Config config, Resource searchDir) 
            throws SAXException, IOException, SearchException;

    /**
     * returns a collection by name
     * @param name name of the desired collection (case insensitive)
     * @return returns lucene collection object matching name
     * @throws SearchException if no matching Collection exist 
     */
    public abstract SearchCollection getCollectionByName(String name)
            throws SearchException;

    /**
     * @return returns all collections as a query object
     */
    public abstract Query getCollectionsAsQuery();

    /**
     * Creates a new Collection and Store it (creating always a spellindex)
     * @param name The Name of the Collection
     * @param path the path to store
     * @param language The language of the collection
     * @param allowOverwrite
     * @return New SearchCollection
     * @throws SearchException
     */
    public abstract SearchCollection createCollection(String name, Resource path,
            String language, boolean allowOverwrite) throws SearchException;


    /**
     * @return returns the directory of the search storage
     */
    public abstract Resource getDirectory();

    /**
     * return XML Element Matching index id
     * @param collElement XML Collection Element
     * @param id
     * @return XML Element
     */
    public abstract Element getIndexElement(Element collElement, String id);

    /**
     * @return returns the Name of the search engine to display in admin
     */
    public abstract String getDisplayName();

}