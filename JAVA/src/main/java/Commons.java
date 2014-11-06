/**
 * Copyright (c) 2012-2014, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

import org.dihedron.core.License;
import org.dihedron.core.library.Library;
import org.dihedron.core.library.Traits;


/**
 * @author Andrea Funto'
 */
@License
public class Commons extends Library {
	
	/**
	 * The name of the library.
	 */
	private static final String LIBRARY_NAME = "commons";
	
	/**
	 * The single instance.
	 */
	private static Commons singleton = new Commons();

	/**
	 * Constructor.
	 */
	private Commons() {
		super(LIBRARY_NAME);
	}
	
	/**
	 * Returns the value of the give trait.
	 * 
	 * @param trait
	 *   the trait to retrieve.
	 * @return
	 *   the value of the trait.
	 */
	public static String valueOf(Traits trait) {
		synchronized(Commons.class) {
			if(singleton == null) {
				singleton = new Commons();
			}
		}
		return singleton.get(trait);
	}
}
