package com.nickferraro.bayesian.model.hashed.core;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * A node that represents a category for the hashed bayesian model.
 * This class keeps a count of the number of times a category exists in the model, as well as links to associated words.
 * @author Nick Ferraro
 *
 * @param <T> The category data type
 */
public class CategoryNode<T> {
	private final HashMap<String, Link<T>> linksMap = new HashMap<String, Link<T>>();
	private int count = 0;
	private T value = null;
	
	/**
	 * Constructor that initializes the node value
	 * @param value The initial value for this node. Cannot be NULL.
	 * @throws InvalidParameterException Thrown when value is NULL
	 */
	public CategoryNode(T value) throws InvalidParameterException {
		setCount(0);
		setValue(value);
	}
	
	/**
	 * Get the current count associated with this node.
	 * @return The count for this node.
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Set the count associated with this node.
	 * @param count The count to set for this node.
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Get the value associated with this node.
	 * @return The value of this node.
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * Set the value associated with this node.
	 * @param value The value to set for this node. Cannot be NULL.
	 * @throws InvalidParameterException Thrown when value is NULL.
	 */
	public void setValue(T value) throws InvalidParameterException {
		// Validate that value is not null
		if( value == null ) {
			throw new InvalidParameterException("A node cannot have a NULL value");
		}
		
		this.value = value;
	}
	
	/**
	 * Get a link from this category to the specified word.
	 * @param word The word to find a link for.
	 * @return The link if found or NULL if no link exists.
	 */
	public Link<T> getLink(String word) {
		return linksMap.get(word);
	}
	
	/**
	 * Get a string set of words this category node is linked to.
	 * @return A string set of words associated with this node.
	 */
	public Set<String> getLinkWords() {
		return linksMap.keySet();
	}
	
	/**
	 * Get all the links associated with this node
	 * @return A collection of links
	 */
	public Collection<Link<T>> getLinks() {
		return linksMap.values();
	}
	
	/**
	 * Adds a link to this category node. The link must reference this category node.
	 * If a link between this category and word already exists, it is replaced with this link.
	 * @param link The link to add. Must have a valid word node and point to this category node.
	 * @throws InvalidParameterException Thrown when the link is NULL, word node is NULL, or category is not this node.
	 */
	public void addLink(Link<T> link) throws InvalidParameterException {
		// Validate the link is not null
		if( link == null ) {
			throw new InvalidParameterException("Category node cannot add a NULL link");
		}
		
		// Validate the link's word node is not null
		WordNode<T> wordNode = link.getWordNode();
		if( wordNode == null ) {
			throw new InvalidParameterException("Category node cannot add a link with a NULL word node");
		}
		
		// Validate the link's category node is this node
		CategoryNode<T> categoryNode = link.getCategoryNode();
		if( this != categoryNode ) {
			throw new InvalidParameterException("Category node cannot add a link that points to a different category node");
		}
		
		// Put the link in the linkmap, replacing an existing link between these two nodes
		linksMap.put(wordNode.getValue(), link);
	}
	
	/**
	 * Remove a link between this category and a word.
	 * @param word The word to remove a link from.
	 * @return The link that was removed or NULL if the link didn't exist.
	 */
	public Link<T> removeLink(String word) {
		return linksMap.remove(word);
	}
	
	/**
	 * Get the link strength between this category and a word.
	 * @param word The word association to lookup.
	 * @return The strength of the link between this category and the word. If a link does not exist, 0 is returned.
	 */
	public int getLinkStrength(String word) {
		// Set default link strength
		int strength = 0;
		
		// Get link strength if link exists
		Link<T> link = linksMap.get(word);
		if( link != null ) {
			strength = link.getWeight();
		}
		
		// Return the link strength
		return strength;
	}
	
	/**
	 * Get the number of links associated with this category node.
	 * @return The number of links to/from this node.
	 */
	public int getLinksCount() {
		return linksMap.size();
	}
}
