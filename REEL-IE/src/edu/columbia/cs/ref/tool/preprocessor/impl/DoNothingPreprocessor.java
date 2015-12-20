package edu.columbia.cs.ref.tool.preprocessor.impl;

import edu.columbia.cs.ref.tool.preprocessor.Preprocessor;

public class DoNothingPreprocessor implements Preprocessor {

	@Override
	public String process(String content) {
		return content;
	}

}
