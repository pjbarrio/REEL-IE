package edu.columbia.cs.cg.prdualrank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import edu.columbia.cs.api.PatternBasedRelationshipExtractor;
import edu.columbia.cs.cg.prdualrank.graph.PRDualRankGraph;
import edu.columbia.cs.cg.prdualrank.graph.generator.ExtractionGraphGenerator;
import edu.columbia.cs.cg.prdualrank.graph.generator.SearchGraphGenerator;
import edu.columbia.cs.cg.prdualrank.index.Index;
import edu.columbia.cs.cg.prdualrank.index.analyzer.TokenBasedAnalyzer;
import edu.columbia.cs.cg.prdualrank.inference.InferencePRDualRank;
import edu.columbia.cs.cg.prdualrank.inference.convergence.impl.NumberOfIterationsConvergence;
import edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator;
import edu.columbia.cs.cg.prdualrank.inference.quest.impl.MapBasedQuestCalculator;
import edu.columbia.cs.cg.prdualrank.inference.ranking.RankFunction;
import edu.columbia.cs.cg.prdualrank.model.PRDualRankModel;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.PatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.impl.ExtractionPatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.impl.WindowedSearchPatternExtractor;
import edu.columbia.cs.cg.prdualrank.searchengine.SearchEngine;
import edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.QueryGenerator;
import edu.columbia.cs.ref.engine.Engine;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.constraint.role.RoleConstraint;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.impl.RelationOperableStructure;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;
import edu.columbia.cs.utils.NAryCartesianProduct;
import edu.columbia.cs.utils.Words;

/**
 * This class is used for our implementation of: 
 * <b> "Searching Patterns for Relation Extraction over the Web: Rediscovering the Pattern-Relation Duality" </b>. Y. Fang and K. C.-C. Chang. In WSDM, pages 825-834, 2011.
 * 
 * For further information, <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a>.
 * 
 * <br><br>
 * 
 * <b>Description</b><br><br>
 * 
 * Main algorithm of PRDualRank to generate search and extraction patterns. The method <b>train</b> generates a PRDualRank model instance.
 * 
 * <br>
 * 
 * This algorithm represents the behavior described in <b>Algorithm PatternSearch(To,S,E)</b> in Figure 9 on Section 5 of the mentioned paper.
 * 
 * <br>
 * @see <a href="http://www.wsdm2011.org/"> WSDM 2011 Conference Website </a> 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-10-07
 */

public class PRDualRank implements Engine{

	private SearchEngine se;
	private QueryGenerator<String> qg;
	private int k_seed;
	private int minsupport;
	private int k_nolabel;
	private int iterations;
	private RankFunction<Pattern<Document,TokenizedDocument>> searchpatternRankFunction;
	private RankFunction<Pattern<Relationship,TokenizedDocument>> extractpatternRankFunction;
	private RankFunction<Relationship> tupleRankFunction;
	private Tokenizer tokenizer;
	private RelationshipType rType;
	private TokenBasedAnalyzer myAnalyzer;
	private QueryGenerator<Query> forIndexQueryGenerator;
	private PatternExtractor<Document> spe;
	private PatternExtractor<Relationship> epe;
	private QuestCalculator<Document, TokenizedDocument> searchPatternQuestCalculator;
	private QuestCalculator<Relationship, TokenizedDocument> extractionPatternQuestCalculator;
	
	/**
	 * Instantiates a new pR dual rank.
	 *
	 * @param spe the Search Pattern Extractor instance.
	 * @param epe the Extraction Pattern Extractor instance.
	 * @param se the Search Engine.
	 * @param qg the Query Generator for the Search Engine se.
	 * @param k_seed the number of documents to be retrieved per query.
	 * @param minsupport the minimum required support for patterns to be considered in the graph generation.
	 * @param k_nolabel the number of non-seed tuples used in graph generation. Recommended 10 times k_seed.
	 * @param searchpatternRankFunction the ranking function for the search patterns.
	 * @param extractpatternRankFunction the ranking function for the extraction patterns.
	 * @param tupleRankFunction the ranking function for tuples.
	 * @param tokenizer the tokenizer used to tokenize the retrieved documents.
	 * @param rType the relationship type to be processed.
	 * @param myAnalyzer the Lucene analyzer for documents to be indexed and queried.
	 * @param forIndexQueryGenerator the query generator for the index. Has to understand the search patterns generated by the Search Pattern Extractor instance.
	 * @param searchPatternQuestCalculator the search pattern quest calculator (as defined in PRDualRank)
	 * @param extractionPatternQuestCalculator the extraction pattern quest calculator (as defined in PRDualRank)
	 */
	public PRDualRank(PatternExtractor<Document> spe, PatternExtractor<Relationship> epe, SearchEngine se, QueryGenerator<String> qg, int k_seed, int minsupport,
			int k_nolabel, RankFunction<Pattern<Document,TokenizedDocument>> searchpatternRankFunction,
			RankFunction<Pattern<Relationship,TokenizedDocument>> extractpatternRankFunction, RankFunction<Relationship> tupleRankFunction, 
			Tokenizer tokenizer, RelationshipType rType, TokenBasedAnalyzer myAnalyzer, QueryGenerator<Query> forIndexQueryGenerator,
			QuestCalculator<Document, TokenizedDocument> searchPatternQuestCalculator, QuestCalculator<Relationship, TokenizedDocument> extractionPatternQuestCalculator){
		this.se = se;
		this.qg = qg;
		this.k_seed = k_seed;
		this.minsupport = minsupport;
		this.k_nolabel = k_nolabel;
		this.searchpatternRankFunction = searchpatternRankFunction;
		this.extractpatternRankFunction = extractpatternRankFunction;
		this.tupleRankFunction = tupleRankFunction;
		this.tokenizer = tokenizer;
		this.rType = rType;
		this.myAnalyzer = myAnalyzer;
		this.forIndexQueryGenerator = forIndexQueryGenerator;
		this.spe = spe;
		this.epe = epe;
		this.searchPatternQuestCalculator = searchPatternQuestCalculator;
		this.extractionPatternQuestCalculator = extractionPatternQuestCalculator;
		//span is for the relationship type. that comes in the List<OperableStructure>
	}
	
