package edu.columbia.cs.ref.tool.io;

/**
 * The Interface Writable represents an object that can be written to a file.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public interface Writable {
	
	/**
	 * Obtains the name of file where the object will be written
	 *
	 * @return the name of file where the object will be written
	 */
	public String getWritableValue();
}
