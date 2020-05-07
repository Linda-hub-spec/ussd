package com.neptunesoftware.reuseableClasses;

import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;



//import org.eclipse.jetty.util.ajax.JSON;
import org.xml.sax.InputSource;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class CommonMethods {

	public static String getInfo(String name, Class<?> clazz) {
		try {
			InputStream is = clazz.getResourceAsStream(name);
			byte[] data = new byte[is.available()];
			is.read(data);
			is.close();
			String content = new String(data);
			return content;
		} catch (Exception ex) {
			System.out.println("Input File Not Found");
			//ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private static String convertToString(Object object, String tag) {
		String text = "", items = "", prevVal = "";
		Class<?> beanClass = !isBlank(object) ? object.getClass() : String.class;
		try {
			if (isBlank(object)) {
				return !isBlank(tag) ? (tag + "=<" + String.valueOf(object) + ">") : String.valueOf(object);
			}
			for (MethodDescriptor methodDescriptor : Introspector.getBeanInfo(beanClass).getMethodDescriptors()) {
				if ("toString".equalsIgnoreCase(methodDescriptor.getName())
						&& beanClass == methodDescriptor.getMethod().getDeclaringClass()) {
					return !isBlank(tag) ? (tag + "=<" + String.valueOf(object) + ">") : String.valueOf(object);
				}
			}
			if (object instanceof byte[]) {
				return !isBlank(tag) ? (tag + "=<" + new String((byte[]) object) + ">") : new String((byte[]) object);
			}

			tag = isBlank(tag) ? beanClass.getSimpleName() : tag;

			if (object instanceof Collection) {
				for (Object item : ((Collection) object).toArray()) {
					items += (isBlank(items) ? "" : ", ") + (prevVal.contains("\r\n") ? "\r\n" : "")
							+ (prevVal = convertToString(item, null)).trim();
				}
				return items.contains("\r\n") ? ("\r\n" + tag + "=<[" + "\r\n\t" + items + "\r\n" + "]>")
						: (tag + "=<[" + (!isBlank(items) ? " " + items + " " : "") + "]>");
			} else if (object instanceof Map) {
				for (Object key : ((Map) object).keySet()) {
					items += (isBlank(items) ? "" : ", ") + (prevVal.contains("\r\n") ? "\r\n" : "")
							+ (prevVal = convertToString(((Map) object).get(key), String.valueOf(key))).trim();
				}
				return items.contains("\r\n") ? ("\r\n" + tag + "=<[" + "\r\n\t" + items + "\r\n" + "]>")
						: (tag + "=<[" + (!isBlank(items) ? " " + items + " " : "") + "]>");
			} else if (beanClass.isArray()) {
				for (Object item : (Object[]) object) {
					items += (isBlank(items) ? "" : ", ") + (prevVal.contains("\r\n") ? "\r\n\t" : "")
							+ (prevVal = convertToString(item, null)).trim();
				}
				return items.contains("\r\n") ? ("\r\n" + tag + "=<[" + "\r\n\t" + items + "\r\n" + "]>")
						: (tag + "=<[" + (!isBlank(items) ? " " + items + " " : "") + "]>");
			} else {
				Method readMethod;
				for (PropertyDescriptor propertyDesc : Introspector.getBeanInfo(beanClass).getPropertyDescriptors()) {
					if ((readMethod = propertyDesc.getReadMethod()) != null) {
						Object value = readMethod.invoke(object);
						if (!(value instanceof Class)) {
							text += (isBlank(text) ? "" : ", ") + (prevVal.contains("\r\n") ? "\r\n" : "")
									+ (prevVal = convertToString(value, propertyDesc.getName()));
						}
					}
				}
			}
		} catch (Exception ex) {
			System.out.println("Request Can Not Be Converted");
			//ex.printStackTrace();
		}
		return (!isBlank(tag) ? ("\r\n\t" + tag + "=<[ " + (isBlank(text) ? String.valueOf(object) : text) + " ]>")
				: (isBlank(text) ? String.valueOf(object) : text));
	}

	public static boolean isBlank(Object object) {
		return object == null || "".equals(String.valueOf(object).trim())
				|| "null".equals(String.valueOf(object).trim()) || "---select---".equals(String.valueOf(object).trim());
	}

	public static String convertToString(Object object) {
		String result = convertToString(object, null).trim();

		return result;
	}

	public static void initialMessage(String methodName, Object requestObject) {
		System.out.println("In " + methodName + " post method");
		System.out.println("Request: " + requestObject);
		System.out.println("Response loading...");
	}

	public static void showResponse(Object responseObject) {
		System.out.println("Response: " + responseObject);
		System.out.println(convertToString(responseObject));
	}

	@SuppressWarnings("unchecked")
	public static <T> T xmlStringToObject(String xmlString, Class<?> clazz) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StreamSource streamSource = new StreamSource(new StringReader(xmlString));
		JAXBElement<T> jxb = (JAXBElement<T>) unmarshaller.unmarshal(streamSource, clazz);
		return jxb.getValue();
	}
	
	public static String objectToXml(Object response) {
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);

		String xmlString = "";
		try {
			JAXBContext contextObj = JAXBContext.newInstance(response.getClass());
			Marshaller marshallerObj = contextObj.createMarshaller();
			marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshallerObj.marshal(response, result);
			// marshallerObj.marshal(integratorResponse, System.out);
			xmlString = readFromStringWriter(stringWriter.getBuffer().toString());
		} catch (Exception e) {
		}

		// JAXB.unmarshal(new StringReader(body), CustomerInformationRequest.class);
		// //or simply use this
		return xmlString;
	}

	public static Object JSONStringToObject(String jsonStr, Class<?> clazz) {
		Gson gson = new Gson();
		Object JavaObject = gson.fromJson(jsonStr, clazz);
		return JavaObject;
	}

	public static String ObjectToJsonString(Object clazz) {
		// Convert object to JSON string
		Gson gson = new GsonBuilder()
				//.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.setPrettyPrinting()
				.create();
//		Gson gson = new Gson();
		String json = gson.toJson(clazz);
		return json;
	}
	
	public static String zeroPadding(String valueToPad, int neededSignificantValue, boolean isBefore) {
		
		String zeros = "", padedValue = valueToPad;
		while (neededSignificantValue-- > 0) {
			zeros += "0";
		}

		padedValue = zeros + valueToPad;
		
		if (!isBefore) {
			padedValue += zeros;
		}
		
		return padedValue;		
	}
	
