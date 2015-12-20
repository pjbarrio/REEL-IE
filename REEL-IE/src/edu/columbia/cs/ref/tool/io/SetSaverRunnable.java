package edu.columbia.cs.ref.tool.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Set;

public class SetSaverRunnable<T> implements Runnable {

	private String file;
	private Set<T> cs;

	public SetSaverRunnable(String file,
			Set<T> cs) {
		this.file = file;
		this.cs = cs;
	}

	@Override
	public void run(){
		
		try {
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(cs);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
