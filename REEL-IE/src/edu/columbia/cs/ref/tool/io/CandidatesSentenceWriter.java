package edu.columbia.cs.ref.tool.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import edu.columbia.cs.ref.model.CandidateSentence;
import edu.stanford.nlp.util.Pair;


public class CandidatesSentenceWriter {
	
	
	/**
	 * Method to write a set of candidate sentences to a file. The path to the file is given as input
	 * 
	 * @param candidates the set of candidate sentences to be written
	 * @param output the path to the file where the candidate sentences will be written
	 * @throws IOException
	 */
	private static List<Pair<Set<CandidateSentence>,String>> preparedList;
	
	public synchronized static void writeCandidateSentences(Set<CandidateSentence> candidates, String output) throws IOException{
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(output));
		out.writeObject(candidates);
		out.close();
	}

	public synchronized static void prepareCandidateSentences(Set<CandidateSentence> candidates,
			String output) {
		
		getPreparedList().add(new Pair<Set<CandidateSentence>,String>(candidates,output));
		
	}

	private synchronized static List<Pair<Set<CandidateSentence>,String>> getPreparedList() {
		
		if (preparedList == null){
			preparedList = new ArrayList<Pair<Set<CandidateSentence>,String>>();
		}
		return preparedList;
	}
	
	public synchronized static void writeCandidateSentences() {
		
		List<Thread> ts = new ArrayList<Thread>(getPreparedList().size());
		
		for (int i = 0; i < getPreparedList().size();i++){
			Thread t = new Thread(new SetSaverRunnable<CandidateSentence>(getPreparedList().get(i).second(), getPreparedList().get(i).first()));
			
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
		
	}
}

