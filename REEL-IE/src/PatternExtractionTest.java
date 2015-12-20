import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;

import edu.columbia.cs.cg.pattern.prdualrank.SearchPattern;
import edu.columbia.cs.cg.prdualrank.index.Index;
import edu.columbia.cs.cg.prdualrank.index.analyzer.TokenBasedAnalyzer;
import edu.columbia.cs.cg.prdualrank.index.analyzer.TokenizerBasedAnalyzer;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.PatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.impl.ExtractionPatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.impl.WindowedSearchPatternExtractor;
import edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.QueryGenerator;
import edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.impl.LuceneQueryGenerator;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.constraint.relationship.impl.WordDistanceBetweenEntities;
import edu.columbia.cs.ref.model.constraint.role.RoleConstraint;
import edu.columbia.cs.ref.model.constraint.role.impl.EntityTypeConstraint;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.impl.RelationOperableStructure;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;
import edu.columbia.cs.ref.model.matcher.impl.DictionaryEntityMatcher;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.loader.document.impl.RawDocumentLoader;
import edu.columbia.cs.ref.tool.preprocessor.Preprocessor;
import edu.columbia.cs.ref.tool.preprocessor.impl.HTMLContentKeeper;
import edu.columbia.cs.ref.tool.segmentator.DocumentSegmentator;
import edu.columbia.cs.ref.tool.segmentator.impl.SimpleSegmentDocumentSegmentator;
import edu.columbia.cs.ref.tool.tagger.Tagger;
import edu.columbia.cs.ref.tool.tagger.entity.impl.DictionaryBasedEntityTagger;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;
import edu.columbia.cs.ref.tool.tokenizer.impl.OpenNLPTokenizer;
import edu.columbia.cs.utils.Dictionary;
import edu.columbia.cs.utils.NAryCartesianProduct;
import edu.columbia.cs.utils.Words;


public class PatternExtractionTest {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		int window = 20;
		int ngram = 4;
		int numberOfPhrases = 2;
		
		int span=10;
		int extractionPatternLenght=10;
		
		Words.initialize(new File("data/stopWords.txt"), null);
		
		String locationsFile = "data/location.txt";
		Dictionary locationsdictionary = new Dictionary(new File(locationsFile), ";","location");
		
		//countries Dictionary
		String countriesFile = "data/country.txt";
		Dictionary countriessdictionary = new Dictionary(new File(countriesFile), ";","country");

		//capitals Dictionary
		String capitalsFile = "data/capital.txt";
		Dictionary capitalsdictionary = new Dictionary(new File(capitalsFile), ";","capital");
		
		//Tokenizer used for the tokenization. Ideally the same.
		Tokenizer tokenizer = new OpenNLPTokenizer("en-token.bin");
		
		String countryRole = "country";
		String capitalRole = "capital";
		//Type of tuples to extract
		RelationshipType rType = new RelationshipType("capitalOf", countryRole,capitalRole);
		
		String locationType = "location";
		
		EntityTypeConstraint countryConstraint = new EntityTypeConstraint(locationType);
		rType.setConstraints(countryConstraint, countryRole);
		
		EntityTypeConstraint capitalConstraint = new EntityTypeConstraint(locationType);
		rType.setConstraints(capitalConstraint, capitalRole);
		
		RelationshipConstraint constraint = new WordDistanceBetweenEntities(tokenizer, span);
		
		rType.setConstraints(constraint);
		
		EntityMatcher countryMatcher = new DictionaryEntityMatcher(locationsdictionary);
		rType.setMatchers(countryMatcher, countryRole);
		
		EntityMatcher capitalMatcher = new DictionaryEntityMatcher(locationsdictionary);
		rType.setMatchers(capitalMatcher, capitalRole);

		//How to segment the html documents
		DocumentSegmentator docSegmentator = new SimpleSegmentDocumentSegmentator();
		
		//Dictionary for Country
		Tagger countryDictionaryTagger = new DictionaryBasedEntityTagger(locationType, countriessdictionary, tokenizer);
		
		//Dictionary for Capitals
		Tagger capitalDictionaryTagger = new DictionaryBasedEntityTagger(locationType, capitalsdictionary, tokenizer );
		
		//Preprocessor for html documents;
		Preprocessor preprocessor = new HTMLContentKeeper();
		
		Set<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();
		relationshipTypes.add(rType);
		
		//Loader from string to Document.
		RawDocumentLoader loader = new RawDocumentLoader(relationshipTypes, preprocessor , docSegmentator, countryDictionaryTagger, capitalDictionaryTagger);
		
