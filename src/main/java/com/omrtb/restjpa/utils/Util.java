package com.omrtb.restjpa.utils;

import java.io.FileReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import com.omrtb.restjpa.entity.model.User;
import com.opencsv.CSVReader;

public class Util {
	public static ZoneId zoneId = ZoneId.of( "Asia/Kolkata" );        //Zone information
	public static ZonedDateTime convertLocalDateToZonedDate(LocalDateTime localDate) {
		 
		 
		ZonedDateTime zdtAtAsia = localDate.atZone( zoneId );     //Local time in Asia timezone
		 
		//ZonedDateTime zdtAtET = zdtAtAsia
		//        .withZoneSameInstant( ZoneId.of( "America/New_York" ) ); //Sama time in ET timezone
		
		return zdtAtAsia;
	}
	public static ZonedDateTime convertDateToZonedDate(Date date) {
		ZonedDateTime zonedDate = ZonedDateTime.ofInstant(date.toInstant(),
				zoneId);
		return zonedDate;
	}
	public static LocalDateTime convertZonedDateToLocalDate(ZonedDateTime zonedDate) {
		ZonedDateTime swissZoned = zonedDate.withZoneSameInstant(zoneId);
		LocalDateTime localDate = swissZoned.toLocalDateTime();
		return localDate;
	}
	public static ZonedDateTime changeZoneID(ZonedDateTime zonedDate) {
		ZonedDateTime zdtAtAsia = zonedDate.withZoneSameInstant( ZoneId.of( "Asia/Calcutta" ) );     //Local time in Asia timezone
		return zdtAtAsia;
		
	}

	public static String secondsToHoursMinutesSecondsConverter(Integer seconds) {
		if(seconds!=null) {
			NumberFormat nf = new DecimalFormat("00");
	        int p1 = seconds.intValue() % 60;
	        int p2 = seconds.intValue() / 60;
	        int p3 = p2 % 60;
	        p2 = p2 / 60;
	        return nf.format(p2)+":"+nf.format(p3)+":"+nf.format(p1);
		}
		else {
			return "00:00:00";
		}
	}
	
	public static Integer hoursMinutesSecondsToSecondsConverter(String hms) {
		if(hms!=null && !hms.isEmpty()) {
			String[] hmsArray = hms.split(":");
	        int h = Integer.parseInt(hmsArray[0]);
	        int m = Integer.parseInt(hmsArray[1]);
	        int s = Integer.parseInt(hmsArray[2]);
	        return (h*60*60)+m*60+s;
		}
		else {
			return 0;
		}
	}
	public static void main(String[] args) {
		importUsers("C:\\Users\\DELL\\git\\commitWs\\omrtb-application\\restjpa\\src\\main\\resources\\omrtb.csv");
	}
	public static void importUsers(String file) {
	    try { 
	    	  
	        // Create an object of filereader 
	        // class with CSV file as a parameter. 
	        FileReader filereader = new FileReader(file); 
	  
	        // create csvReader object passing 
	        // file reader as a parameter 
	        CSVReader csvReader = new CSVReader(filereader); 
	        String[] nextRecord;
	        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	  
	        // we are going to read data line by line 
	        int rec =0;
	        while ((nextRecord = csvReader.readNext()) != null) {
	        	int i = 0;
	        	rec++;
	            for (String cell : nextRecord) {
	                //System.out.print(cell + ",");
	                i++;
	            }
	            if(rec!=1 && nextRecord.length==24) {
	            User user = new User();
	            user.setAddress(nextRecord[0]);
	            user.setBloodgroup(nextRecord[2]);
	            user.setCycling("1".equals(nextRecord[4]));
	            user.setDob(new java.sql.Date(df.parse(nextRecord[5]).getTime()));
	            user.setEmail(nextRecord[6]);
	            user.setGender(nextRecord[7]);
	            user.setMobile(nextRecord[8]);
	            user.setName(nextRecord[9]);
	            try {
	            	user.setPincode(new BigDecimal(Integer.parseInt(nextRecord[11])));
	            }
	            catch (Exception e) {
					// TODO: handle exception
	            	System.out.println(i+", "+nextRecord[11]);
				}
	            user.setTshirt(nextRecord[14]);
	            user.setVenue(nextRecord[17]);
	            System.out.println(user);
	            }
	        }
	        csvReader.close();
	        filereader.close();
	    } 
	    catch (Exception e) { 
	        e.printStackTrace(); 
	    }
	}
}
