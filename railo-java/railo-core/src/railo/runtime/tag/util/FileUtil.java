package railo.runtime.tag.util;

import railo.runtime.exp.ApplicationException;


public class FileUtil {

	public static final int NAMECONFLICT_UNDEFINED  =  1;   // can't start at 0 because we need to be able to do a bitmask test
	public static final int NAMECONFLICT_ERROR      =  2;
	public static final int NAMECONFLICT_SKIP       =  4;   // same as IGNORE
	public static final int NAMECONFLICT_OVERWRITE  =  8;   // same as MERGE
	public static final int NAMECONFLICT_MAKEUNIQUE = 16;
//	public static final int NAMECONFLICT_CLOSURE    = 32;	// FUTURE


	public static int toNameConflict( String nameConflict ) throws ApplicationException {

		if ( nameConflict == null ) return NAMECONFLICT_UNDEFINED;

		nameConflict = nameConflict.trim().toLowerCase();

		if ( nameConflict.isEmpty() ) return NAMECONFLICT_UNDEFINED;

		if ( "error".equals( nameConflict ) )
			return NAMECONFLICT_ERROR;

		if ( "skip".equals( nameConflict ) || "ignore".equals( nameConflict ) )
			return NAMECONFLICT_SKIP;

		if ( "merge".equals( nameConflict ) || "overwrite".equals( nameConflict ) )
			return NAMECONFLICT_OVERWRITE;

		if ( "makeunique".equals( nameConflict ) || "unique".equals( nameConflict ) )
			return NAMECONFLICT_MAKEUNIQUE;

		throw new ApplicationException("Invalid value for attribute nameConflict ["+nameConflict+"]",
				"valid values are [" + fromNameConflictBitMask( Integer.MAX_VALUE ) + "]");
	}


	/**
	 *
	 * @param nameConflict
	 * @param allowedValuesMask
	 * @return
	 * @throws ApplicationException
	 */
	public static int toNameConflict( String nameConflict, int allowedValuesMask ) throws ApplicationException {

		int result = toNameConflict( nameConflict );

		if ( ( allowedValuesMask & result ) == 0 ) {

			throw new ApplicationException("Invalid value for attribute nameConflict ["+nameConflict+"]",
				"valid values are [" + fromNameConflictBitMask( allowedValuesMask ) + "]");
		}

		return result;
	}


	/**
	 *
	 * @param nameConflict
	 * @param allowedValuesMask
	 * @param defaultValue
	 * @return
	 * @throws ApplicationException
	 */
	public static int toNameConflict( String nameConflict, int allowedValuesMask, int defaultValue ) throws ApplicationException {

		int result = toNameConflict( nameConflict, allowedValuesMask );

		if ( result == NAMECONFLICT_UNDEFINED )
			return defaultValue;

		return result;
	}


	public static String fromNameConflictBitMask( int bitmask ) {

		StringBuilder sb = new StringBuilder();

		if ( (bitmask & NAMECONFLICT_ERROR) > 0 )      sb.append( "error" ).append(',');
		if ( (bitmask & NAMECONFLICT_MAKEUNIQUE) > 0 ) sb.append( "makeunique (unique)" ).append(',');
		if ( (bitmask & NAMECONFLICT_OVERWRITE) > 0 )  sb.append( "overwrite (merge)" ).append(',');
		if ( (bitmask & NAMECONFLICT_SKIP) > 0 )       sb.append( "skip (ignore)" ).append(',');

		if ( sb.length() > 0 )
			sb.setLength( sb.length() - 1 );    // remove last ,

		return sb.toString();
	}

}
