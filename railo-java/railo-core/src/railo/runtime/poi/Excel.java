package railo.runtime.poi;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.it.ValueIterator;
import railo.runtime.type.util.StructSupport;

public class Excel extends StructSupport implements Cloneable,Struct {

	public static final short FORMAT_UNDEFINED=0;
	public static final short FORMAT_XSSF=1;
	public static final short FORMAT_HSSF=2;
	public static final short FORMAT_SXSSF=4;
	private Workbook workbook;
	private Drawing drawing;
	private short xmlFormat;

	private static final Collection.Key SHEET_NAME=KeyImpl.init("SHEETNAME");
	private static final Collection.Key SHEET_NUMBER=KeyImpl.init("SHEETNUMBER");
	private static final Collection.Key ROW_COUNT=KeyImpl.init("ROWCOUNT");
	private static final Collection.Key SUMMARY_INFO=KeyImpl.init("SUMMARYINFO");
	private static final Collection.Key[] keys=new Collection.Key[]{
		SHEET_NAME,SHEET_NUMBER,ROW_COUNT,SUMMARY_INFO
	};
	private static final String[] skeys=new String[]{
		SHEET_NAME.getString(),SHEET_NUMBER.getString(),ROW_COUNT.getString(),SUMMARY_INFO.getString()
	};

    public static final String XSSF_FORMAT = "XSSF";
    public static final String HSSF_FORMAT = "HSSF";
    public static final String SXSSF_FORMAT = "SXSSF";
	
	
	public Excel(String sheetName, short xmlFormat, int rows){
		if(FORMAT_XSSF==xmlFormat) workbook=new XSSFWorkbook();
		else if(FORMAT_HSSF==xmlFormat) workbook=new HSSFWorkbook();
		else if(FORMAT_SXSSF==xmlFormat) workbook=new SXSSFWorkbook();
		this.xmlFormat=xmlFormat;
        Sheet sheet = workbook.createSheet();
        drawing = sheet.createDrawingPatriarch();
        workbook.setSheetName(0, sheetName);
	}


    private Workbook getWorkbook() {
		return workbook;
	}
    

