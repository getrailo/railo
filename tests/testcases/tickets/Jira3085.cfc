<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function setup(){
		SetTimeZone("UTC");
		variables.tz=getTimeZone();

		SetLocale("de_CH");
		variables.loc=getLocale();

		variables.dt=CreateDateTime(2000,1,2,3,4,5,6,"UTC");
	}
	


	public void function testSetGetLocale(){
		var old=SetLocale("en_us");

		assertEquals("english (us)",getLocale());
		SetLocale(old);
		assertEquals(old,getLocale());
	}

	public void function testLocaleInfo(){
		SetLocale("en_us");
		var l=GetLocale();
		var i=l.info();
		assertEquals("english (us)",l);
		assertEquals("US",i.country);
		assertEquals("en",i.language);
		assertEquals("English (United States)",i.name);
		assertEquals("USA",i.iso.country);
		assertEquals("eng",i.iso.language);
		assertEquals("United States",i.display.country);
		assertEquals("English",i.display.language);
	}

	public void function testSetGetTimeZone(){
		var old=SetTimeZone("PST");

		assertEquals("PST",getTimeZone());
		SetTimeZone(old);
		assertEquals(old,getTimeZone());
	}

	public void function testTimeZoneInfo(){
		var old=SetTimeZone("PST");
		var tz=GetTimeZone();
		getTimeZoneInfo("PST","de_CH");
		var i=tz.info(loc);
		
		assertEquals("Pazifische Normalzeit",i.name);
		assertEquals("PST",i.id);
		assertEquals(3600,i.DSTOffset);
		assertEquals(-25200,i.offset);
		//assertEquals("english (us)",l);
	}

	public void function testCreateDate(){
		SetTimeZone("UTC");
		var tz=getTimeZone();
		
		var dt=CreateDate(2000,1,1,"GMT+1");
		assertEquals("{ts '2000-01-01 01:00:00'}",dt&"");
		
		var dt=CreateDate(2000,1,1,tz);
		assertEquals("{ts '2000-01-01 00:00:00'}",dt&"");
		
		var dt=CreateDate(2000,1,1);
		assertEquals("{ts '2000-01-01 00:00:00'}",dt&"");
	}

	public void function testCreateDateTime(){
		SetTimeZone("UTC");
		var tz=getTimeZone();
		
		var dt=CreateDateTime(2000,1,2,3,4,5,6,"GMT+1");
		assertEquals("{ts '2000-01-02 04:04:05'}",dt&"");
		
		var dt=CreateDateTime(2000,1,2,3,4,5,6,tz);
		assertEquals("{ts '2000-01-02 03:04:05'}",dt&"");
		
		var dt=CreateDateTime(2000,1,2,3,4,5,6);
		assertEquals("{ts '2000-01-02 03:04:05'}",dt&"");
		
	}

	public void function testCreateTime(){
		SetTimeZone("UTC");
		var tz=getTimeZone();
		
		var dt=CreateTime(3,4,5,6,"GMT+1");
		assertEquals("{t '04:04:05'}",dt&"");
		
		var dt=CreateTime(3,4,5,6,tz);
		assertEquals("{t '03:04:05'}",dt&"");
		
		var dt=CreateTime(3,4,5,6);
		assertEquals("{t '03:04:05'}",dt&"");
	}


	public void function testLSIsDate(){
		assertEquals(true,LSIsDate(date:dt,timezone:tz));
		assertEquals(true,LSIsDate(date:dt,timezone:"UTC"));
		assertEquals(true,LSIsDate(date:dt));
	}

	public void function testDateFormat(){
		assertEquals("02-Jan-00",dateFormat(date:dt,timezone:tz));
		assertEquals("02-Jan-00",dateFormat(date:dt,timezone:"UTC"));
		assertEquals("02-Jan-00",dateFormat(date:dt));
	}

	public void function testLSDateFormat(){
		assertEquals("02.01.2000",LSdateFormat(date:dt));

		assertEquals("02.01.2000",LSdateFormat(date:dt,timezone:tz));
		assertEquals("02.01.2000",LSdateFormat(date:dt,timezone:"UTC"));

		assertEquals("02.01.2000",LSdateFormat(date:dt,locale:loc));
		assertEquals("02.01.2000",LSdateFormat(date:dt,locale:"de_CH"));
	}

	public void function testDateTimeFormat(){
		assertEquals("02-Jan-2000 03:04:05",dateTimeFormat(date:dt));

		assertEquals("02-Jan-2000 03:04:05",dateTimeFormat(date:dt,timezone:tz));
		assertEquals("02-Jan-2000 03:04:05",dateTimeFormat(date:dt,timezone:"UTC"));
	}

	public void function testLSDateTimeFormat(){
		assertEquals("02-Jan-2000 03:04:05",LSdateTimeFormat(date:dt));

		assertEquals("02-Jan-2000 03:04:05",LSdateTimeFormat(date:dt,timezone:tz));
		assertEquals("02-Jan-2000 03:04:05",LSdateTimeFormat(date:dt,timezone:"UTC"));

		assertEquals("02-Jan-2000 03:04:05",lsdateTimeFormat(date:dt,locale:loc));
		assertEquals("02-Jan-2000 03:04:05",lsdateTimeFormat(date:dt,locale:"DE_CH"));
	}

	public void function testTimeFormat(){
		assertEquals("03:04 AM",TimeFormat(time:dt,timezone:tz));
		//assertEquals("03:04 AM",TimeFormat(time:dt,timezone:"UTC"));
		//assertEquals("03:04 AM",TimeFormat(time:dt));
	}

	public void function testLSTimeFormat(){
		assertEquals("03:04",LSTimeFormat(time:dt));

		assertEquals("03:04",LSTimeFormat(time:dt,timezone:tz));
		assertEquals("03:04",LSTimeFormat(time:dt,timezone:"UTC"));

		assertEquals("03:04",LSTimeFormat(time:dt,locale:loc));
		assertEquals("03:04",LSTimeFormat(time:dt,locale:"de_CH"));
	}

	public void function testDatePart(){
		assertEquals("3",DatePart(datepart:"h",date:dt,timezone:tz));
		assertEquals("3",DatePart(datepart:"h",date:dt,timezone:"UTC"));
		assertEquals("3",DatePart(datepart:"h",date:dt));
	}

	public void function testYear(){
		assertEquals("2000",Year(date:dt,timezone:tz));
		assertEquals("2000",Year(date:dt,timezone:"UTC"));
		assertEquals("2000",Year(date:dt));
	}

	public void function testMonth(){
		assertEquals("1",Month(date:dt,timezone:tz));
		assertEquals("1",Month(date:dt,timezone:"UTC"));
		assertEquals("1",Month(date:dt));
	}

	public void function testWeek(){
		assertEquals("2",Week(date:dt,timezone:tz));
		assertEquals("2",Week(date:dt,timezone:"UTC"));
		assertEquals("2",Week(date:dt));
	}

	public void function testLSWeek(){
		assertEquals("52",LSWeek(date:dt));

		assertEquals("52",LSWeek(date:dt,timezone:tz));
		assertEquals("52",LSWeek(date:dt,timezone:"UTC"));

		assertEquals("52",LSWeek(date:dt,locale:loc));
		assertEquals("52",LSWeek(date:dt,locale:"de_CH"));
	}

	public void function testDay(){
		assertEquals("2",Day(date:dt,timezone:tz));
		assertEquals("2",Day(date:dt,timezone:"UTC"));
		assertEquals("2",Day(date:dt));
	}

	public void function testHour(){
		assertEquals("3",Hour(date:dt,timezone:tz));
		assertEquals("3",Hour(date:dt,timezone:"UTC"));
		assertEquals("3",Hour(date:dt));
	}

	public void function testMinute(){
		assertEquals("4",Minute(date:dt,timezone:tz));
		assertEquals("4",Minute(date:dt,timezone:"UTC"));
		assertEquals("4",Minute(date:dt));
	}

	public void function testDay(){
		assertEquals("5",Second(date:dt,timezone:tz));
		assertEquals("5",Second(date:dt,timezone:"UTC"));
		assertEquals("5",Second(date:dt));
	}

	public void function testSecond(){
		assertEquals("6",MilliSecond(date:dt,timezone:tz));
		assertEquals("6",MilliSecond(date:dt,timezone:"UTC"));
		assertEquals("6",MilliSecond(date:dt));
	}

	public void function testQuarter(){
		assertEquals("1",Quarter(date:dt,timezone:tz));
		assertEquals("1",Quarter(date:dt,timezone:"UTC"));
		assertEquals("1",Quarter(date:dt));
	}

	public void function testDayOfWeek(){
		assertEquals(1,DayOfWeek(date:dt,timezone:tz));
		assertEquals(1,DayOfWeek(date:dt,timezone:"UTC"));
		assertEquals(1,DayOfWeek(date:dt));
	}

	public void function testLSDayOfWeek(){
		assertEquals(1,LSDayOfWeek(date:dt));
		
		assertEquals(1,LSDayOfWeek(date:dt,timezone:tz));
		assertEquals(1,LSDayOfWeek(date:dt,timezone:"UTC"));
		
		assertEquals(1,LSDayOfWeek(date:dt,locale:loc));
		assertEquals(1,LSDayOfWeek(date:dt,locale:"de_CH"));


	}

	public void function testDayOfYear(){
		assertEquals("2",DayOfYear(date:dt,timezone:tz));
		assertEquals("2",DayOfYear(date:dt,timezone:"UTC"));
		assertEquals("2",DayOfYear(date:dt));
	}

	public void function testDaysInMonth(){
		assertEquals(31,DaysInMonth(date:dt,timezone:tz));
		assertEquals(31,DaysInMonth(date:dt,timezone:"UTC"));
		assertEquals(31,DaysInMonth(date:dt));
	}

	public void function testDaysInYear(){
		assertEquals(366,DaysInYear(date:dt,timezone:tz));
		assertEquals(366,DaysInYear(date:dt,timezone:"UTC"));
		assertEquals(366,DaysInYear(date:dt));
	}

	public void function testFirstDayOfMonth(){
		assertEquals(1,FirstDayOfMonth(date:dt,timezone:tz));
		assertEquals(1,FirstDayOfMonth(date:dt,timezone:"UTC"));
		assertEquals(1,FirstDayOfMonth(date:dt));
	}

	public void function testParseDateTime(){
		assertEquals("{ts '2000-01-02 03:04:05'}",ParseDateTime(date:dt&"",timezone:tz));
		assertEquals("{ts '2000-01-02 03:04:05'}",ParseDateTime(date:dt&"",timezone:"UTC"));
		assertEquals("{ts '2000-01-02 03:04:05'}",ParseDateTime(date:dt&""));
	}





	public void function testDayOfWeekAsString(){
		assertEquals("Sonntag",DayOfWeekAsString(day_of_week:1,locale:loc));
		assertEquals("Sonntag",DayOfWeekAsString(day_of_week:1,locale:"de_CH"));
		assertEquals("Sonntag",DayOfWeekAsString(day_of_week:1));
	}

	public void function testDayOfWeekShortAsString(){
		assertEquals("So",DayOfWeekShortAsString(day_of_week:1,locale:loc));
		assertEquals("So",DayOfWeekShortAsString(day_of_week:1,locale:"de_CH"));
		assertEquals("So",DayOfWeekShortAsString(day_of_week:1));
	}
	public void function testLSCurrencyFormat(){
		assertEquals("SFr. 1.00",LSCurrencyFormat(number:1,locale:loc));
		assertEquals("SFr. 1.00",LSCurrencyFormat(number:1,locale:"de_CH"));
		assertEquals("SFr. 1.00",LSCurrencyFormat(number:1));
	}
	public void function testLSNumberFormat(){
		assertEquals("1",LSNumberFormat(number:1,locale:loc));
		assertEquals("1",LSNumberFormat(number:1,locale:"de_CH"));
		assertEquals("1",LSNumberFormat(number:1));
	}

	public void function testLSEuroCurrencyFormat(){
		assertEquals("SFr. 1.00",LSEuroCurrencyFormat(number:1,locale:loc));
		assertEquals("SFr. 1.00",LSEuroCurrencyFormat(number:1,locale:"de_CH"));
		assertEquals("SFr. 1.00",LSEuroCurrencyFormat(number:1));
	}

	public void function testLSIsCurrency(){
		assertEquals(true,LSIsCurrency(number:1,locale:loc));
		assertEquals(true,LSIsCurrency(number:1,locale:"de_CH"));
		assertEquals(true,LSIsCurrency(number:1));
	}

	public void function testLSIsDate(){
		assertEquals(true,LSIsDate(date:dt,locale:loc));
		assertEquals(true,LSIsDate(date:dt,locale:"de_CH"));
		assertEquals(true,LSIsDate(date:dt));
	}

	public void function testLSIsNumeric(){
		assertEquals(true,LSIsNumeric(number:1,locale:loc));
		assertEquals(true,LSIsNumeric(number:1,locale:"de_CH"));
		assertEquals(true,LSIsNumeric(number:1));
	}

	public void function testLSParseCurrency(){
		assertEquals(true,LSParseCurrency(number:1,locale:loc));
		assertEquals(true,LSParseCurrency(number:1,locale:"de_CH"));
		assertEquals(true,LSParseCurrency(number:1));
	}

	public void function testLSEuroParseCurrency(){
		assertEquals(true,LSParseEuroCurrency(number:1,locale:loc));
		assertEquals(true,LSParseEuroCurrency(number:1,locale:"de_CH"));
		assertEquals(true,LSParseEuroCurrency(number:1));
	}

	public void function testLSParseNumber(){
		assertEquals(true,LSParseNumber(number:1,locale:loc));
		assertEquals(true,LSParseNumber(number:1,locale:"de_CH"));
		assertEquals(true,LSParseNumber(number:1));
	}

	public void function testLSParseDateTime(){
		assertEquals("{ts '2000-01-02 03:04:05'}",LSParseDateTime(date:dt));

		assertEquals("{ts '2000-01-02 03:04:05'}",LSParseDateTime(date:dt,timezone:tz));
		assertEquals("{ts '2000-01-02 03:04:05'}",LSParseDateTime(date:dt,timezone:"UTC"));

		assertEquals("{ts '2000-01-02 03:04:05'}",LSParseDateTime(date:dt,locale:loc));
		assertEquals("{ts '2000-01-02 03:04:05'}",LSParseDateTime(date:dt,locale:"de_CH"));
	}


	public void function testMonthAsString(){
		assertEquals("Januar",MonthAsString(number:1,locale:loc));
		assertEquals("Januar",MonthAsString(number:1,locale:"de_CH"));
		assertEquals("Januar",MonthAsString(number:1));
	}

	public void function testTagApplication(){
		application action="update" timezone="UTC";
		application action="update" timezone="#tz#";

		application action="update" locale="de_CH";
		application action="update" locale="#loc#";
	}

	public void function testTagQuery(){
		var qry=query(a:[1,2]);
		query dbtype="query" timezone="UTC" {
			echo("select * from qry");
		}
		query dbtype="query" timezone="#tz#" {
			echo("select * from qry");
		}
	}

} 
</cfscript>