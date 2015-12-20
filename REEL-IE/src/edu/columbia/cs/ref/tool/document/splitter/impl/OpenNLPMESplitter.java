package edu.columbia.cs.ref.tool.document.splitter.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Segment;
import edu.columbia.cs.ref.model.Sentence;
import edu.columbia.cs.ref.tool.document.splitter.SentenceSplitter;

/**
 * The Class OpenNLPMESplitter is an implementation of a sentence splitter that uses
 * the SentenceDetectorME models from <a href="http://incubator.apache.org/opennlp/">OpenNLP</a>.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenNLPMESplitter implements SentenceSplitter {

	/** The sentence detector. */
	private SentenceDetectorME sentenceDetector;
	
	/**
	 * Instantiates a OpenNLP sentence splitter.
	 *
	 * @param path the path to the model to be used.
	 * @throws FileNotFoundException the file not found exception
	 */
	public OpenNLPMESplitter(String path) throws FileNotFoundException{
		InputStream modelIn = new FileInputStream(path);
		SentenceModel model=null;
		try {
			model = new SentenceModel(modelIn);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
		}

		sentenceDetector = new SentenceDetectorME(model);
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.tool.document.splitter.SentenceSplitter#split(edu.columbia.cs.ref.model.Document)
	 */
	@Override
	public Sentence[] split(Document d) {
		List<Sentence> sents = new ArrayList<Sentence>();
		for(Segment seg : d.getPlainText()){
			Span[] sentsSpans = sentenceDetector.sentPosDetect(seg.getValue());
			for(Span s : sentsSpans){
				int startSpan = seg.getOffset()+s.getStart();
				int endSpan = seg.getOffset()+s.getEnd();
				Sentence newSent = new Sentence(d, startSpan, endSpan-startSpan);
				sents.add(newSent);
			}
		}
		
		Sentence[] result = new Sentence[sents.size()];
		return sents.toArray(result);
	}

}
