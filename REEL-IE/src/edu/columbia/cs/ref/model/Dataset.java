package edu.columbia.cs.ref.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.columbia.cs.ref.tool.loader.Loader;

/**
 * The Class Dataset represents a dataset of a given type of atomic elements (e.g., Documents, Candidate Sentences).
 *
 * <br>
 * <br>
 * 
 * This class implements all the methods that allow to iterate over the dataset
 * as well as load it.
 *
 * @param <E> the atomic element of the dataset
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class Dataset<E> implements Iterable<E>{

	/** The dataset. */
	private Set<E> dataset;
	
	/**
	 * Instantiates an empty dataset.
	 */
	public Dataset(){
		dataset=new HashSet<E>();
	}
	
	/**
	 * Instantiates a new dataset from a given collection of atomic elements.
	 *
	 * @param list the collection of atomic elements
	 */
	public Dataset(Collection<E> list){
		dataset=new HashSet<E>();
		dataset.addAll(list);
	}
	
	/**
	 * Instantiates a new dataset by using a specific loader and a file.
	 * 
	 * <br>
	 * <br>
	 * 
	 * The final parameter indicates whether the file used to load the dataset
	 * is a text document containing the paths for the elements of the dataset
	 * or if it is a file to be loaded.
	 *
	 * @param l the loader that will be used to load the dataset
	 * @param f the file to be loaded
	 * @param isListOfFiles true if f is a text document containing the paths for the elements of the dataset
	 */
	public Dataset(Loader<E> l, File f, boolean isListOfFiles){
		dataset=new HashSet<E>();
		if(f.isDirectory()){//Folder to the dataset
			File[] list = f.listFiles();
			dataset.addAll(l.load(Arrays.asList(list)));
		}else if(isListOfFiles){
			dataset.addAll(l.load(getFiles(f)));
		}else{
			dataset.addAll(l.load(f));
		}
	}
	
	/**
	 * Adds the input object to the dataset
	 *
	 * @param input the new element of the dataset
	 */
	public void add(E input){
		dataset.add(input);
	}
	
	/**
	 * Adds all the objects in the in the input collection to the dataset
	 *
	 * @param inputs the new elements of the dataset
	 */
	public void addAll(Collection<E> inputs){
		dataset.addAll(inputs);
	}
	
	/**
	 * Adds the all the elements that are in the input dataset to the current dataset
	 *
	 * @param inputs the dataset to be added
	 */
	public void addAll(Dataset<E> inputs){
		dataset.addAll(inputs.dataset);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return dataset.iterator();
	}
	
	/**
	 * The number of objects in the dataset
	 *
	 * @return the number of objects in the dataset
	 */
	public int size(){
		return dataset.size();
	}
	
	private static List<File> getFiles(File f){
		List<File> entries = new ArrayList<File>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String input;
			while ((input = in.readLine()) != null) {
				entries.add(new File(input));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return entries;
	}
}