	public void write(Resource res, String password) {
		// TODO Auto-generated method stub
		
	}

	
	public void setValue(int rowNumber, int columnNumber, String value) throws CasterException {
		if(value==null) value="";
		Sheet sheet = workbook.getSheet(getExcelSheetName());
	    
		// get Row
		Row row = sheet.getRow(rowNumber);
	    if(row==null) row = sheet.createRow(rowNumber);
	     
	    // get Cell
	    Cell cell = row.getCell(columnNumber);
	    CellStyle style = null;
	    if(cell != null) {
	    	style = cell.getCellStyle();
	        row.removeCell(cell);
	    }
	    cell = row.createCell(columnNumber);
	    if(style != null) cell.setCellStyle(style);
	    
        CreationHelper createHelper = workbook.getCreationHelper();
        boolean isFormula=style != null && style.getDataFormatString().equals("@");
        
        
        if(!isFormula && Decision.isNumeric(value)) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			double dbl = Caster.toDoubleValue(value);
            cell.setCellValue(dbl);
            _expandColumnWidth(sheet,Caster.toString(dbl),columnNumber);
		}
        else if(StringUtil.isEmpty("")) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
            cell.setCellValue(createHelper.createRichTextString(""));
        }
        else {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(createHelper.createRichTextString(value));
            _expandColumnWidth(sheet,value,columnNumber);
        } 
	        
	        
	}
	

	private static void _expandColumnWidth(Sheet sheet, String value, int columnNumber) {
		int colwidth = sheet.getColumnWidth(columnNumber );
        int len = (int)((value.length() * 8) / 0.05D);
        if(colwidth < len)
            sheet.setColumnWidth(columnNumber, len + 1);
	}


	private String getExcelSheetName() {
		return workbook.getSheetName(0);
	}
	
	private int getSheetIndex() {
		return workbook.getSheetIndex(getExcelSheetName());
	}
	private int getExcelSheetNumber() {
		return getSheetIndex()+1;
	}
	

	
	
	
	
	public Struct getSummaryInfo() {
        Struct infostruct = new StructImpl();
        
        int sheets = workbook.getNumberOfSheets();
        infostruct.setEL("SHEETS", new Double(sheets));
        if(sheets>0) {
        	StringBuilder sb=new StringBuilder();
            for(int i=0; i<sheets; i++){
            	if(i>0)sb.append(',');
            	sb.append(workbook.getSheetName(i));
            }
            infostruct.setEL("SHEETNAMES", sb.toString());
        }
    	
        if(xmlFormat==FORMAT_HSSF) {
        	infostruct.setEL("SPREADSHEETTYPE", "Excel");
        	
        	HSSFWorkbook hssfworkbook = (HSSFWorkbook)workbook;
            info(infostruct,hssfworkbook.getSummaryInformation());
            info(infostruct,hssfworkbook.getDocumentSummaryInformation());
        } 
        else if(xmlFormat==FORMAT_XSSF)	{
        	infostruct.put("SPREADSHEETTYPE", "Excel (2007)");
            
        	XSSFWorkbook xssfworkbook = (XSSFWorkbook)workbook;
            POIXMLProperties props = xssfworkbook.getProperties();
            info(infostruct,props.getCoreProperties().getUnderlyingProperties());
            info(infostruct,props.getExtendedProperties().getUnderlyingProperties());
        }
        return infostruct;
    }
	



	





	private void info(Struct sct, CTProperties props) {
		if(props==null) return;
		set(sct, "COMPANY", props.getCompany());
		set(sct, "MANAGER", props.getManager());
	}

	private void info(Struct sct, PackagePropertiesPart props) {
		if(props==null) return;
		set(sct, "AUTHOR", props.getCreatorProperty().getValue());
		set(sct, "CATEGORY", props.getCategoryProperty().getValue());
		set(sct, "COMMENTS", props.getDescriptionProperty().getValue());
		set(sct, "CREATIONDATE", props.getCreatedProperty().getValue());
		set(sct, "KEYWORDS", props.getKeywordsProperty().getValue());
		set(sct, "LASTAUTHOR", props.getLastModifiedByProperty().getValue());
		set(sct, "LASTEDITED", props.getModifiedProperty().getValue());
		set(sct, "SUBJECT", props.getSubjectProperty().getValue());
		set(sct, "TITLE", props.getTitleProperty().getValue());
	}

	private void info(Struct sct, DocumentSummaryInformation summary) {
		if(summary==null) return;
		set(sct,"CATEGORY",summary.getCategory());
		set(sct,"COMPANY",summary.getCompany());
		set(sct,"MANAGER",summary.getManager());
		set(sct,"PRESENTATIONFORMAT",summary.getPresentationFormat());
	}

	private void info(Struct sct, SummaryInformation summary) {
		if(summary==null) return;
		set(sct,"AUTHOR",summary.getAuthor());
		set(sct,"APPLICATIONNAME",summary.getApplicationName());
		set(sct,"COMMENTS",summary.getComments());
		set(sct,"CREATIONDATE",summary.getCreateDateTime());
        set(sct,"KEYWORDS",summary.getKeywords());
		set(sct,"LASTAUTHOR",summary.getLastAuthor());
		set(sct,"LASTEDITED",summary.getEditTime());
		set(sct,"LASTSAVED",summary.getLastSaveDateTime());
        set(sct,"REVNUMBER",summary.getRevNumber());
		set(sct,"SUBJECT",summary.getSubject());
		set(sct,"TITLE",summary.getTitle());
		set(sct,"TEMPLATE",summary.getTemplate());
	}

	private void set(Struct sct, String name, String value) {
		sct.setEL(KeyImpl.init(name), StringUtil.toStringEmptyIfNull(value));
	}

	private void set(Struct sct, String name, Date value) {
		Object obj=Caster.toDate(value,false, null,null);
		if(obj==null)obj="";
		sct.setEL(KeyImpl.init(name), obj);
	}

	private void set(Struct sct, String name, long value) {
		Object obj=(value!=0)?new DateTimeImpl(value, false):"";
        sct.setEL(KeyImpl.init(name), obj);
	}
	
// Struct methods

	@Override
	public int size() {
		return keys.length;
	}


	@Override
	public Key[] keys() {
		return keys;
	}


	@Override
	public Object remove(Key key) throws PageException {
		return null;
	}


	@Override
	public Object removeEL(Key key) {
		return null;
	}


	@Override
	public void clear() {
	}


	@Override
	public Object get(Key key) throws PageException {
		Object value = get(key,NullSupportHelper.NULL());
		if(value!=NullSupportHelper.NULL()) return value;
		throw invalidKey(null,this,key);
	}


	@Override
	public Object get(Key key, Object defaultValue) {
		if(key.equals(SHEET_NAME)) return getExcelSheetName();
		else if(key.equals(SHEET_NUMBER)) return Caster.toDouble(getExcelSheetNumber());
		else if(key.equals(ROW_COUNT)) return Caster.toDouble(0);
		else if(key.equals(SUMMARY_INFO)) return getSummaryInfo();
		return defaultValue;
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		return value;
	}


	@Override
	public Object setEL(Key key, Object value) {
		return value;
	}


	@Override
	public Collection duplicate(boolean deepCopy) {
		return this;
	}


	@Override
	public boolean containsKey(Key key) {
		return get(key,null)!=null;
	}


	@Override
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys);
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this,keys);
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return new ValueIterator(this,keys());
	}
}
