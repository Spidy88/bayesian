package com.nickferraro.bayesian.model.hashed.core;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;

/**
 * A node that represents a word for the hashed bayesian model.
 * This class keeps a count of the number of times a word exists in the model, as well as links to associated categories.
 * @author Nick Ferraro
 *
 * @param <T> The category data type this word is associated with.
 */
public class WordNode<T> {
	private final HashMap<T, Link<T>> linksMap = new HashMap<T, Link<T>>();
	private int count = 0;
	private String value = null;
	
	/**
	 * Constructor that initializes the node value
	 * @param value The initial value for this node. Cannot be NULL.
	 * @throws InvalidParameterException Thrown when value is NULL
	 */
	public WordNode(String value) throws InvalidParameterException {
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
	public String getValue() {
		return value;
	}
	
	/**
	 * Set the value associated with this node.
	 * @param value The value to set for this node. Cannot be NULL.
	 * @throws InvalidParameterException Thrown when value is NULL.
	 */
	public void setValue(String value) throws InvalidParameterException {
		// Validate that value is not null
		if( value == null ) {
			throw new InvalidParameterException("A node cannot have a NULL value");
		}
		
		this.value = value;
	}
	
	/**
	 * Get a link from this word to the specified category.
	 * @param category The cateogry to find a link for.
	 * @return The link if found or NULL if no link exists.
	 */
	public Link<T> getLink(T category) {
		return linksMap.get(category);
	}
	
	/**
	 * Get all the category links associated with this word node.
	 * @return A collection of links between this node and other category nodes.
	 */
	public Collection<Link<T>> getLinks() {
		return linksMap.values();
	}
	
	/**
	 * Adds a link to this word node. The link must reference this word node.
	 * If a link between this word and category already exists, it is replaced with this link.
	 * @param link The link to add. Must have a valid category node and point to this word node.
	 * @throws InvalidParameterException Thrown when the link is NULL, category node is NULL, or word is not this node.
	 */
	public void addLink(Link<T> link) throws InvalidParameterException {
		// Validate the link is not null
		if( link == null ) {
			throw new InvalidParameterException("Word node cannot add a NULL link");
		}
		
		// Validate the link's category node is not null
		CategoryNode<T> categoryNode = link.getCategoryNode();
		if( categoryNode == null ) {
			throw new InvalidParameterException("Word node cannot add a link with a NULL category node");
		}
		
		// Validate the link's word node is this node
		WordNode<T> wordNode = link.getWordNode();
		if( this != wordNode ) {
			throw new InvalidParameterException("Word node cannot add a link that points to a different word node");
		}
		
		// Put the link in the linkmap, replacing an existing link between these two nodes
		linksMap.put(categoryNode.getValue(), link);
	}
	
	/**
	 * Remove a link between this word and a category.
	 * @param category The category to remove a link from.
	 * @return The link that was removed or NULL if the link didn't exist.
	 */
	public Link<T> removeLink(T category) {
		return linksMap.remove(category);
	}
	
	/**
	 * Get the link strength between this word and a category.
	 * @param category The category association to lookup.
	 * @return The strength of the link between this word and the category. If a link does not exist, 0 is returned.
	 */
	public int getLinkStrength(T category) {
		// Set default link strength
		int strength = 0;
		
		// Get link strength if link exists
		Link<T> link = linksMap.get(category);
		if( link != null ) {
			strength = link.getWeight();
		}
		
		// Return the link strength
		return strength;
	}
	
	/**
	 * Get the number of links associated with this word node.
	 * @return The number of links to/from this node.
	 */
	public int getLinksCount() {
		return linksMap.size();
	}
}
