/**
 * 
 */
package com.iotcore.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class StringUtil {
	
	private static final String SYSTEM_LINESEPARATOR = "line.separator";
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	private static String lineSeparator = System.getProperty(SYSTEM_LINESEPARATOR);


	/**
	 * Convert a byte array to a printable hex string
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	
	/**
	 * @param str
	 * @return true si str es null o es una cadena vacía 
	 */
	public static Boolean isBlank(String str) {
		if ((str == null) || str.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param str
	 * @return true si str es null o es una cadena vacía 
	 */
	public static String trim(String str) {
		if ((str == null) || str.isEmpty()) {
			return null;
		}
		return str.replaceAll(" ", "");
	}
	
	/**
	 * @param str
	 * @param sep 
	 * @return true si str es null o es una cadena vac�a 
	 */
	public static String[] splitTrim(String str, String sep) {
		String[] ret = null;
		if ((str == null) || (sep == null)) {
			return null;
		}
		
		ret = str.split(sep);
		for (int i=0;i<ret.length;i++) {
			ret[i] = ret[i].trim();
		}
		return ret;
	}
	
	
	public static String indent(int width, String prefix, String text) {
		String format = "%-" + width + "s%s%n";
		return String.format(format, prefix, text);
		
	}
	
	/**
	 * @param str
	 * @param sep 
	 * @return true si str es null o es una cadena vac�a 
	 */
	public static List<String> splitTrimList(String str, String sep) {
		List<String> ret = null;
		if ((str == null) || (sep == null)) {
			return null;
		}
		
		ret = new ArrayList<String>();
		String[] array = str.split(sep);
		for (String s:array) {
			ret.add(s.trim());
		}
		return ret;
	}
	
	
	/**
	 * Returns the String representation of this object when it is not null
	 * If it is null then this method also returns null
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		if ((obj == null)) {
			return null;
		}
		else {
			return obj.toString();
		}
	}
	
	
	public static Number toNumber(String str) {
		
		if (str == null) {
	        return null;
	    }
	    
		Number ret = null;
		
		try {
			return Long.parseLong(str);
	    } catch (NumberFormatException nfe) {}
		
		try {
			return Integer.parseInt(str);
	    } catch (NumberFormatException nfe) {}
		
		try {
			return Float.parseFloat(str);
	    } catch (NumberFormatException nfe) {}
		
		try {
			return Double.parseDouble(str);
	    } catch (NumberFormatException nfe) {}
		
		
	    return ret;
		
		
	}
		
	/**
	 * Returns the lastDigits last string digits
	 * @param str
	 * @param lastDigits
	 * @return
	 */
	public static String last(String str, int lastDigits) {
		String hexStr = null;
		Long hexId = null;
		int sz = str.length();
		if (isBlank(str)) {
			return null;
		}
		else if (str.length() >= lastDigits) {
			hexStr = str.substring(sz - lastDigits);
		}
		else {
			hexStr = str;
			
		}
		
		try {
			hexId = Long.parseLong(hexStr, 16);
		}
		catch (NumberFormatException e) {
			return null;
		}
		
		return String.format("%0" + lastDigits + "x", hexId);
		
	}
	
	
	/**
	 * @param data
	 * @return
	 */
	public static String toBase64(byte [] data) {
		 return new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8);
	}
	
	/**
	 * @param b64Data
	 * @return
	 */
	public static byte [] fromBase64(String b64Data) {
		 return Base64.getDecoder().decode(b64Data);
	}
	
	public static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer("0x");
        while(sb.length() < numchars){
        	sb.append(String.format("%08x", r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
	
	/**
	 * @param dir
	 * @return
	 */
	public static File [] getImagesFromDirectory(File dir) {
		return dir.listFiles( new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".jpg")) {
						return true;
					} else {
						return false;
					}
				}
			});
		
	}
	
	/**
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static String readB64FromFile(File f) throws IOException {
		byte[] aux = new byte[(int) f.length()];
	    DataInputStream dis = new DataInputStream(new FileInputStream(f));
	    dis.readFully(aux);
	    dis.close();
	    return new String(Base64.getEncoder().encode(aux), StandardCharsets.UTF_8);
	}
	
	
	/**
	 * @param f
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String readFromFile(File f) {
		try {
			return readFromFile(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * @param istream
	 * @return
	 */
	public static String readFromFile(InputStream istream) {
		String ret = null;
		try {
			ret = new String(istream.readAllBytes());
		} catch (IOException e) {
			// LOG.error("Exception: {}", e.getMessage());
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * @param r
	 * @return
	 */
	public static String readFromFile(Reader r) {
		String ret = null;
	    BufferedReader buffReader = new BufferedReader(r);
		try {
			String	line = null;
			StringBuilder  stringBuilder = new StringBuilder();
			while((line = buffReader.readLine()) != null) {
				stringBuilder.append(line);
		        stringBuilder.append(lineSeparator);
			}
		    ret = stringBuilder.toString();
		    buffReader.close();
		} catch (IOException e) {} 
		
		return ret;
	    
	}
	
	
	public static boolean equalsOneOf(String str, String[] array) {
		
		for (String comp:array) {
			if (str.equals(comp)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param str
	 * @param file
	 * @return
	 */
	public static File writeToFile(String str, File file) {
		File ret = null;
		try {
	    	BufferedWriter writer = new BufferedWriter(new FileWriter (file));
	    	writer.write(str);
	    	writer.close();
	    	ret = file;
	    } catch (IOException e) {} 
		return ret;
	}
	
	/**
	 * @param str
	 * @return
	 */
	public static InputStream toInputStream(String str) {
		InputStream ret = null;
		ret = new ByteArrayInputStream(str.getBytes());
		return ret;
	}

	
	/**
	 * @param unformattedJsonString 
	 * @return
	 */
	public  String jsonPretty(String unformattedJsonString) {
		  StringBuilder prettyJSONBuilder = new StringBuilder();
		  int indentLevel = 0;
		  boolean inQuote = false;
		  for(char charFromUnformattedJson : unformattedJsonString.toCharArray()) {
		    switch(charFromUnformattedJson) {
		      case '"':
		        // switch the quoting status
		        inQuote = !inQuote;
		        prettyJSONBuilder.append(charFromUnformattedJson);
		        break;
		      case ' ':
		        // For space: ignore the space if it is not being quoted.
		        if(inQuote) {
		          prettyJSONBuilder.append(charFromUnformattedJson);
		        }
		        break;
		      case '{':
		      case '[':
		        // Starting a new block: increase the indent level
		        prettyJSONBuilder.append(charFromUnformattedJson);
		        indentLevel++;
		        appendIndentedNewLine(indentLevel, prettyJSONBuilder);
		        break;
		      case '}':
		      case ']':
		        // Ending a new block; decrese the indent level
		        indentLevel--;
		        appendIndentedNewLine(indentLevel, prettyJSONBuilder);
		        prettyJSONBuilder.append(charFromUnformattedJson);
		        break;
		      case ',':
		        // Ending a json item; create a new line after
		        prettyJSONBuilder.append(charFromUnformattedJson);
		        if(!inQuote) {
		          appendIndentedNewLine(indentLevel, prettyJSONBuilder);
		        }
		        break;
		      default:
		        prettyJSONBuilder.append(charFromUnformattedJson);
		    }
		  }
		  return prettyJSONBuilder.toString();
		}

	
		/**
		 * Print a new line with indention at the beginning of the new line.
		 * @param indentLevel
		 * @param stringBuilder
		 */
		private static void appendIndentedNewLine(int indentLevel, StringBuilder stringBuilder) {
		  stringBuilder.append("\n");
		  for(int i = 0; i < indentLevel; i++) {
		    // Assuming indention using 2 spaces
		    stringBuilder.append("  ");
		  }
		}
		

}
