package edu.columbia.cs.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import weka.classifiers.Classifier;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.Sentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.chunker.Chunker;
import edu.columbia.cs.ref.tool.document.splitter.SentenceSplitter;
import edu.columbia.cs.ref.tool.postagger.POSTagger;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;
import edu.columbia.cs.utils.AlternativeOpenIEConfFunction;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;

/**
 * This class is used for the implementation of the KnowItAll/Reverb unsupervised
 * relationship extraction described in 
 * <b> "Identifying Relations for Open Information Extraction" </b>. A. Fader and S. Soderland and O. Etzioni. In Conference on Empirical Methods in Natural Language Processing 2011, 2011.
 * For further information, <a href="http://reverb.cs.washington.edu/">ReVerb Website</a>.
 * 
 * <br>
 * <br>
 * 
 * This class uses the original software of ReVerb that can be found in
 * <a href="http://reverb.cs.washington.edu/">http://reverb.cs.washington.edu/</a>
 * 
 * <br>
 * <br>
 * 
 * This extractor can use a classifier to determine
 * the confidence that each result of the unsupervised learning is a relationship (ReVerb
 * approach)
 * 
 * <br>
 * <br>
 * 
 * To know more about KnowItAll or Reverb please refer to <a href="http://reverb.cs.washington.edu/">http://reverb.cs.washington.edu/</a>
 * 
 * <br>
 * @see <a href="http://reverb.cs.washington.edu/"> ReVerb Website </a>
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenIEUnsupervisedRelationshipExtractor<D extends Document> implements RelationshipExtractor<Document>{

	private SentenceSplitter splitter;
	private Tokenizer tokenizer;
	private POSTagger pos;
	private Chunker chunker;
	private Classifier cla;
	private double threshold;

	/**
	 * Constructor of the Open IE relationship extractor. This constructor does not
	 * receive a classifier nor a threshold. In this case the classifier that is used
	 * is the default classifier of ReVerb while the threshold is set to 0.5.
	 * 
	 * @param splitter the sentence splitter
	 * @param tokenizer the tokenizer
	 * @param pos the POS tagger
	 * @param chunker the NLP chunker
	 */
	public OpenIEUnsupervisedRelationshipExtractor(SentenceSplitter splitter,
			Tokenizer tokenizer,POSTagger pos,Chunker chunker){
		this(splitter,tokenizer,pos,chunker,null,0.5);
	}
	
	/**
	 * Constructor of the Open IE relationship extractor. This constructor does not
	 * receive a threshold. In this case the threshold is set to 0.5.
	 * 
	 * @param splitter the sentence splitter
	 * @param tokenizer the tokenizer
	 * @param pos the POS tagger
	 * @param chunker the NLP chunker
	 * @param cla the classifier used to compute the confidence that a given answer is a relationship
	 */
	public OpenIEUnsupervisedRelationshipExtractor(SentenceSplitter splitter,
			Tokenizer tokenizer,POSTagger pos,Chunker chunker, Classifier cla){
		this(splitter,tokenizer,pos,chunker,cla,0.5);
	}
	
	/**
	 * Constructor of the Open IE relationship extractor. This constructor does not
	 * receive a classifier. In this case the classifier that is used
	 * is the default classifier of ReVerb.
	 * 
	 * @param splitter the sentence splitter
	 * @param tokenizer the tokenizer
	 * @param pos the POS tagger
	 * @param chunker the NLP chunker
	 * @param threshold the confidence threshold to consider that a given candidate is
	 * a relationship: if the confidence of a candidate is higher than the threshold
	 * then the candidate is considered a relationship otherwise it is discarded
	 */
	public OpenIEUnsupervisedRelationshipExtractor(SentenceSplitter splitter,
			Tokenizer tokenizer,POSTagger pos,Chunker chunker, double threshold){
		this(splitter,tokenizer,pos,chunker,null,threshold);
	}

	/**
	 * Constructor of the Open IE relationship extractor.
	 * 
	 * @param splitter the sentence splitter
	 * @param tokenizer the tokenizer
	 * @param pos the POS tagger
	 * @param chunker the NLP chunker
	 * @param cla the classifier used to compute the confidence that a given answer is a relationship
	 * @param threshold the confidence threshold to consider that a given candidate is
	 * a relationship: if the confidence of a candidate is higher than the threshold
	 * then the candidate is considered a relationship otherwise it is discarded
	 */
	public OpenIEUnsupervisedRelationshipExtractor(SentenceSplitter splitter,
			Tokenizer tokenizer,POSTagger pos,Chunker chunker, Classifier cla,
			double threshold){
		this.splitter=splitter;
		this.tokenizer=tokenizer;
		this.pos=pos;
		this.chunker=chunker;
		this.cla=cla;
		this.threshold=threshold;
	}

	/**
	 * 
	 * Implementation of the extractTuples method. This method starts by splitting the
	 * input document into sentences. Each sentence is tokenized, and its POS tags and
	 * chunks are computed. Next, the candidates to relationships are generated
	 * using KnowItAll unsupervised learning. Finally, the the confidence of the
	 * candidate is computed using the classifier confidence. Only the candidates
	 * with confidence above the threshold are returned as relationships.
	 * 
	 * @param d the document that contains the information to be extracted
	 */
	public List<Relationship> extractTuples(Document doc){
		Sentence[] sents = splitter.split(doc);
		List<ChunkedSentence> chunkedSents = new ArrayList<ChunkedSentence>();
		for(int i=0; i<sents.length; i++){
			String sentenceValue = sents[i].getValue();
			Span[] tokenization = tokenizer.tokenize(sentenceValue);
			String[] tokenizationStrings = new String[tokenization.length];
			for(int j=0; j<tokenization.length; j++){
				Span s = tokenization[j];
				tokenizationStrings[j]=sentenceValue.substring(s.getStart(),s.getEnd());
			}
			String[] posTags = pos.tag(tokenizationStrings);
			String[] chunks = chunker.chunk(tokenizationStrings, posTags);
			ChunkedSentence sent = new ChunkedSentence(tokenizationStrings, posTags, chunks);
			chunkedSents.add(sent);
		}

		ReVerbExtractor reverb;
		try {
			reverb = new ReVerbExtractor();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
		
		AlternativeOpenIEConfFunction confFunc;
		try {
			if(cla==null){
				confFunc = new AlternativeOpenIEConfFunction();
			}else{
				confFunc = new AlternativeOpenIEConfFunction(cla);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}

		Map<Span,Integer> entityIds = new TreeMap<Span,Integer>();
		List<Relationship> results = new ArrayList<Relationship>();
		for(ChunkedSentence sent : chunkedSents){
			for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
				double conf;
				try {
					conf = confFunc.getConf(extr);
				} catch (ConfidenceFunctionException e) {
					e.printStackTrace();
					System.exit(1);
					return null;
				}
				if(conf>threshold){
					RelationshipType t = new RelationshipType(extr.getRelation().toString(), new String[]{"Arg-1", "Arg-2"});
					Relationship rel = new Relationship(t);
					Span ent1 = new Span(extr.getArgument1().getStart(), extr.getArgument1().getStart()+extr.getArgument1().getLength());
					Integer id1 = entityIds.get(ent1);
					if(id1==null){
						id1=entityIds.size();
						entityIds.put(ent1, id1);
					}
					Entity arg1 = new Entity(doc.getFilename() + "-Entity" + id1,"UnknownType",extr.getArgument1().getStart(),
							extr.getArgument1().getLength(),extr.getArgument1().toString(),doc);
	
					Span ent2 = new Span(extr.getArgument2().getStart(), extr.getArgument2().getStart()+extr.getArgument2().getLength());
					Integer id2 = entityIds.get(ent2);
					if(id2==null){
						id2=entityIds.size();
						entityIds.put(ent2, id2);
					}
					Entity arg2 = new Entity(doc.getFilename() + "-Entity" + id2,"UnknownType",extr.getArgument2().getStart(),
							extr.getArgument2().getLength(),extr.getArgument2().toString(),doc);
	
					doc.addEntity(arg1);
					doc.addEntity(arg2);
	
					rel.setRole("Arg-1", arg1);
					rel.setRole("Arg-2", arg2);
					rel.setLabel(t.getType());
	
					results.add(rel);
				}
			}
		}
		return results;
	}
}