//	public static void main(String[] arg) {
//		System.out.println(zeroPadding("1", 3 - "1".length(), true));
//	}

	// Should return an object
	public static Object JSONOrXMLToObject(String message, Class<?> clazz) throws Exception {
		Object messageObject = null;

		if (IsJson(message) == true) {
			// call the JSON to Object method.
			messageObject = JSONStringToObject(message, clazz);
		} else if (IsXml(message) == true) {
			// call the XML to Object method
			messageObject = xmlStringToObject(message, clazz);
		} else {
			// Return a message string.
			System.out.println( "The Request is Invalid : " + message);
		}

		return messageObject;
		// At the end of each methods, call the MEdiaType(Object to JSON or XML) method
		// to return result in the IntegrationSoapImpl class.
	}

	// Should return a String. Either an XML or JSON format.
	public static String ObjectToJSONOrXMLstring(String request, Object response) throws Exception {
		String messageObject = null;

		if (IsJson(request) == true) {
			// call the JSON to Object method.
			messageObject = ObjectToJsonString(response);
			System.out.println("Object is converted to JSON");
			
			return messageObject;
		}

		if (IsXml(request) == true) {
			// call the XML to Object method
			messageObject = objectToXml(response);
			System.out.println("Object is converted to XML");
			
			return messageObject;
		}

		return messageObject;
		// At the end of each methods, call this MEdiaType(Object to JSON or XML) method
		// to return result in the IntegrationSoapImpl class.
	}

	
	// Checks if the request is JSON format
	  public static boolean IsJson(String jsonInString) {
		  Gson gson = new Gson();
	      try {
	          gson.fromJson(jsonInString, Object.class);
	          return true;
	      } catch(JsonSyntaxException ex) { 
	          return false;
	      }
	  }
	  
	// Checks if the request is XML format
	public static boolean IsXml(String message) throws Exception {
		if (message.startsWith("<")) {
			try {
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(message)));
				// System.out.println("Message is valid XML.");
			} catch (Exception e) {
				// System.out.println("Message is not valid XML.");
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	private static String readFromStringWriter(String inputString)  {
		StringReader stringReader = new StringReader(inputString);
		StringBuilder builder = new StringBuilder();

		int charsRead = -1;
		char[] chars = new char[100];
		do {
			try {
				charsRead = stringReader.read(chars, 0, chars.length);
			} catch (IOException e) {}
			
			// if we have valid chars, append them to end of string.
			if (charsRead > 0)
				builder.append(chars, 0, charsRead);
		} while (charsRead > 0);

		return builder.toString();
	}
		
    public static String koboToNaira(int amountInKobo) {
    	int amountInNaira = amountInKobo / 100;
    	return Integer.toString(amountInNaira);
    }
    
    public static String nairaToKobo(int amountInNaira) {
    	int amountInKobo  = amountInNaira * 100;
    	return Integer.toString(amountInKobo);
    }
	
	public static String SHA512(String valueToHash) {
		return SHAHashValue(valueToHash, "SHA-512");
	}

	private static String SHAHashValue(String cipherValue, String algorithm) {
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			// md.update(salt);
			byte[] bytes = md.digest(cipherValue.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}  

	
	
	private static Date getCurrentDate() {
	    java.util.Date today = new java.util.Date();
	    return today;
	}
	
	public static String getCurrentDate(String inputDate, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		String formattedDate = "";
		formattedDate = sdf.format(javaDate);
		
		try {
			if (inputDate.trim().length() > 0)
				formattedDate = sdf.format(sdf.parse(inputDate));
		} catch (ParseException e) {
		}
	    return formattedDate;
	}

	public static String addMonths(String dateAsString, String dateFormat, int months) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		//String formattedDate = sdf.format(javaDate);

		try {
			javaDate = sdf.parse(dateAsString);
		} catch (ParseException e) {}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(javaDate);
		cal.add(Calendar.MONTH, months);
		Date dateAsObjAfterAMonth = cal.getTime();
		return sdf.format(dateAsObjAfterAMonth);
	}
	
	public static void logContent(String content) {		
		try {
			
			String todayDate = CommonMethods.getCurrentDate("", "dd-MM-yyyy");
			String fileName = "Integrator-" + todayDate + ".log";
			
			FileOutputStream file = new FileOutputStream(fileName, true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, StandardCharsets.UTF_8);
			Writer writer = new BufferedWriter(outputStreamWriter);
			writer.write(getCurrentDate("", "dd-MMM-yyyy hh:mm:ss:ssss"));
			writer.write("\r\n" + content + "\r\n\r\n");
			
			writer.close();
			outputStreamWriter.close();
			file.close();
		} 
		catch (Exception ex) {
			System.out.println("contents not logged!");
		}		
	}
	
	public static void main(String[] args) {
		DBConnection oracleDB = new DBConnection("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521/orclpdb", "neptunelive",
				"neptune", "ORACLE");
		
		String processingDate = "";
		try {
			processingDate = oracleDB.selectProcessingDate();
		} catch (Exception e) {
		}
		String startDate = CommonMethods.getCurrentDate(processingDate, "yyyyMMdd");
		String formattedDate = CommonMethods.getCurrentDate(processingDate, "yyyy-MM-dd");
		LocalDate date = LocalDate.parse(formattedDate).minusMonths(1);
		
		String endDate = CommonMethods.addMonths(startDate, "yyyyMMdd", -1);
		
		//System.out.println(getCurrentDate("20190730","yyyyMMdd"));
		System.out.println("processing Date: " + processingDate);
		System.out.println("start Date: " + startDate);
		System.out.println("formattedDate: " + formattedDate);
		System.out.println("date: " + date);
		System.out.println("End date: " + endDate);
	}


}
