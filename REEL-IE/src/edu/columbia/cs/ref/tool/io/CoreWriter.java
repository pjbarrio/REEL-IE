package edu.columbia.cs.ref.tool.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import edu.stanford.nlp.util.Pair;

import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;

public class CoreWriter {
	
	private static List<Pair<Set<OperableStructure>,String>> preparedList;
	
	public synchronized static void writeOperableStructures(Set<OperableStructure> candidates, String output) throws IOException{
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(output));
		out.writeObject(candidates);
		out.close();
	}

	public synchronized static void prepareOperableStructures(Set<OperableStructure> candidates,
			String file) {
		getPreparedList().add(new Pair<Set<OperableStructure>,String>(candidates,file));
	}
	
	private synchronized static List<Pair<Set<OperableStructure>,String>> getPreparedList(){
		if (preparedList == null){
			preparedList = new ArrayList<Pair<Set<OperableStructure>,String>>();
		}
		return preparedList;
	}
	
	public synchronized static void writeOperableStructures(){
		
		List<Thread> ts = new ArrayList<Thread>(getPreparedList().size());
		
		for(int i = 0; i < getPreparedList().size(); i++){
			Thread t = new Thread(new SetSaverRunnable<OperableStructure>(getPreparedList().get(i).second(), getPreparedList().get(i).first()));
			
			ts.add(t);
			
		}
		
		for (Thread t : ts){
			t.start();
		}
		
		for (Thread t : ts){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		getPreparedList().clear();
		
		System.gc();
	}
	
}
