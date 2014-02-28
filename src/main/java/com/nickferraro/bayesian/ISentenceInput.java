package com.nickferraro.bayesian;

import java.util.List;
import java.util.Set;

/**
 * The ISentenceInput interface represents a sentence that can be parsed into words.
 * @author Nick Ferraro
 *
 */
public interface ISentenceInput {
	/**
	 * Get the sentence of this data row.
	 * @return The data row sentence.
	 */
	public String getSentence();
	
	/**
	 * Get the words found in this data row's sentence.
	 * @return A list of strings that represent the words in this sentence. Must not be NULL.
	 */
	public List<String> getSentenceWords();
	
	/**
	 * Get the unique words found in this data row's sentence.
	 * @return A set of strings that represent the unique words in this sentence. Must not be NULL.
	 */
	public Set<String> getUniqueSentenceWords();
}
