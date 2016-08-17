package be.viaa.util;

import com.google.gson.Gson;

/**
 * Support class for JSON
 * 
 * @author Hannes Lowette
 *
 */
public class GsonUtil {
	
	/**
	 * The GSON object to convert POJO to JSON.
	 */
	private static final Gson GSON = new Gson();

	/**
	 * Converts a POJO to a valid JSON message represented as a byte array
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] convert(Object object) {
		return GSON.toJson(object).getBytes();
	}

}