		String file = "/home/pjbarrio/Desktop/test/Ottawa.html";
		
		Document doc = loader.load(new FileReader(file));

		doc.setPath("/home/pjbarrio/Desktop/test/");
		
		doc.setFilename("Ottawa.html");
		
//NOT USEFUL		TokenizerBasedAnalyzer myTokenizerAnalyzer = new TokenizerBasedAnalyzer(tokenizer, Words.getStopWords());
		
		TokenizedDocument tokenized = new TokenizedDocument(doc, tokenizer);

		TokenBasedAnalyzer myAnalyzer = new TokenBasedAnalyzer(Words.getStopWords());
		
		Index index = new Index(myAnalyzer,true,Words.getStopWords());
		
		index.addDocument(tokenized);
		
		index.close();

		
//		
//		PatternExtractor<Document> spe = new WindowedSearchPatternExtractor<Document>(window, ngram, numberOfPhrases);
//		
		Relationship relationship = generateOperableStructure(rType,"1",locationType,countryRole,new String("Ottawa"),capitalRole,new String("Ottawa"));
	
		Relationship relationship2 = generateOperableStructure(rType,"2",locationType,countryRole,new String("Ottawa"),capitalRole,new String("Ottawa"));
		
		System.out.println(relationship.hashCode());
		System.out.println(relationship2.hashCode());
		
		System.out.println(relationship.equals(relationship2));
		
//		List<Relationship> matchingRelationships = getMatchingRelationships(tokenized, relationship);
		
//		Map<Pattern<Document, TokenizedDocument>, Integer> patterns = spe.extractPatterns(tokenized, relationship, matchingRelationships);
		
		
//		QueryGenerator<Query> forIndexQueryGenerator = new LuceneQueryGenerator(myAnalyzer);
//		
//		for (Pattern<Document, TokenizedDocument> pattern : patterns.keySet()) {
//			
//			if (index.search(forIndexQueryGenerator.generateQuery((SearchPattern<Document, TokenizedDocument>)pattern), 1).size() == 0){
//				System.out.println(false);
//			}
//			
//		}

//		PatternExtractor<Relationship> epe = new ExtractionPatternExtractor<Relationship>(span,extractionPatternLenght,rType);
//
//		Map<Pattern<Relationship, TokenizedDocument>, Integer> patterns = epe.extractPatterns(tokenized, relationship, matchingRelationships);
//		
//		Set<Entry<Pattern<Relationship, TokenizedDocument>, Integer>> entries = patterns.entrySet();
//		
//		List<Pattern<Relationship, TokenizedDocument>> surv = new ArrayList<Pattern<Relationship,TokenizedDocument>>();
//		
//		for (Entry<Pattern<Relationship, TokenizedDocument>, Integer> entry : entries) {
//			
//			if (entry.getValue() > 3){
////				System.out.println(entry.getKey() + " - " + entry.getValue());
//				surv.add(entry.getKey());
//			}
//			
//		}
//		
//		for (Pattern<Relationship, TokenizedDocument> pattern : surv) {
//			
//			System.out.println(pattern.toString());
//			
//			List<Relationship> rel = pattern.findMatch(tokenized);
//			
//			for (Relationship relationship2 : rel) {
//				
//				System.out.println(Arrays.toString(getRelavant(relationship2,tokenized)));
//				
//			}
//			
//		}

	}
	
	private static String[] getRelavant(Relationship relationship2,
			TokenizedDocument tokenized) {
		
		Entity[] relEntities = relationship2.getEntities();
		
		return Arrays.copyOfRange(tokenized.getTokenizedString(),Math.min(tokenized.getEntitySpan(relEntities[0]).getStart(), tokenized.getEntitySpan(relEntities[1]).getStart()) - 5,Math.max(tokenized.getEntitySpan(relEntities[0]).getEnd(), tokenized.getEntitySpan(relEntities[1]).getEnd()) + 5);
		
	}

	private static Relationship generateOperableStructure(RelationshipType rType, String id, String entityType,String countryRole, String country, String capitalRole, String capital) {
		
		Relationship r1 = new Relationship(rType);
		Entity countryE = new Entity(id, entityType, (int)Math.round(Math.random()*500), country.length(), country, null);
		r1.setRole(countryRole, countryE);
		Entity capitalE = new Entity(id, entityType, (int)Math.round(Math.random()*500), capital.length(), capital, null);
		r1.setRole(capitalRole, capitalE);
		return r1;
		
	}

	private static List<Relationship> getMatchingRelationships(TokenizedDocument document,
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

	
}
