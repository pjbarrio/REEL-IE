package edu.columbia.cs.ref.tool.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Set;

import edu.columbia.cs.ref.model.core.structure.OperableStructure;

public class CoreReader {
	public static Set<OperableStructure> readOperableStructures(String output) throws IOException, ClassNotFoundException{
		InputStream file = new FileInputStream( output );
		InputStream buffer = new BufferedInputStream( file );
		ObjectInput input = new ObjectInputStream ( buffer );
		Set<OperableStructure> candidates = (Set<OperableStructure>)input.readObject();
		input.close();
		return candidates;
	}

	public Set<OperableStructure> readOperableStructuresInstance(String output)  throws IOException, ClassNotFoundException{
		InputStream file = new FileInputStream( output );
		InputStream buffer = new BufferedInputStream( file );
		ObjectInput input = new ObjectInputStream ( buffer );
		Set<OperableStructure> candidates = (Set<OperableStructure>)input.readObject();
		input.close();
		return candidates;
	}
}
