package com.nickferraro.bayesian.model.hashed.core;

import java.security.InvalidParameterException;

/**
 * This class represents a link between a CategoryNode and a WordNode.
 * The link has a weight that describes the strength between the two nodes.
 * @author Nick Ferraro
 *
 * @param <T> The category data type for this link
 */
public class Link<T> {
	private final CategoryNode<T> categoryNode;
	private final WordNode<T> wordNode;
	private int weight = 0;
	
	/**
	 * Constructor for creating a Link. Both parameters are required and cannot be NULL.
	 * @param categoryNode The CategoryNode for this link. Cannot be NULL.
	 * @param wordNode The WordNode for this link. Cannot be NULL.
	 * @throws InvalidParameterException Thrown when either the category node or word node are NULL.
	 */
	public Link(CategoryNode<T> categoryNode, WordNode<T> wordNode) throws InvalidParameterException {
		// Validate the category node
		if( categoryNode == null ) {
			throw new InvalidParameterException("Link cannot have a NULL category node");
		}
		
		// Validate the word node
		if( wordNode == null ) {
			throw new InvalidParameterException("Link cannot have a NULL word node");
		}
		
		this.categoryNode = categoryNode;
		this.wordNode = wordNode;
		setWeight(0);
	}
	
	/**
	 * Get the category node associated with this link.
	 * @return The category node of this link. Will never be NULL.
	 */
	public CategoryNode<T> getCategoryNode() {
		return this.categoryNode;
	}

	/**
	 * Get the word node associated with this link.
	 * @return The word node of this link. Will never be NULL.
	 */
	public WordNode<T> getWordNode() {
		return this.wordNode;
	}

	/**
	 * Get the weight associated with this link.
	 * @return The weight of this link. Will always be a positive number (including 0).
	 */
	public int getWeight() {
		return this.weight;
	}
	
	/**
	 * Set the weight of this link.
	 * @param weight The link weight to set. Must be a positive number (including 0).
	 * @throws InvalidParameterException Thrown when the weight parameter is less than 0.
	 */
	public void setWeight(int weight) throws InvalidParameterException {
		// Validate the weight parameter
		if( weight < 0 ) {
			throw new InvalidParameterException("Link weight cannot be a negative number");
		}
		
		this.weight = weight;
	}
}
