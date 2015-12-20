import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;


import edu.columbia.cs.cg.prdualrank.PRDualRank;
import edu.columbia.cs.cg.prdualrank.index.analyzer.TokenBasedAnalyzer;
import edu.columbia.cs.cg.prdualrank.index.analyzer.TokenizerBasedAnalyzer;
import edu.columbia.cs.cg.prdualrank.inference.convergence.impl.NumberOfIterationsConvergence;
import edu.columbia.cs.cg.prdualrank.inference.quest.QuestCalculator;
import edu.columbia.cs.cg.prdualrank.inference.quest.impl.MapBasedQuestCalculator;
import edu.columbia.cs.cg.prdualrank.inference.ranking.RankFunction;
import edu.columbia.cs.cg.prdualrank.inference.ranking.impl.FScoreBasedRankFunction;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.PatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.impl.ExtractionPatternExtractor;
import edu.columbia.cs.cg.prdualrank.pattern.extractor.impl.WindowedSearchPatternExtractor;
import edu.columbia.cs.cg.prdualrank.searchengine.SearchEngine;
import edu.columbia.cs.cg.prdualrank.searchengine.impl.BingSearchEngine;
import edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.QueryGenerator;
import edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.impl.ConcatQueryGenerator;
import edu.columbia.cs.cg.prdualrank.searchengine.querygenerator.impl.LuceneQueryGenerator;
import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.TokenizedDocument;
import edu.columbia.cs.ref.model.constraint.relationship.RelationshipConstraint;
import edu.columbia.cs.ref.model.constraint.relationship.impl.WordDistanceBetweenEntities;
import edu.columbia.cs.ref.model.constraint.role.impl.EntityTypeConstraint;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.impl.RelationOperableStructure;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.model.matcher.EntityMatcher;
import edu.columbia.cs.ref.model.matcher.impl.DictionaryEntityMatcher;
import edu.columbia.cs.ref.model.pattern.Pattern;
import edu.columbia.cs.ref.model.re.Model;
import edu.columbia.cs.ref.model.relationship.Relationship;
import edu.columbia.cs.ref.model.relationship.RelationshipType;
import edu.columbia.cs.ref.tool.loader.document.impl.RawDocumentLoader;
import edu.columbia.cs.ref.tool.preprocessor.Preprocessor;
import edu.columbia.cs.ref.tool.preprocessor.impl.HTMLContentKeeper;
import edu.columbia.cs.ref.tool.segmentator.DocumentSegmentator;
import edu.columbia.cs.ref.tool.segmentator.impl.SimpleSegmentDocumentSegmentator;
import edu.columbia.cs.ref.tool.tagger.Tagger;
import edu.columbia.cs.ref.tool.tagger.entity.impl.DictionaryBasedEntityTagger;
import edu.columbia.cs.ref.tool.tagger.span.impl.EntitySpan;
import edu.columbia.cs.ref.tool.tokenizer.Tokenizer;
import edu.columbia.cs.ref.tool.tokenizer.impl.OpenNLPTokenizer;
import edu.columbia.cs.utils.Dictionary;
import edu.columbia.cs.utils.Words;


public class PRDualRankTest {

	/**
	 * This code was created for us to do these three experiments on the
	 * servers without much configuration the arg was just an index to the
	 * configuration. So... just disregard this and jump to one of the methods.
	 * (They are all the same but I commented runAreaCode for your convenience).
	 */
	public static void main(String[] args) {
		
		int arg = Integer.parseInt(args[0]);
		
		if(arg==0){
			new PRDualRankTest().runCapitalCountry();
		}else if(arg==1){
			new PRDualRankTest().runPhysicsNobel();
		}else{
			new PRDualRankTest().runAreaCode();
		}
	}

