package edu.columbia.cs.ref.tool.loader;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class that represents a generic loader
 *
 * @param <E> the type of data to be loaded
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class Loader<E> {
	
	/**
	 * Method that loads a set of objects given a collection of Files that represent the directories of the collection
	 *
	 * @param files Represents the directories of the collection
	 * @return a set of objects of a collection
	 */
	public Set<E> load(Collection<File> files){
		Set<E> documents = new HashSet<E>();
		for(File f : files){
			documents.addAll(load(f));
		}
		return documents;
	}
	
	/**
	 * Method that loads a set of objects given a File that represents the directory of the collection
	 * 
	 * @param file Represents the directory of the collection
	 * @return a set of objects of a collection
	 */
	public abstract Set<E> load(File files);
}
