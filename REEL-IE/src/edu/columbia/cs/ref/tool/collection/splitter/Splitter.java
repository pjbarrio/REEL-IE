package edu.columbia.cs.ref.tool.collection.splitter;

import java.io.File;

import edu.columbia.cs.ref.model.Dataset;
import edu.columbia.cs.ref.tool.io.Writable;

/**
 * Interface for a Splitter
 * 
 * <br>
 * <br>
 * 
 * A splitter is a utility that is not directly used in a relationship extraction process
 * but that is very useful for evaluation purposes.
 * 
 * <br>
 * <br>
 * 
 * It is expected that a class that implements splitter separates a dataset into several
 * folds in order for it to be used on cross-validation
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public abstract class Splitter<E extends Writable> {
	
	/**
	 * 
	 * Given a dataset, this method is responsible for creating the folds for it. The results
	 * are written to the folder given as input
	 * 
	 * @param dataset dataset to be splitted
	 * @param outputFolder folder where the fold files will be written
	 */
	public abstract void split(Dataset<E> directory, File outputFolder);
}
