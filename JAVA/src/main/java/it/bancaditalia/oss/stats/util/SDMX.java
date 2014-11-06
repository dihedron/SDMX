/* Copyright 2010,2014 Bank Of Italy
*
* Licensed under the EUPL, Version 1.1 or - as soon they
* will be approved by the European Commission - subsequent
* versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the
* Licence.
* You may obtain a copy of the Licence at:
*
*
* http://ec.europa.eu/idabc/eupl
*
* Unless required by applicable law or agreed to in
* writing, software distributed under the Licence is
* distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied.
* See the Licence for the specific language governing
* permissions and limitations under the Licence.
*/
package it.bancaditalia.oss.stats.util;

import it.bancaditalia.oss.stats.sdmx.client.SdmxClientHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Attilio Mattiocco
 *
 */
public class SDMX {
	
	/**
	 * The name of the file from which the library properties will be loaded.
	 */
	private static final String PROPERTIES_FILE = "classpath:${library}.properties";
			
	/**
	 * The name of the library.
	 */
	private String name; 
	
	/**
	 * The overall library properties.
	 */
	private final Properties properties = new Properties();
	
	/**
	 * Constructor.
	 *
	 * @param name
	 *   the name of the library; the "&lt;library&gt;.properties" (where <&lt;library&gt;
	 *   is the name of the library) will be loaded from the root of the classpath.
	 */
	public SDMX(String name) {
		this.name = name;
		InputStream stream = null;
		try {
			String path = PROPERTIES_FILE.replaceAll("\\$\\{library\\}", name);
			logger.trace("loading from '{}'", path);
			stream = Streams.fromURL(path);
			properties.load(stream);
		} catch (IOException e) {
			logger.error("error loading library properties from input stream", e);
		} catch (PropertiesException e) {
			logger.error("error loading library properties from input stream", e);
		} finally {
			if(stream != null) {
				try {
					stream.close();
					stream = null;
				} catch(IOException e) {
					logger.error("error closing library properties stream", e);
				}
			}
		}		
	}
	
	/**
	 * Returns the given value from the properties file.
	 * 
	 * @param key
	 *   the name of the property to be retrieved.
	 * @return
	 *   the value of the property.
	 */
	public String get(Traits key) {
		return properties.get(key.toString(name));
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length < 3){
			System.out.println("usage: SDMX <provider> <method> <query>");
		}
		else{
			String provider = args[0].toUpperCase();
			String method = args[1];
			String query = args[2];
			//SdmxClientHandler handler = SdmxClientHandler.getInstance();
			
			try {
				if("getDimensions".equalsIgnoreCase(method)){
					System.out.println(SdmxClientHandler.getDimensions(provider, query));			
				}
				else if("getFlows".equalsIgnoreCase(method)){
					System.out.println(SdmxClientHandler.getFlows(provider, query));
				}
				else if("getTimeSeries".equalsIgnoreCase(method)){
					System.out.println(SdmxClientHandler.dumpTimeSeries(provider, query, null, null));
				}
			} catch (SdmxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
