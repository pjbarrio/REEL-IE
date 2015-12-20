package edu.columbia.cs.utils;
import java.io.File;
import java.io.FilenameFilter;

public class SGMFileFilter implements FilenameFilter { 
	public boolean accept(File dir, String name) { 
		return name.endsWith(".sgm"); 
	} 
}

