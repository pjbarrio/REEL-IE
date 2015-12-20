package edu.columbia.cs.ref.model.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.InvalidFormatException;

import edu.columbia.cs.ref.algorithm.feature.generation.FeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.SentenceFeatureGenerator;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.EntitySplitsFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.KnowItAllChunkingFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenInformationExtractionFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPPartOfSpeechFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.OpenNLPTokenizationFG;
import edu.columbia.cs.ref.algorithm.feature.generation.impl.SpansToStringsConvertionFG;
import edu.columbia.cs.ref.model.CandidateSentence;
import edu.columbia.cs.ref.model.Span;
import edu.columbia.cs.ref.model.core.Core;
import edu.columbia.cs.ref.model.core.structure.OperableStructure;
import edu.columbia.cs.ref.model.core.structure.impl.OpenInformationExtractionOS;
import edu.columbia.cs.ref.model.feature.impl.SequenceFS;
import edu.columbia.cs.ref.model.feature.impl.WekaInstanceFS;
import edu.columbia.cs.utils.AlternativeOpenIEFeatures;
import edu.washington.cs.knowitall.extractor.conf.ReVerbFeatures;

/**
 * This class is used for the implementation of the ReVerb confidence function that is described in: 
 * <b> "Identifying Relations for Open Information Extraction" </b>. A. Fader and S. Soderland and O. Etzioni. In Conference on Empirical Methods in Natural Language Processing 2011, 2011.
 * For further information, <a href="http://reverb.cs.washington.edu/"> ReVerb Website </a>.
 * 
 * <br>
 * <br>
 * 
 * This core defines the type of structure that is used by the Open Information Extraction confidence function:
 * OpenInformationExtractionOS. Additionally, it defines the mandatory features needed: (i) tokenization;
 * (ii) Conversion from spans to strings; (iii) POS tagging; (iv) the chunker; (v) the entity-based spliting
 * of the sentence and the creation of the respective Weka Instance object.
 *
 * @see <a href="http://reverb.cs.washington.edu/">ReVerb Website</a>, <a href="http://www.cs.waikato.ac.nz/ml/weka/">Weka</a>
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class OpenInformationExtractionCore extends Core {

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.core.Core#createMandatoryFeatureGenerators()
	 */
	@Override
	protected List<FeatureGenerator> createMandatoryFeatureGenerators() {
		// TODO OpenNLPtoken Chunks entitySplits
		
		List<FeatureGenerator> features = new ArrayList<FeatureGenerator>();
		
		try {
			FeatureGenerator<SequenceFS<Span>> tokenSpans = new OpenNLPTokenizationFG("en-token.bin");
			
			FeatureGenerator<SequenceFS<String>> tokens = new SpansToStringsConvertionFG(tokenSpans);
			
			FeatureGenerator<SequenceFS<String>> pos = new OpenNLPPartOfSpeechFG("en-pos-maxent.bin",tokens);
			
			FeatureGenerator<SequenceFS<String>> chunk = new KnowItAllChunkingFG(tokens,pos);
			
			FeatureGenerator<SequenceFS<Span>> entitySplits = new EntitySplitsFG(tokenSpans);
			
			FeatureGenerator<WekaInstanceFS> wekaFS = new OpenInformationExtractionFG(
					new AlternativeOpenIEFeatures(),tokens,pos,chunk,entitySplits);
			
			features.add(tokens);
			
			features.add(pos);
			
			features.add(chunk);
			
			features.add(entitySplits);
			
			features.add(wekaFS);
			
			return features;
			
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(1);
		
		return null;
		

	}

	/* (non-Javadoc)
	 * @see edu.columbia.cs.ref.model.core.Core#createOperableStructure(edu.columbia.cs.ref.model.CandidateSentence)
	 */
	@Override
	protected OperableStructure createOperableStructure(CandidateSentence sent) {
		
		return new OpenInformationExtractionOS(sent);
	
	}

}
