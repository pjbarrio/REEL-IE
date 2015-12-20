package edu.columbia.cs.ref.algorithm.feature.generation.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.util.InvalidFormatException;
import edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.feature.impl.GraphFS;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.utils.SimpleGraph;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * The Class StanfordNLPDependencyGraphFG is a candidate sentence feature generator that 
 * returns the dependency graph representation of a previously tokenized sentence.
 * 
 * <br>
 * <br>
 * 
 * This class receives as input another feature generator that must produce the original
 * tokenization.
 *
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class StanfordNLPDependencyGraphFG extends CandidateSentenceFeatureGenerator<GraphFS<Integer,String>> {

	/** The parser. */
	private LexicalizedParser parser;
	
	/** The tokenizer. */
	private FeatureGenerator<SequenceFS<Span>> tokenizer;
	
	/**
	 * Instantiates a new StanfordNLPDependencyGraphFG
	 *
	 * @param path the path to the POS model
	 * @param tokenizer the feature generator that produces the original tokenization
	 * @throws InvalidFormatException the invalid format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public StanfordNLPDependencyGraphFG(String path, FeatureGenerator<SequenceFS<Span>> tokenizer) throws InvalidFormatException, IOException{
		parser = LexicalizedParser.getParserFromSerializedFile(path);
				
		this.tokenizer = tokenizer;
	}
	
	private List<Word> getTokens(SequenceFS<Span> spans, CandidateSentence sentence){
		String value = sentence.getSentence().getValue();
		List<Word> tokens = new ArrayList<Word>();
		
		for(int i=0; i<spans.size(); i++){
			Span s = spans.getElement(i);
			tokens.add(new Word(value.substring(s.getStart(),s.getEnd())));
		}
		
		return tokens;
	}
	
	/** The dep parses. */
	private Map<List<Word>,Collection<TypedDependency>> depParses = new HashMap<List<Word>,Collection<TypedDependency>>();

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#extractFeatures(edu.columbia.cs.ref.model.CandidateSentence)
	 */
	@Override
	protected GraphFS<Integer,String> extractFeatures(CandidateSentence sentence) {
		
		SequenceFS<Span> tokenization = sentence.getFeatures(tokenizer);

		List<Word> tokens = getTokens(tokenization, sentence);
		
		Collection<TypedDependency> dependencies = depParses.get(tokens);
		if(dependencies==null){
			System.out.println("PROCESS");

//			parser.parse(tokens);
//			Tree parsingTree = parser.getBestPCFGParse();
			
			Tree parsingTree = parser.parseTree(tokens);
			
			EnglishGrammaticalStructure struc = new EnglishGrammaticalStructure(parsingTree);
			dependencies = struc.allTypedDependencies();
			
			depParses.put(tokens, dependencies);

		}else{
			System.out.println("REUSED");
		}

		SimpleGraph<Integer,String> g = new SimpleGraph<Integer,String>(tokenization.size());

		int tokenizationSize=tokenization.size();

		for(int i=0; i<tokenizationSize; i++){
			g.addNode(i, i);
		}
		
		for(TypedDependency dep : dependencies){

			if (dep.gov().index() != 0) //avoids the root
				g.addEdge(dep.gov().index()-1, dep.dep().index()-1, dep.reln().getShortName());
			
		}
		
		return new GraphFS<Integer,String>(g);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.algorithm.feature.generation.CandidateSentenceFeatureGenerator#retrieveRequiredFeatureGenerators()
	 */
	@Override
	protected List<FeatureGenerator> retrieveRequiredFeatureGenerators() {
		ArrayList<FeatureGenerator> ret = new ArrayList<FeatureGenerator>();
		ret.add(tokenizer);
		return ret;
	}
}
