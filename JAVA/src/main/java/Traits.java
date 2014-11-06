/**
 * Copyright (c) 2012-2014, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

import org.dihedron.core.License;


/**
 * @author Andrea Funto'
 */
@License
public enum Traits {
	
	/**
	 * The name of the library.
	 */
	NAME("${library}.name"),
	
	/**
	 * The version of the library.
	 */
	VERSION("${library}.version"),
	
	/**
	 * The build time stamp of the library.
	 */
	TIMESTAMP("${library}.timestamp"),

	/**
	 * The version of the library.
	 */
	DESCRIPTION("${library}.description"),
	
	/**
	 * The web site of the library.
	 */
	WEBSITE("${library}.website");

	/**
	 * Returns the key of the property for the given library.
	 * 
	 * @param library
	 *   the library for which the property should be retrieved.
	 * @return
	 *   the key of the property for the given library.
	 */
	String toString(String library) {
		return trait.replaceFirst("\\$\\{library\\}", library);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param trait
	 *   the key of the property.
	 */
	private Traits(String trait) {
		this.trait = trait;
	}
	
	/**
	 * The key of the property.
	 */
	private String trait;
}