	private void runAreaCode() {
		//These parameters are just our personal choice... Fell free to
		//play around with them. I am pretty much sure that most of these
		//parameters are from the paper but if they are not I think your
		//best solution is to check the methods that use them because those
		//methods are very well documented
		int extractionPatternLenght = 5;
		int numberOfPhrases = 2;
		int iterations = 3;
		int window = 5;
		int minsupport = 5;
		int k_nolabel = 50;
		int k_seed = 20;
		int ngram = 3;
		int span = 10;

		//These are just the types used by the named entity taggers
		String locationType = "location";
		String numberType = "number";

		String locationFile = "data/usCity.txt";
		Dictionary locationdictionary = new Dictionary(new File(locationFile), ";",locationType);
		String numberFile = "data/areaCode.txt"; 
		Dictionary numberdictionary = new Dictionary(new File(numberFile), ";",numberType);
		//Just the roles that each one perform in the relationship
		String usCityRole = "city";
		String areaCodeRole = "areaCode";
		
		//countries Dictionary
		String usCityFile = "data/usCity.txt";
		Dictionary usCitydictionary = new Dictionary(new File(usCityFile), ";",usCityRole);

		//capitals Dictionary
		String areaCodeFile = "data/areaCode.txt";
		Dictionary areaCodedictionary = new Dictionary(new File(areaCodeFile), ";",areaCodeRole);
		
		//Tokenizer used for the tokenization. Ideally the same.
		Tokenizer tokenizer = new OpenNLPTokenizer("en-token.bin");
		
		
		//Type of tuples to extract and the constraints involved
		RelationshipType rType = new RelationshipType("AreaCodeOfCity", usCityRole,areaCodeRole);
		EntityTypeConstraint physicsConstraint = new EntityTypeConstraint(locationType);
		rType.setConstraints(physicsConstraint, usCityRole);
		EntityTypeConstraint yearOfBirthConstraint = new EntityTypeConstraint(numberType);
		rType.setConstraints(yearOfBirthConstraint, areaCodeRole);
		//We only consider relationships at a distance less that 'span' tokens according to the tokenization
		//provided
		RelationshipConstraint constraint = new WordDistanceBetweenEntities(tokenizer, span);
		rType.setConstraints(constraint);
		
		//These are used for NER. We are only using dictionary based NER but you can use
		//other stuff... A pattern based approach would be very cool but we don't have an
		//implementation. If you need I can also make my master thesis available but
		//in your case I don't think it would help because you don't have the training
		//data
		EntityMatcher personMatcher = new DictionaryEntityMatcher(locationdictionary);
		rType.setMatchers(personMatcher, usCityRole);	
		EntityMatcher yearMatcher = new DictionaryEntityMatcher(areaCodedictionary);
		rType.setMatchers(yearMatcher, areaCodeRole);
		
		
		Set<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();
		relationshipTypes.add(rType);
		
		//How to segment the html documents
		DocumentSegmentator docSegmentator = new SimpleSegmentDocumentSegmentator();
		
		//Dictionary for Country
		Tagger<EntitySpan,Entity> personDictionaryTagger = new DictionaryBasedEntityTagger(locationType, usCitydictionary, tokenizer);
		
		//Dictionary for Capitals
		Tagger<EntitySpan,Entity> yearDictionaryTagger = new DictionaryBasedEntityTagger(numberType, numberdictionary, tokenizer );
		
		//Preprocessor for html documents;
		Preprocessor preprocessor = new HTMLContentKeeper();
		
		//Loader from string to Document.
		RawDocumentLoader loader = new RawDocumentLoader(relationshipTypes, preprocessor , docSegmentator, personDictionaryTagger, yearDictionaryTagger);
		
		//Generation of queries based on Concatenation
		QueryGenerator<String> qg = new ConcatQueryGenerator();

		//Bing Search Engine... You will need to implement your own engine here
		//A standard Lucene engine should suffice
		SearchEngine se = new BingSearchEngine(loader);
		
		//Ranking functions
		double betaextr = 1.0;
		RankFunction<Pattern<Relationship, TokenizedDocument>> extractpatternRankFunction = new FScoreBasedRankFunction<Pattern<Relationship,TokenizedDocument>>(betaextr);
		double betatup = 1.0;
		RankFunction<Relationship> tupleRankFunction = new FScoreBasedRankFunction<Relationship>(betatup);
		double betasearch = 1.0;
		RankFunction<Pattern<Document, TokenizedDocument>> searchpatternRankFunction = new FScoreBasedRankFunction<Pattern<Document,TokenizedDocument>>(betasearch);
				
		Words.initialize(new File("data/stopWords.txt"), null);
		
		//Index And Search.
		//List of stop words
		Set<String> stopW = Words.getStopWords();
		
//		TokenizerBasedAnalyzer myTokenizerAnalyzer = new TokenizerBasedAnalyzer(tokenizer,stopW);

		TokenBasedAnalyzer myAnalyzer = new TokenBasedAnalyzer(stopW);

		QueryGenerator<Query> forIndexQueryGenerator = new LuceneQueryGenerator(myAnalyzer);
		
		PatternExtractor<Document> spe = new WindowedSearchPatternExtractor<Document>(window, ngram, numberOfPhrases);
		
		QuestCalculator<Document, TokenizedDocument> spqc = new MapBasedQuestCalculator<Document,TokenizedDocument>(new NumberOfIterationsConvergence(iterations));
		
		PatternExtractor<Relationship> epe = new ExtractionPatternExtractor<Relationship>(span,extractionPatternLenght,rType);
		
		QuestCalculator<Relationship,TokenizedDocument> epqc = new MapBasedQuestCalculator<Relationship,TokenizedDocument>(new NumberOfIterationsConvergence(iterations));
		
		PRDualRank prDualRank = new PRDualRank(spe, epe, se, qg, k_seed, minsupport, k_nolabel, searchpatternRankFunction, 
				extractpatternRankFunction, tupleRankFunction, tokenizer, rType, myAnalyzer,forIndexQueryGenerator,spqc,epqc);
	
		List<OperableStructure> seeds = new ArrayList<OperableStructure>();
	
		//generate seeds as operable structures... you can load these from a file... it would
		//be better than having them hard coded as we do
		seeds.add(generateOperableStructure(rType,"1",locationType,usCityRole,"Chicago",numberType,areaCodeRole,"312"));
		seeds.add(generateOperableStructure(rType,"2",locationType,usCityRole,"Chicago",numberType,areaCodeRole,"872"));
		seeds.add(generateOperableStructure(rType,"3",locationType,usCityRole,"Atlanta",numberType,areaCodeRole,"470"));
		seeds.add(generateOperableStructure(rType,"4",locationType,usCityRole,"Oklahoma City",numberType,areaCodeRole,"405"));
		seeds.add(generateOperableStructure(rType,"5",locationType,usCityRole,"Nashville",numberType,areaCodeRole,"615"));
		seeds.add(generateOperableStructure(rType,"6",locationType,usCityRole,"Seattle",numberType,areaCodeRole,"206"));
		seeds.add(generateOperableStructure(rType,"7",locationType,usCityRole,"Memphis",numberType,areaCodeRole,"901"));
		
		
		//Here is the important part. After this point, using PRDualRank or
		//any other is basically the same.
		Model out = prDualRank.train(seeds);
		
		try {
			System.setOut(new PrintStream(new File("CityAC.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println(out.toString());

		
	}

	private void runPhysicsNobel() {
		int extractionPatternLenght = 5;
		int numberOfPhrases = 2;
		int iterations = 3;
		int window = 5;
		int minsupport = 5;
		int k_nolabel = 50;
		int k_seed = 20;
		int ngram = 3;
		int span = 10;

		String personType = "person";
		String yearType = "year";

		
		String personFile = "data/Physics.txt"; //should be a Person File
		Dictionary persondictionary = new Dictionary(new File(personFile), ";",personType);
		
		String yobFile = "data/yearOfBirth.txt"; 
		Dictionary yeardictionary = new Dictionary(new File(yobFile), ";",yearType);
		
		String physicRole = "physic";
		String yearOfBirthRole = "yearOfBirth";
		
		//countries Dictionary
		String physicsFile = "data/Physics.txt";
		Dictionary physicsdictionary = new Dictionary(new File(physicsFile), ";",physicRole);

		//capitals Dictionary
		String yearOfBirthFile = "data/yearOfBirth.txt";
		Dictionary yearOfBirthdictionary = new Dictionary(new File(yearOfBirthFile), ";",yearOfBirthRole);
		
		//Tokenizer used for the tokenization. Ideally the same.
		Tokenizer tokenizer = new OpenNLPTokenizer("en-token.bin");
		
		
		//Type of tuples to extract
		RelationshipType rType = new RelationshipType("YearOfBirthPhysic", physicRole,yearOfBirthRole);
		
		EntityTypeConstraint physicsConstraint = new EntityTypeConstraint(personType);
		rType.setConstraints(physicsConstraint, physicRole);
		
		EntityTypeConstraint yearOfBirthConstraint = new EntityTypeConstraint(yearType);
		rType.setConstraints(yearOfBirthConstraint, yearOfBirthRole);
		
		RelationshipConstraint constraint = new WordDistanceBetweenEntities(tokenizer, span);
		
		rType.setConstraints(constraint);
		
		EntityMatcher personMatcher = new DictionaryEntityMatcher(persondictionary);
		rType.setMatchers(personMatcher, physicRole);
		
		EntityMatcher yearMatcher = new DictionaryEntityMatcher(yearOfBirthdictionary);
		rType.setMatchers(yearMatcher, yearOfBirthRole);
		
		
		Set<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();
		relationshipTypes.add(rType);
		
		//How to segment the html documents
		DocumentSegmentator docSegmentator = new SimpleSegmentDocumentSegmentator();
		
		//Dictionary for Country
		Tagger<EntitySpan,Entity> personDictionaryTagger = new DictionaryBasedEntityTagger(personType, physicsdictionary, tokenizer);
		
		//Dictionary for Capitals
		Tagger<EntitySpan,Entity> yearDictionaryTagger = new DictionaryBasedEntityTagger(yearType, yeardictionary, tokenizer );
		
		//Preprocessor for html documents;
		Preprocessor preprocessor = new HTMLContentKeeper();
		
		//Loader from string to Document.
		RawDocumentLoader loader = new RawDocumentLoader(relationshipTypes, preprocessor , docSegmentator, personDictionaryTagger, yearDictionaryTagger);
		
		//Generation of queries based on Concatenation
		QueryGenerator<String> qg = new ConcatQueryGenerator();

		//Bing Search Engine
		SearchEngine se = new BingSearchEngine(loader);
		
		//Ranking functions
		double betaextr = 1.0;
		RankFunction<Pattern<Relationship, TokenizedDocument>> extractpatternRankFunction = new FScoreBasedRankFunction<Pattern<Relationship,TokenizedDocument>>(betaextr);
		double betatup = 1.0;
		RankFunction<Relationship> tupleRankFunction = new FScoreBasedRankFunction<Relationship>(betatup);
		double betasearch = 1.0;
		RankFunction<Pattern<Document, TokenizedDocument>> searchpatternRankFunction = new FScoreBasedRankFunction<Pattern<Document,TokenizedDocument>>(betasearch);
				
		Words.initialize(new File("data/stopWords.txt"), null);
		
		//Index And Search.
		
		Set<String> stopW = Words.getStopWords();
		
//		TokenizerBasedAnalyzer myTokenizerAnalyzer = new TokenizerBasedAnalyzer(tokenizer,stopW);

		TokenBasedAnalyzer myAnalyzer = new TokenBasedAnalyzer(stopW);

		QueryGenerator<Query> forIndexQueryGenerator = new LuceneQueryGenerator(myAnalyzer);
		
		PatternExtractor<Document> spe = new WindowedSearchPatternExtractor<Document>(window, ngram, numberOfPhrases);
		
		QuestCalculator<Document, TokenizedDocument> spqc = new MapBasedQuestCalculator<Document,TokenizedDocument>(new NumberOfIterationsConvergence(iterations));
		
		PatternExtractor<Relationship> epe = new ExtractionPatternExtractor<Relationship>(span,extractionPatternLenght,rType);
		
		QuestCalculator<Relationship,TokenizedDocument> epqc = new MapBasedQuestCalculator<Relationship,TokenizedDocument>(new NumberOfIterationsConvergence(iterations));
		
		PRDualRank prDualRank = new PRDualRank(spe, epe, se, qg, k_seed, minsupport, k_nolabel, searchpatternRankFunction, 
				extractpatternRankFunction, tupleRankFunction, tokenizer, rType, myAnalyzer,forIndexQueryGenerator,spqc,epqc);
	
		List<OperableStructure> seeds = new ArrayList<OperableStructure>();
	
		seeds.add(generateOperableStructure(rType,"1",personType,physicRole,"Alexei Alexeyevich Abrikosov",yearType,yearOfBirthRole,"1928"));
		seeds.add(generateOperableStructure(rType,"2",personType,physicRole,"Subrahmanyan Chandrasekhar",yearType,yearOfBirthRole,"1910"));
		seeds.add(generateOperableStructure(rType,"3",personType,physicRole,"Pierre Curie",yearType,yearOfBirthRole,"1859"));
		seeds.add(generateOperableStructure(rType,"4",personType,physicRole,"Albert Fert",yearType,yearOfBirthRole,"1938"));
		seeds.add(generateOperableStructure(rType,"5",personType,physicRole,"Max Theodor Felix Von Laue",yearType,yearOfBirthRole,"1879"));
		
		Model out = prDualRank.train(seeds);
		
		try {
			System.setOut(new PrintStream(new File("physicsYOB.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println(out.toString());
	}

	private void runCapitalCountry() {
		int extractionPatternLenght = 5;
		int numberOfPhrases = 2;
		int iterations = 3;
		int window = 5;
		int minsupport = 5;
		int k_nolabel = 50;
		int k_seed = 20;
		int ngram = 3;
		int span = 10;

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
		
		
		Set<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();
		relationshipTypes.add(rType);
		
		//How to segment the html documents
		DocumentSegmentator docSegmentator = new SimpleSegmentDocumentSegmentator();
		
		//Dictionary for Country
		Tagger<EntitySpan,Entity> countryDictionaryTagger = new DictionaryBasedEntityTagger(locationType, countriessdictionary, tokenizer);
		
		//Dictionary for Capitals
		Tagger<EntitySpan,Entity> capitalDictionaryTagger = new DictionaryBasedEntityTagger(locationType, capitalsdictionary, tokenizer );
		
		//Preprocessor for html documents;
		Preprocessor preprocessor = new HTMLContentKeeper();
		
		//Loader from string to Document.
		RawDocumentLoader loader = new RawDocumentLoader(relationshipTypes, preprocessor , docSegmentator, countryDictionaryTagger, capitalDictionaryTagger);
		
		//Generation of queries based on Concatenation
		QueryGenerator<String> qg = new ConcatQueryGenerator();

		//Bing Search Engine
		SearchEngine se = new BingSearchEngine(loader);
		
		//Ranking functions
		double betaextr = 1.0;
		RankFunction<Pattern<Relationship, TokenizedDocument>> extractpatternRankFunction = new FScoreBasedRankFunction<Pattern<Relationship,TokenizedDocument>>(betaextr);
		double betatup = 1.0;
		RankFunction<Relationship> tupleRankFunction = new FScoreBasedRankFunction<Relationship>(betatup);
		double betasearch = 1.0;
		RankFunction<Pattern<Document, TokenizedDocument>> searchpatternRankFunction = new FScoreBasedRankFunction<Pattern<Document,TokenizedDocument>>(betasearch);
				
		Words.initialize(new File("data/stopWords.txt"), null);
		
		//Index And Search.
		
		Set<String> stopW = Words.getStopWords();
		
//		TokenizerBasedAnalyzer myTokenizerAnalyzer = new TokenizerBasedAnalyzer(tokenizer,stopW);

		TokenBasedAnalyzer myAnalyzer = new TokenBasedAnalyzer(stopW);

		QueryGenerator<Query> forIndexQueryGenerator = new LuceneQueryGenerator(myAnalyzer);
		
		PatternExtractor<Document> spe = new WindowedSearchPatternExtractor<Document>(window, ngram, numberOfPhrases);
		
		QuestCalculator<Document, TokenizedDocument> spqc = new MapBasedQuestCalculator<Document,TokenizedDocument>(new NumberOfIterationsConvergence(iterations));
		
		PatternExtractor<Relationship> epe = new ExtractionPatternExtractor<Relationship>(span,extractionPatternLenght,rType);
		
		QuestCalculator<Relationship,TokenizedDocument> epqc = new MapBasedQuestCalculator<Relationship,TokenizedDocument>(new NumberOfIterationsConvergence(iterations));
		
		PRDualRank prDualRank = new PRDualRank(spe, epe, se, qg, k_seed, minsupport, k_nolabel, searchpatternRankFunction, 
				extractpatternRankFunction, tupleRankFunction, tokenizer, rType, myAnalyzer,forIndexQueryGenerator,spqc,epqc);
	
		List<OperableStructure> seeds = new ArrayList<OperableStructure>();
	
		seeds.add(generateOperableStructure(rType,"1",locationType,countryRole,"Canada",locationType,capitalRole,"Ottawa"));
		seeds.add(generateOperableStructure(rType,"2",locationType,countryRole,"China",locationType,capitalRole,"Beijing"));
		seeds.add(generateOperableStructure(rType,"3",locationType,countryRole,"Bulgaria",locationType,capitalRole,"Sofia"));
		seeds.add(generateOperableStructure(rType,"4",locationType,countryRole,"France",locationType,capitalRole,"Paris"));
		seeds.add(generateOperableStructure(rType,"5",locationType,countryRole,"Portugal",locationType,capitalRole,"Lisbon"));
		
		Model out = prDualRank.train(seeds);
		
		try {
			System.setOut(new PrintStream(new File("capitalCountry.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println(out.toString());
		
	}

	private static OperableStructure generateOperableStructure(RelationshipType rType, String id, String entityType,String entityRole, String entityValue, String entity2Type,String entity2Role, String entity2Value) {
		
		Relationship r1 = new Relationship(rType);
		Entity entity = new Entity(id, entityType, 0, entityRole.length(), entityValue, null);
		r1.setRole(entityRole, entity);
		Entity entity2 = new Entity(id, entity2Type, 0, entity2Role.length(), entity2Value, null);
		r1.setRole(entity2Role, entity2);
		return new RelationOperableStructure(r1);
		
	}

}
