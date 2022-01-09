package org.jeecg.modules.system.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	
	  public static Date getCurrentDate(){
		  return new Date(System.currentTimeMillis());
	  }
	  
	  public static String DateToString(Date date) {
		  	if(date == null){
		  		return "";
		  	}
		  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  	return sdf.format(date);
	 }
}
