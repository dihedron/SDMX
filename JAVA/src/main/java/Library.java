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

import java.io.IOException;
import java.io.InputStream;

import org.dihedron.core.License;
import org.dihedron.core.properties.Properties;
import org.dihedron.core.properties.PropertiesException;
import org.dihedron.core.streams.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@License
public abstract class Library {
	
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Library.class);

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
	protected Library(String name) {
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
}
