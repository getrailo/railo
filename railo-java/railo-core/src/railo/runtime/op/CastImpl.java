package railo.runtime.op;

import java.awt.Color;
import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import railo.commons.color.ColorCaster;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.util.Cast;

/**
 * Implementation of the cast interface
 */
public final class CastImpl implements Cast {

    private static CastImpl singelton;

    /**
     * @see railo.runtime.util.Cast#castTo(railo.runtime.PageContext, short, java.lang.Object)
     */
    public Object castTo(PageContext pc, short type, Object o) throws PageException {
        return Caster.castTo(pc,type,o);
    }

    /**
     * @see railo.runtime.util.Cast#castTo(railo.runtime.PageContext, short, java.lang.String, java.lang.Object)
     */
    public Object castTo(PageContext pc, short type, String strType, Object o) throws PageException {
        return Caster.castTo(pc,type,strType,o);
    }

    /**
     * @see railo.runtime.util.Cast#castTo(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object castTo(PageContext pc, String type, Object o) throws PageException {
        return Caster.castTo(pc,type,o,false);
    }
    public Object castTo(PageContext pc, String type, Object o, boolean alsoPattern) throws PageException {
        return Caster.castTo(pc,type,o,alsoPattern);
    }

    /**
     * @see railo.runtime.util.Cast#toArray(java.lang.Object, railo.runtime.type.Array)
     */
    public Array toArray(Object obj, Array defaultValue) {
        return Caster.toArray(obj,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toArray(java.lang.Object)
     */
    public Array toArray(Object obj) throws PageException {
        return Caster.toArray(obj);
    }

    /**
     * @see railo.runtime.util.Cast#toBase64(java.lang.Object, java.lang.String)
     */
    public String toBase64(Object o, String defaultValue) {
        return Caster.toBase64(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toBase64(java.lang.Object)
     */
    public String toBase64(Object o) throws PageException {
        return Caster.toBase64(o);
    }

    /**
     * @see railo.runtime.util.Cast#toBinary(java.lang.Object, byte[])
     */
    public byte[] toBinary(Object obj, byte[] defaultValue) {
        return Caster.toBinary(obj,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toBinary(java.lang.Object)
     */
    public byte[] toBinary(Object obj) throws PageException {
        return Caster.toBinary(obj);
    }

    /**
     * @see railo.runtime.util.Cast#toBoolean(boolean)
     */
    public Boolean toBoolean(boolean b) {
        return Caster.toBoolean(b);
    }

    /**
     * @see railo.runtime.util.Cast#toBoolean(char)
     */
    public Boolean toBoolean(char c) {
        return Caster.toBoolean(c);
    }

    /**
     * @see railo.runtime.util.Cast#toBoolean(double)
     */
    public Boolean toBoolean(double d) {
        return Caster.toBoolean(d);
    }

    /**
     * @see railo.runtime.util.Cast#toBoolean(java.lang.Object, java.lang.Boolean)
     */
    public Boolean toBoolean(Object o, Boolean defaultValue) {
        return Caster.toBoolean(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toBoolean(java.lang.Object)
     */
    public Boolean toBoolean(Object o) throws PageException {
        return Caster.toBoolean(o);
    }

    /**
     * @see railo.runtime.util.Cast#toBoolean(java.lang.String, java.lang.Boolean)
     */
    public Boolean toBoolean(String str, Boolean defaultValue) {
        return Caster.toBoolean(str,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toBoolean(java.lang.String)
     */
    public Boolean toBoolean(String str) throws PageException {
        return Caster.toBoolean(str);
    }

    /**
     * @see railo.runtime.util.Cast#toBooleanValue(boolean)
     */
    public boolean toBooleanValue(boolean b) {
        return Caster.toBooleanValue(b);
    }

    /**
     * @see railo.runtime.util.Cast#toBooleanValue(char)
     */
    public boolean toBooleanValue(char c) {
        return Caster.toBooleanValue(c);
    }

    /**
     * @see railo.runtime.util.Cast#toBooleanValue(double)
     */
    public boolean toBooleanValue(double d) {
        return Caster.toBooleanValue(d);
    }

    /**
     * @see railo.runtime.util.Cast#toBooleanValue(java.lang.Object, boolean)
     */
    public boolean toBooleanValue(Object o, boolean defaultValue) {
        return Caster.toBooleanValue(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toBooleanValue(java.lang.Object)
     */
    public boolean toBooleanValue(Object o) throws PageException {
        return Caster.toBooleanValue(o);
    }

    /**
     * @see railo.runtime.util.Cast#toBooleanValue(java.lang.String, boolean)
     */
    public boolean toBooleanValue(String str, boolean defaultValue) {
        return Caster.toBooleanValue(str,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toBooleanValue(java.lang.String)
     */
    public boolean toBooleanValue(String str) throws PageException {
        return Caster.toBooleanValue(str);
    }

    /**
     * @see railo.runtime.util.Cast#toByte(boolean)
     */
    public Byte toByte(boolean b) {
        return Caster.toByte(b);
    }

    /**
     * @see railo.runtime.util.Cast#toByte(char)
     */
    public Byte toByte(char c) {
        return Caster.toByte(c);
    }

    /**
     * @see railo.runtime.util.Cast#toByte(double)
     */
    public Byte toByte(double d) {
        return Caster.toByte(d);
    }

    /**
     * @see railo.runtime.util.Cast#toByte(java.lang.Object, java.lang.Byte)
     */
    public Byte toByte(Object o, Byte defaultValue) {
        return Caster.toByte(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toByte(java.lang.Object)
     */
    public Byte toByte(Object o) throws PageException {
        return Caster.toByte(o);
    }

    /**
     * @see railo.runtime.util.Cast#toByteValue(boolean)
     */
    public byte toByteValue(boolean b) {
        return Caster.toByteValue(b);
    }

    /**
     * @see railo.runtime.util.Cast#toByteValue(char)
     */
    public byte toByteValue(char c) {
        return Caster.toByteValue(c);
    }

    /**
     * @see railo.runtime.util.Cast#toByteValue(double)
     */
    public byte toByteValue(double d) {
        return Caster.toByteValue(d);
    }

    /**
     * @see railo.runtime.util.Cast#toByteValue(java.lang.Object, byte)
     */
    public byte toByteValue(Object o, byte defaultValue) {
        return Caster.toByteValue(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toByteValue(java.lang.Object)
     */
    public byte toByteValue(Object o) throws PageException {
        return Caster.toByteValue(o);
    }

    /**
     * @see railo.runtime.util.Cast#toCharacter(boolean)
     */
    public Character toCharacter(boolean b) {
        return Caster.toCharacter(b);
    }

    /**
     * @see railo.runtime.util.Cast#toCharacter(char)
     */
    public Character toCharacter(char c) {
        return Caster.toCharacter(c);
    }

    /**
     * @see railo.runtime.util.Cast#toCharacter(double)
     */
    public Character toCharacter(double d) {
        return Caster.toCharacter(d);
    }

    /**
     * @see railo.runtime.util.Cast#toCharacter(java.lang.Object, java.lang.Character)
     */
    public Character toCharacter(Object o, Character defaultValue) {
        return Caster.toCharacter(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toCharacter(java.lang.Object)
     */
    public Character toCharacter(Object o) throws PageException {
        return Caster.toCharacter(o);
    }

    /**
     * @see railo.runtime.util.Cast#toCharValue(boolean)
     */
    public char toCharValue(boolean b) {
        return Caster.toCharValue(b);
    }

    /**
     * @see railo.runtime.util.Cast#toCharValue(char)
     */
    public char toCharValue(char c) {
        return Caster.toCharValue(c);
    }

    /**
     * @see railo.runtime.util.Cast#toCharValue(double)
     */
    public char toCharValue(double d) {
        return Caster.toCharValue(d);
    }

    /**
     * @see railo.runtime.util.Cast#toCharValue(java.lang.Object, char)
     */
    public char toCharValue(Object o, char defaultValue) {
        return Caster.toCharValue(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toCharValue(java.lang.Object)
     */
    public char toCharValue(Object o) throws PageException {
        return Caster.toCharValue(o);
    }

    /**
     * @see railo.runtime.util.Cast#toCollection(java.lang.Object, railo.runtime.type.Collection)
     */
    public Collection toCollection(Object o, Collection defaultValue) {
        return Caster.toCollection(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toCollection(java.lang.Object)
     */
    public Collection toCollection(Object o) throws PageException {
        return Caster.toCollection(o);
    }
    
    /**
     * @see railo.runtime.util.Cast#toColor(java.lang.Object)
     */
    public Color toColor(Object o) throws PageException {
    	if(o instanceof Color) return (Color) o;
    	else if (o instanceof String)ColorCaster.toColor((String)o);
    	else if (o instanceof Number)ColorCaster.toColor(Integer.toHexString(((Number)o).intValue()));
    	throw new CasterException(o,Color.class);
    }

    
    /**
     * @see railo.runtime.util.Cast#toDate(boolean, java.util.TimeZone)
     */
    public DateTime toDate(boolean b, TimeZone tz) {
        return Caster.toDate(b,tz);
    }

    /**
     * @see railo.runtime.util.Cast#toDate(char, java.util.TimeZone)
     */
    public DateTime toDate(char c, TimeZone tz) {
        return Caster.toDate(c,tz);
    }

    /**
     * @see railo.runtime.util.Cast#toDate(double, java.util.TimeZone)
     */
    public DateTime toDate(double d, TimeZone tz) {
        return Caster.toDate(d,tz);
    }

    /**
     * @see railo.runtime.util.Cast#toDate(java.util.Locale, java.lang.String, java.util.TimeZone, railo.runtime.type.dt.DateTime)
     */
    public DateTime toDate(Locale locale, String str, TimeZone tz, DateTime defaultValue) {
        return Caster.toDateTime(locale,str,tz,defaultValue,true);
    }

    /**
     * @see railo.runtime.util.Cast#toDate(java.util.Locale, java.lang.String, java.util.TimeZone)
     */
    public DateTime toDate(Locale locale, String str, TimeZone tz) throws PageException {
        return Caster.toDateTime(locale,str,tz,true);
    }

    /**
     * @see railo.runtime.util.Cast#toDate(java.lang.Object, boolean, java.util.TimeZone, railo.runtime.type.dt.DateTime)
     */
    public DateTime toDate(Object o, boolean alsoNumbers, TimeZone tz, DateTime defaultValue) {
        return Caster.toDate(o,alsoNumbers,tz,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toDate(java.lang.Object, java.util.TimeZone)
     */
    public DateTime toDate(Object o, TimeZone tz) throws PageException {
        return Caster.toDate(o,tz);
    }

    /**
     * @see railo.runtime.util.Cast#toDate(java.lang.String, boolean, java.util.TimeZone, railo.runtime.type.dt.DateTime)
     */
    public DateTime toDate(String str, boolean alsoNumbers, TimeZone tz, DateTime defaultValue) {
        return Caster.toDate(str,alsoNumbers,tz,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toDate(java.lang.String, java.util.TimeZone)
     */
    public DateTime toDate(String str, TimeZone tz) throws PageException {
        return Caster.toDate(str,tz);
    }

    /**
     * @see railo.runtime.util.Cast#toDatetime(java.lang.Object, java.util.TimeZone)
     */
    public DateTime toDatetime(Object o, TimeZone tz) throws PageException {
        return Caster.toDate(o,tz);
    }

    /**
     * @see railo.runtime.util.Cast#toDateTime(java.lang.Object, java.util.TimeZone)
     */
    public DateTime toDateTime(Object o, TimeZone tz) throws PageException {
        return Caster.toDate(o,tz);
    }

    /**
     * @see railo.runtime.util.Cast#toDecimal(boolean)
     */
    public String toDecimal(boolean b) {
        return Caster.toDecimal(b);
    }

    /**
     * @see railo.runtime.util.Cast#toDecimal(char)
     */
    public String toDecimal(char c) {
        return Caster.toDecimal(c);
    }

    /**
     * @see railo.runtime.util.Cast#toDecimal(double)
     */
    public String toDecimal(double d) {
        return Caster.toDecimal(d);
    }

    /**
     * @see railo.runtime.util.Cast#toDecimal(java.lang.Object, java.lang.String)
     */
    public String toDecimal(Object value, String defaultValue) {
        return Caster.toDecimal(value,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toDecimal(java.lang.Object)
     */
    public String toDecimal(Object value) throws PageException {
        return Caster.toDecimal(value);
    }

    /**
     * @see railo.runtime.util.Cast#toDouble(boolean)
     */
    public Double toDouble(boolean b) {
        return Caster.toDouble(b);
    }

    /**
     * @see railo.runtime.util.Cast#toDouble(char)
     */
    public Double toDouble(char c) {
        return Caster.toDouble(c);
    }

    /**
     * @see railo.runtime.util.Cast#toDouble(double)
     */
    public Double toDouble(double d) {
        return Caster.toDouble(d);
    }

    /**
     * @see railo.runtime.util.Cast#toDouble(java.lang.Object, java.lang.Double)
     */
    public Double toDouble(Object o, Double defaultValue) {
        return Caster.toDouble(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toDouble(java.lang.Object)
     */
    public Double toDouble(Object o) throws PageException {
        return Caster.toDouble(o);
    }

    /**
     * @see railo.runtime.util.Cast#toDouble(java.lang.String, java.lang.Double)
     */
    public Double toDouble(String str, Double defaultValue) {
        return Caster.toDouble(str,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toDouble(java.lang.String)
     */
    public Double toDouble(String str) throws PageException {
        return Caster.toDouble(str);
    }

    /**
     * @see railo.runtime.util.Cast#toDoubleValue(boolean)
     */
    public double toDoubleValue(boolean b) {
        return Caster.toDoubleValue(b);
    }

    /**
     * @see railo.runtime.util.Cast#toDoubleValue(char)
     */
    public double toDoubleValue(char c) {
        return Caster.toDoubleValue(c);
    }

    /**
     * @see railo.runtime.util.Cast#toDoubleValue(double)
     */
    public double toDoubleValue(double d) {
        return Caster.toDoubleValue(d);
    }

    /**
     * @see railo.runtime.util.Cast#toDoubleValue(java.lang.Object, double)
     */
    public double toDoubleValue(Object o, double defaultValue) {
        return Caster.toDoubleValue(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toDoubleValue(java.lang.Object)
     */
    public double toDoubleValue(Object o) throws PageException {
        return Caster.toDoubleValue(o);
    }

    /**
     * @see railo.runtime.util.Cast#toDoubleValue(java.lang.String, double)
     */
    public double toDoubleValue(String str, double defaultValue) {
        return Caster.toDoubleValue(str,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toDoubleValue(java.lang.String)
     */
    public double toDoubleValue(String str) throws PageException {
        return Caster.toDoubleValue(str);
    }

    /**
     * @see railo.runtime.util.Cast#toFile(java.lang.Object, java.io.File)
     */
    public File toFile(Object obj, File defaultValue) {
        return Caster.toFile(obj,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toFile(java.lang.Object)
     */
    public File toFile(Object obj) throws PageException {
        return Caster.toFile(obj);
    }

    /**
     * @see railo.runtime.util.Cast#toInteger(boolean)
     */
    public Integer toInteger(boolean b) {
        return Caster.toInteger(b);
    }

    /**
     * @see railo.runtime.util.Cast#toInteger(char)
     */
    public Integer toInteger(char c) {
        return Caster.toInteger(c);
    }

    /**
     * @see railo.runtime.util.Cast#toInteger(double)
     */
    public Integer toInteger(double d) {
        return Caster.toInteger(d);
    }

    /**
     * @see railo.runtime.util.Cast#toInteger(java.lang.Object, java.lang.Integer)
     */
    public Integer toInteger(Object o, Integer defaultValue) {
        return Caster.toInteger(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toInteger(java.lang.Object)
     */
    public Integer toInteger(Object o) throws PageException {
        return Caster.toInteger(o);
    }

    /**
     * @see railo.runtime.util.Cast#toIntValue(boolean)
     */
    public int toIntValue(boolean b) {
        return Caster.toIntValue(b);
    }

    /**
     * @see railo.runtime.util.Cast#toIntValue(char)
     */
    public int toIntValue(char c) {
        return Caster.toIntValue(c);
    }

    /**
     * @see railo.runtime.util.Cast#toIntValue(double)
     */
    public int toIntValue(double d) {
        return Caster.toIntValue(d);
    }

    /**
     * @see railo.runtime.util.Cast#toIntValue(java.lang.Object, int)
     */
    public int toIntValue(Object o, int defaultValue) {
        return Caster.toIntValue(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toIntValue(java.lang.Object)
     */
    public int toIntValue(Object o) throws PageException {
        return Caster.toIntValue(o);
    }

    /**
     * @see railo.runtime.util.Cast#toIntValue(java.lang.String, int)
     */
    public int toIntValue(String str, int defaultValue) {
        return Caster.toIntValue(str,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toIntValue(java.lang.String)
     */
    public int toIntValue(String str) throws PageException {
        return Caster.toIntValue(str);
    }

    /**
     * @see railo.runtime.util.Cast#toIterator(java.lang.Object)
     */
    public Iterator toIterator(Object o) throws PageException {
        return Caster.toIterator(o);
    }

    /**
     * @see railo.runtime.util.Cast#toList(java.lang.Object, boolean, java.util.List)
     */
    public List toList(Object o, boolean duplicate, List defaultValue) {
        return Caster.toList(o,duplicate,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toList(java.lang.Object, boolean)
     */
    public List toList(Object o, boolean duplicate) throws PageException {
        return Caster.toList(o,duplicate);
    }

    /**
     * @see railo.runtime.util.Cast#toList(java.lang.Object, java.util.List)
     */
    public List toList(Object o, List defaultValue) {
        return Caster.toList(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toList(java.lang.Object)
     */
    public List toList(Object o) throws PageException {
        return Caster.toList(o);
    }

    /**
     * @see railo.runtime.util.Cast#toLocale(java.lang.String, java.util.Locale)
     */
    public Locale toLocale(String strLocale, Locale defaultValue) {
        return Caster.toLocale(strLocale,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toLocale(java.lang.String)
     */
    public Locale toLocale(String strLocale) throws PageException {
        return Caster.toLocale(strLocale);
    }

    /**
     * @see railo.runtime.util.Cast#toLong(boolean)
     */
    public Long toLong(boolean b) {
        return Caster.toLong(b);
    }

    /**
     * @see railo.runtime.util.Cast#toLong(char)
     */
    public Long toLong(char c) {
        return Caster.toLong(c);
    }

    /**
     * @see railo.runtime.util.Cast#toLong(double)
     */
    public Long toLong(double d) {
        return Caster.toLong(d);
    }

    /**
     * @see railo.runtime.util.Cast#toLong(java.lang.Object, java.lang.Long)
     */
    public Long toLong(Object o, Long defaultValue) {
        return Caster.toLong(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toLong(java.lang.Object)
     */
    public Long toLong(Object o) throws PageException {
        return Caster.toLong(o);
    }

    /**
     * @see railo.runtime.util.Cast#toLongValue(boolean)
     */
    public long toLongValue(boolean b) {
        return Caster.toLongValue(b);
    }

    /**
     * @see railo.runtime.util.Cast#toLongValue(char)
     */
    public long toLongValue(char c) {
        return Caster.toLongValue(c);
    }

    /**
     * @see railo.runtime.util.Cast#toLongValue(double)
     */
    public long toLongValue(double d) {
        return Caster.toLongValue(d);
    }

    /**
     * @see railo.runtime.util.Cast#toLongValue(java.lang.Object, long)
     */
    public long toLongValue(Object o, long defaultValue) {
        return Caster.toLongValue(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toLongValue(java.lang.Object)
     */
    public long toLongValue(Object o) throws PageException {
        return Caster.toLongValue(o);
    }

    /**
     * @see railo.runtime.util.Cast#toMap(java.lang.Object, boolean, java.util.Map)
     */
    public Map toMap(Object o, boolean duplicate, Map defaultValue) {
        return Caster.toMap(o,duplicate,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toMap(java.lang.Object, boolean)
     */
    public Map toMap(Object o, boolean duplicate) throws PageException {
        return Caster.toMap(o,duplicate);
    }

    /**
     * @see railo.runtime.util.Cast#toMap(java.lang.Object, java.util.Map)
     */
    public Map toMap(Object o, Map defaultValue) {
        return Caster.toMap(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toMap(java.lang.Object)
     */
    public Map toMap(Object o) throws PageException {
        return Caster.toMap(o);
    }

    /**
     * @see railo.runtime.util.Cast#toNode(java.lang.Object, org.w3c.dom.Node)
     */
    public Node toNode(Object o, Node defaultValue) {
        return Caster.toNode(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toNode(java.lang.Object)
     */
    public Node toNode(Object o) throws PageException {
        return Caster.toNode(o);
    }

    /**
     * @see railo.runtime.util.Cast#toNodeList(java.lang.Object, org.w3c.dom.NodeList)
     */
    public NodeList toNodeList(Object o, NodeList defaultValue) {
        return Caster.toNodeList(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toNodeList(java.lang.Object)
     */
    public NodeList toNodeList(Object o) throws PageException {
        return Caster.toNodeList(o);
    }

    /**
     * @see railo.runtime.util.Cast#toNull(java.lang.Object, java.lang.Object)
     */
    public Object toNull(Object value, Object defaultValue) {
        return Caster.toNull(value,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toNull(java.lang.Object)
     */
    public Object toNull(Object value) throws PageException {
        return Caster.toNull(value);
    }

    /**
     * @see railo.runtime.util.Cast#toPageException(java.lang.Throwable)
     */
    public PageException toPageException(Throwable t) {
        return Caster.toPageException(t);
    }

    /**
     * @see railo.runtime.util.Cast#toQuery(java.lang.Object, boolean, railo.runtime.type.Query)
     */
    public Query toQuery(Object o, boolean duplicate, Query defaultValue) {
        return Caster.toQuery(o,duplicate,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toQuery(java.lang.Object, boolean)
     */
    public Query toQuery(Object o, boolean duplicate) throws PageException {
        return Caster.toQuery(o,duplicate);
    }

    /**
     * @see railo.runtime.util.Cast#toQuery(java.lang.Object, railo.runtime.type.Query)
     */
    public Query toQuery(Object o, Query defaultValue) {
        return Caster.toQuery(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toQuery(java.lang.Object)
     */
    public Query toQuery(Object o) throws PageException {
        return Caster.toQuery(o);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(boolean)
     */
    public Boolean toRef(boolean b) {
        return Caster.toRef(b);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(byte)
     */
    public Byte toRef(byte b) {
        return Caster.toRef(b);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(char)
     */
    public String toRef(char c) {
        return Caster.toRef(c);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(railo.runtime.type.Collection)
     */
    public Collection toRef(Collection o) {
        return Caster.toRef(o);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(double)
     */
    public Double toRef(double d) {
        return Caster.toRef(d);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(float)
     */
    public Float toRef(float f) {
        return Caster.toRef(f);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(int)
     */
    public Integer toRef(int i) {
        return Caster.toRef(i);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(long)
     */
    public Long toRef(long l) {
        return Caster.toRef(l);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(java.lang.Object)
     */
    public Object toRef(Object o) {
        return Caster.toRef(o);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(short)
     */
    public Short toRef(short s) {
        return Caster.toRef(s);
    }

    /**
     * @see railo.runtime.util.Cast#toRef(java.lang.String)
     */
    public String toRef(String str) {
        return Caster.toRef(str);
    }

    /**
     * @see railo.runtime.util.Cast#toShort(boolean)
     */
    public Short toShort(boolean b) {
        return Caster.toShort(b);
    }

    /**
     * @see railo.runtime.util.Cast#toShort(char)
     */
    public Short toShort(char c) {
        return Caster.toShort(c);
    }

    /**
     * @see railo.runtime.util.Cast#toShort(double)
     */
    public Short toShort(double d) {
        return Caster.toShort(d);
    }

    /**
     * @see railo.runtime.util.Cast#toShort(java.lang.Object, java.lang.Short)
     */
    public Short toShort(Object o, Short defaultValue) {
        return Caster.toShort(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toShort(java.lang.Object)
     */
    public Short toShort(Object o) throws PageException {
        return Caster.toShort(o);
    }

    /**
     * @see railo.runtime.util.Cast#toShortValue(boolean)
     */
    public short toShortValue(boolean b) {
        return Caster.toShortValue(b);
    }

    /**
     * @see railo.runtime.util.Cast#toShortValue(char)
     */
    public short toShortValue(char c) {
        return Caster.toShortValue(c);
    }

    /**
     * @see railo.runtime.util.Cast#toShortValue(double)
     */
    public short toShortValue(double d) {
        return Caster.toShortValue(d);
    }

    /**
     * @see railo.runtime.util.Cast#toShortValue(java.lang.Object, short)
     */
    public short toShortValue(Object o, short defaultValue) {
        return Caster.toShortValue(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toShortValue(java.lang.Object)
     */
    public short toShortValue(Object o) throws PageException {
        return Caster.toShortValue(o);
    }

    /**
     * @see railo.runtime.util.Cast#toString(boolean)
     */
    public String toString(boolean b) {
        return Caster.toString(b);
    }

    /**
     * @see railo.runtime.util.Cast#toString(double)
     */
    public String toString(double d) {
        return Caster.toString(d);
    }

    /**
     * @see railo.runtime.util.Cast#toString(int)
     */
    public String toString(int i) {
        return Caster.toString(i);
    }

    /**
     * @see railo.runtime.util.Cast#toString(long)
     */
    public String toString(long l) {
        return Caster.toString(l);
    }

    /**
     * @see railo.runtime.util.Cast#toString(java.lang.Object, java.lang.String)
     */
    public String toString(Object o, String defaultValue) {
        return Caster.toString(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toString(java.lang.Object)
     */
    public String toString(Object o) throws PageException {
        return Caster.toString(o);
    }
    
    /**
     * @see railo.runtime.util.Cast#toStruct(java.lang.Object, railo.runtime.type.Struct, boolean)
     */
    public Struct toStruct(Object o, Struct defaultValue,boolean caseSensitive) {
        return Caster.toStruct(o,defaultValue,caseSensitive);
    }

    /**
     * @see railo.runtime.util.Cast#toStruct(java.lang.Object, railo.runtime.type.Struct)
     */
    public Struct toStruct(Object o, Struct defaultValue) {
        return Caster.toStruct(o,defaultValue,true);
    }

    /**
     * @see railo.runtime.util.Cast#toStruct(java.lang.Object)
     */
    public Struct toStruct(Object o) throws PageException {
        return Caster.toStruct(o);
    }

    /**
     * @see railo.runtime.util.Cast#toTimespan(java.lang.Object, railo.runtime.type.dt.TimeSpan)
     */
    public TimeSpan toTimespan(Object o, TimeSpan defaultValue) {
        return Caster.toTimespan(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toTimespan(java.lang.Object)
     */
    public TimeSpan toTimespan(Object o) throws PageException {
        return Caster.toTimespan(o);
    }

    /**
     * @see railo.runtime.util.Cast#toTypeName(java.lang.Object)
     */
    public String toTypeName(Object o) {
        return Caster.toTypeName(o);
    }

    /**
     * @see railo.runtime.util.Cast#toUUId(java.lang.Object, java.lang.Object)
     */
    public Object toUUId(Object o, Object defaultValue) {
        return Caster.toUUId(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toUUId(java.lang.Object)
     */
    public Object toUUId(Object o) throws PageException {
        return Caster.toUUId(o);
    }

    /**
     * @see railo.runtime.util.Cast#toVariableName(java.lang.Object, java.lang.Object)
     */
    public Object toVariableName(Object obj, Object defaultValue) {
        return Caster.toVariableName(obj,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toVariableName(java.lang.Object)
     */
    public Object toVariableName(Object o) throws PageException {
        return Caster.toVariableName(o);
    }

    /**
     * @see railo.runtime.util.Cast#toVoid(java.lang.Object, java.lang.Object)
     */
    public Object toVoid(Object o, Object defaultValue) {
        return Caster.toVoid(o,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toVoid(java.lang.Object)
     */
    public Object toVoid(Object o) throws PageException {
        return Caster.toVoid(o);
    }

    /**
     * @see railo.runtime.util.Cast#toXML(java.lang.Object, org.w3c.dom.Node)
     */
    public Node toXML(Object value, Node defaultValue) {
        return Caster.toXML(value,defaultValue);
    }

    /**
     * @see railo.runtime.util.Cast#toXML(java.lang.Object)
     */
    public Node toXML(Object value) throws PageException {
        return Caster.toXML(value);
    }

    public static Cast getInstance() {
        if(singelton==null)singelton=new CastImpl();
        return singelton;
    }

	/**
	 *
	 * @see railo.runtime.util.Cast#toResource(java.lang.Object)
	 */
	public Resource toResource(Object obj) throws PageException {
		if(obj instanceof Resource) return (Resource) obj;
		if(obj instanceof File) return ResourceUtil.toResource((File) obj);
		return ResourceUtil.toResourceNotExisting(ThreadLocalPageContext.get(), toString(obj));
	}

	/**
	 *
	 * @see railo.runtime.util.Cast#toResource(java.lang.Object, railo.commons.io.res.Resource)
	 */
	public Resource toResource(Object obj, Resource defaultValue) {
		if(obj instanceof Resource) return (Resource) obj;
		String path=toString(obj,null);
		if(path==null)return defaultValue;
		return ResourceUtil.toResourceNotExisting(ThreadLocalPageContext.get(), path);
	}

	/**
	 * @see railo.runtime.util.Cast#to(java.lang.String, java.lang.Object, boolean)
	 */
	public Object to(String type, Object o,boolean alsoPattern) throws PageException {
		return Caster.castTo(ThreadLocalPageContext.get(), type, o,alsoPattern);
	}

	/**
	 * @see railo.runtime.util.Cast#toSerializable(java.lang.Object)
	 */
	public Serializable toSerializable(Object obj) throws PageException {
		return Caster.toSerializable(obj);
	}

	/**
	 * @see railo.runtime.util.Cast#toSerializable(java.lang.Object, java.io.Serializable)
	 */
	public Serializable toSerializable(Object object, Serializable defaultValue) {
		return Caster.toSerializable(object, defaultValue);
	}

}