	/* (non-Javadoc)
	 * @see edu.columbia.cs.engine.Engine#train(java.util.List)
	 */
	@Override
	public Model train(Collection<OperableStructure> list) {
		
		HashMap<Pattern<Document,TokenizedDocument>, Integer> Ps = new HashMap<Pattern<Document,TokenizedDocument>, Integer>();
		
		HashMap<Pattern<Relationship,TokenizedDocument>, Integer> Pe = new HashMap<Pattern<Relationship,TokenizedDocument>, Integer>();
		
		Set<Relationship> seeds = new HashSet<Relationship>();
		
		Set<Relationship> initial = new HashSet<Relationship>();
		
		for (OperableStructure operableStructure : list) {
			
			seeds.add(((RelationOperableStructure)operableStructure).getRelation());
			
			initial.add(((RelationOperableStructure)operableStructure).getRelation());
			
		}
		
		for (Relationship relationship : seeds) {
			
			List<Document> documents = se.search(qg.generateQuery(relationship), k_seed);

			for (Document document : documents) {

				TokenizedDocument tokenizedDocument = new TokenizedDocument(document, tokenizer);
				
				List<Relationship> mathchingRelationships = getMatchingRelationships(tokenizedDocument,relationship);
				
				updateMap(Ps,spe.extractPatterns(tokenizedDocument,relationship,mathchingRelationships));
				
				updateMap(Pe,epe.extractPatterns(tokenizedDocument,relationship,mathchingRelationships));
				
			}
			
		}
		
		Set<Pattern<Document,TokenizedDocument>> searchPatterns = filter(Ps,minsupport);
		
		Set<Pattern<Relationship,TokenizedDocument>> extractPatterns = filter(Pe,minsupport);
		
		PatternBasedRelationshipExtractor<Relationship,TokenizedDocument> pbre = new PatternBasedRelationshipExtractor<Relationship,TokenizedDocument>(extractPatterns);
		
		HashMap<Relationship,Integer> extractedTuples = new HashMap<Relationship,Integer>();
		
		for (Relationship relationship : seeds) {
			
			for (String role : relationship.getRoles()) {
				
				List<Document> documents = se.search(qg.generateQuery(relationship.getRole(role)), k_seed);
				
				for (Document document : documents) {
					
					TokenizedDocument tokenizedDocument = new TokenizedDocument(document, tokenizer);
					
					updateMap(extractedTuples,filterByRole(rType,role,relationship.getRole(role),pbre.extractTuples(tokenizedDocument)));
					
				}
								
			}
			
		}
		
		Set<Relationship> topTuples = filterTopK(extractedTuples,k_nolabel,initial);
		
		Set<TokenizedDocument> documents = new HashSet<TokenizedDocument>();
		
		Index index = new Index(myAnalyzer,true,Words.getStopWords());
				
		for (Relationship relationship : topTuples) {
			
			List<Document> searchResults = se.search(qg.generateQuery(relationship), k_seed);
			
			for (Document document : searchResults) {
				
				TokenizedDocument tokenizedDocument = new TokenizedDocument(document, tokenizer);
				
				documents.add(tokenizedDocument);
	
				index.addDocument(tokenizedDocument);
				
			}
			
		}
		
		index.close();
		
		PRDualRankGraph<Document,TokenizedDocument> Gs = new SearchGraphGenerator<Document,TokenizedDocument>(rType,index,forIndexQueryGenerator).generateGraph(topTuples,searchPatterns,documents);
		
		System.out.println("Extraction Graph Generation");
		
		PRDualRankGraph<Relationship,TokenizedDocument> Ge = new ExtractionGraphGenerator<Relationship,TokenizedDocument>().generateGraph(topTuples,extractPatterns,documents);
				
		InferencePRDualRank<Document,TokenizedDocument> search = new InferencePRDualRank<Document,TokenizedDocument>();
		
		System.out.println("Ranking...");
		
		searchPatternQuestCalculator.setSeeds(seeds);
		
		search.rank(Gs, searchpatternRankFunction, tupleRankFunction, searchPatternQuestCalculator);
		
		InferencePRDualRank<Relationship,TokenizedDocument> extract = new InferencePRDualRank<Relationship,TokenizedDocument>();

		System.out.println("Ranking...");
		
		extractionPatternQuestCalculator.setSeeds(seeds);
		
		extract.rank(Ge, extractpatternRankFunction, tupleRankFunction, extractionPatternQuestCalculator);
		
		System.out.println("Returning...");
		
		return new PRDualRankModel<Document,Relationship,TokenizedDocument>(search.getRankedPatterns(),extract.getRankedPatterns(),search.getRankedTuples(),extract.getRankedTuples());
		
	}

	private List<Relationship> getMatchingRelationships(TokenizedDocument document,
			Relationship relationship) {
		
		Set<Entity> entities = new HashSet<Entity>(document.getEntities());
		
		Collection<String> roles = relationship.getRoles();
		
		Map<String,Set<Entity>> candidateEntitiesForRole = new HashMap<String,Set<Entity>>();
		
		for(String role : roles){
			
			RoleConstraint roleConstraint = relationship.getRelationshipType().getConstraint(role);
			
			Set<Entity> entitiesForRole = roleConstraint.getCompatibleEntities(entities);
			
			EntityMatcher entityMatcher = relationship.getRelationshipType().getMatchers(role);
			
			Set<Entity> filteredEntitiesForRole = new HashSet<Entity>();
			
			for (Entity entity : entitiesForRole) {
				
				if (entityMatcher.match(relationship.getRole(role), entity)){
					filteredEntitiesForRole.add(entity);
				}
				
			}

			candidateEntitiesForRole.put(role, filteredEntitiesForRole);
		
		}

		List<Relationship> matchingTuples = new ArrayList<Relationship>();
		
		List<Map<String, Entity>> possibilities = NAryCartesianProduct.generateAllPossibilities(candidateEntitiesForRole);
		
		for(Map<String,Entity> candidate : possibilities){
			
			Relationship newRelationship = new Relationship(relationship.getRelationshipType());
			
			for(Entry<String,Entity> entry : candidate.entrySet()){

				newRelationship.setRole(entry.getKey(), entry.getValue());
			
			}

			if (relationship.getRelationshipType().getRelationshipConstraint().checkConstraint(newRelationship)){
				
				matchingTuples.add(newRelationship);
				
			}
			
		}
		
		return matchingTuples;

		
	}

	private class ValueComparator<T> implements Comparator<T>{

		private Map<T, Integer> frequencymap;

		private ValueComparator(Map<T,Integer> frequencymap){
			this.frequencymap = frequencymap;
		}
		
		@Override
		public int compare(T obj1, T obj2) {
			
			return frequencymap.get(obj2).compareTo(frequencymap.get(obj1));
			
		}
		
	}
	
	private <T> Set<T> filterTopK(
			Map<T, Integer> toSelect, int k, Set<T> initial) {
		
		int realLimit = k + initial.size();
		
		SortedMap<T,Integer> sorted = new TreeMap<T, Integer>(new ValueComparator<T>(toSelect));
		
		for (T element : toSelect.keySet()) {
			
			sorted.put(element, toSelect.get(element));
			
		}
		
		for (T element : sorted.keySet()) {
			
			initial.add(element);
			
			if (initial.size() == realLimit)
				break;
		}
		
		return initial;
	}

	private Map<Relationship,Integer> filterByRole(RelationshipType relationshipType, String role,
			Entity value, List<Relationship> extractTuples) {
		
		Map<Relationship, Integer> ret = new HashMap<Relationship, Integer>();
		
		for (Relationship relationship : extractTuples) {
			
			EntityMatcher em = relationshipType.getMatchers(role);
			
			if (em.match(relationship.getRole(role), value)){
				
				Integer freq = ret.get(relationship);
				
				if (freq == null){
					freq = 0;
				}
				
				ret.put(relationship, freq+1);
			}
			
		}
		
		return ret;
	}

	private <T> Set<T> filter(Map<T, Integer> toFilter, int minsupport) {
		
		Set<T> ret = new HashSet<T>();
		
		for (T pattern : toFilter.keySet()) {

			Integer freq = toFilter.get(pattern);
			
			if (freq >= minsupport){
				ret.add(pattern);
			}
			
		}

		return ret;
		
	}

	private <T> void updateMap(Map<T, Integer> acc,
			Map<T, Integer> actual) {
		
		for (T pattern : actual.keySet()) {
			
			Integer freq = acc.get(pattern);
			
			if (freq == null){
				
				freq = 0;
				
			}
			
			acc.put(pattern, freq + actual.get(pattern));
			
		}
		
	}
	
}
