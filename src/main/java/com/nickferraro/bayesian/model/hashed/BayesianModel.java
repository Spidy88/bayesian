package com.nickferraro.bayesian.model.hashed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nickferraro.bayesian.IDataRow;
import com.nickferraro.bayesian.model.IBayesianModel;
import com.nickferraro.bayesian.model.hashed.core.CategoryNode;
import com.nickferraro.bayesian.model.hashed.core.Link;
import com.nickferraro.bayesian.model.hashed.core.WordNode;

/**
 * This BayesianModel class uses a Graph-like Hash to map input words to an output category.
 * This class is Thread-safe.
 * 
 * @author Nick Ferraro
 *
 * @param <T> The category data type
 */
public class BayesianModel<T> implements IBayesianModel<T> {
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private Lock readLock = readWriteLock.readLock();
	private Lock writeLock = readWriteLock.writeLock();
	
	private final HashMap<T, CategoryNode<T>> categoryNodes = new HashMap<T, CategoryNode<T>>();
	private final HashMap<String, WordNode<T>> wordNodes = new HashMap<String, WordNode<T>>();
	private int totalRows = 0;
	
	/**
	 * Default constructor
	 */
	public BayesianModel() {}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public boolean addCategory(T category) {
		// Lock
		writeLock.lock();
		
		try {
			// Add category node with thread unsafe private method
			return _addCategory(category);
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public int addCategories(List<T> categories) {
		// Validate categories parameter
		if( categories == null ) {
			return 0;
		}
		
		// Lock
		writeLock.lock();
		
		try {
			// Iterate and add all categories
			int addedCount = 0;
			for(T category : categories) {
				// Count categories actually added
				if( _addCategory(category) ) {
					++addedCount;
				}
			}
			
			return addedCount;
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public T removeCategory(T category) {
		// Validate category parameter
		if( category == null ) {
			return null;
		}
		
		// Lock
		writeLock.lock();
		
		try {
			// Remove category
			return _removeCategory(category);
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public List<T> removeCategories(List<T> categories) {
		ArrayList<T> removedCategories = new ArrayList<T>();
		
		// Validate categories parameter
		if( categories == null ) {
			return removedCategories;
		}
		
		// Lock
		writeLock.lock();
		
		try {
			// Iterate over categories and remove each one
			for( T category : categories ) {
				T removedCategory = _removeCategory(category);
				if( removedCategory != null ) {
					removedCategories.add(removedCategory);
				}
			}
			
			return removedCategories;
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public boolean addWord(String word) {
		// Lock
		writeLock.lock();
		
		try {
			// Add word node with thread unsafe private method
			return _addWord(word);
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public int addWords(List<String> words) {
		// Validate words parameter
		if( words == null ) {
			return 0;
		}
				
		// Lock
		writeLock.lock();
		
		try {
			// Iterate and add all words
			int addedCount = 0;
			for(String word : words) {
				// Count words actually added
				if( _addWord(word) ) {
					++addedCount;
				}
			}
			
			return addedCount;
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method requires a valid category and non-null set of unique words from the data row. This method is thread-safe.
	 */
	@Override
	public boolean addDataRow(IDataRow<T> dataRow) {
		// Lock
		writeLock.lock();
		
		try {
			// Add data row with thread unsafe private method
			return _addDataRow(dataRow);
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * This method requires data rows with a valid category and non-null set of unique words from the data row. This method is thread-safe.
	 */
	@Override
	public int addDataRows(List<IDataRow<T>> dataRows) {
		// Validates data rows parameter
		if( dataRows == null ) {
			return 0;
		}
		
		// Lock
		writeLock.lock();
		
		try {
			// Iterate and add all data rows with thread unsafe private method
			int addedCount = 0;
			for( IDataRow<T> dataRow : dataRows ) {
				// If the row is successfully added, increase addedCount
				if( _addDataRow(dataRow) ) {
					++addedCount;
				}
			}
			
			return addedCount;
		} finally {
			// Unlock
			writeLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public int getTotalRows() {
		// Lock
		readLock.lock();
		
		try {
			// Get total rows count
			return this.totalRows;
		} finally {
			// Unlock
			readLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public Set<T> getUniqueCategories() {
		// Lock
		readLock.lock();

		try {
			// Get number of unique category nodes
			return categoryNodes.keySet();
		} finally {
			// Unlock
			readLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public Set<String> getUniqueWords() {
		// Lock
		readLock.lock();

		try {
			// Get number of unique category nodes
			return wordNodes.keySet();
		} finally {	
			// Unlock
			readLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public List<ILink<T>> getLinks() {
		// Lock
		readLock.lock();
		
		try {
			// Create a list to aggregate links
			List<ILink<T>> aggregatedLinks = new ArrayList<ILink<T>>();
			
			// Iterate over category nodes and aggregate links
			Collection<CategoryNode<T>> categoryNodes = this.categoryNodes.values();
			for(CategoryNode<T> categoryNode : categoryNodes ) {
				Collection<Link<T>> links = categoryNode.getLinks();
				aggregatedLinks.addAll(links);
			}
			
			return aggregatedLinks;
		} finally {
			// Unlock
			readLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public int countRowsWithCategory(T category) {
		// Validate category parameter
		if( category == null ) {
			return 0;
		}
		
		// Lock
		readLock.lock();
		
		try {
			// Get category node
			CategoryNode<T> categoryNode = categoryNodes.get(category);
			
			// Set the default return to 0 and get category count
			int count = 0;
			if( categoryNode != null ) {
				count = categoryNode.getCount();
			}
			
			return count;
		} finally {
			// Unlock
			readLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public int countRowsWithWord(String word) {
		// Validate word parameter
		if( word == null ) {
			return 0;
		}
				
		// Lock
		readLock.lock();

		try {
			// Get word node
			WordNode<T> wordNode = wordNodes.get(word);
					
			// Set the default return to 0 and get word count
			int count = 0;
			if( wordNode != null ) {
				count = wordNode.getCount();
			}
					
			return count;
		} finally {
			// Unlock
			readLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is thread-safe.
	 */
	@Override
	public int countRowsWithCategoryWithWord(T category, String word) {
		// Validate category and word parameters
		if( category == null || word == null ) {
			return 0;
		}
		
		// Lock
		readLock.lock();
		
		try {
			// Get category node to count from (could start from WordNode as well since link is shared)
			CategoryNode<T> categoryNode = categoryNodes.get(category);
			
			// Set the default return to 0 and then get link strength
			int count = 0;
			if( categoryNode != null ) {
				count = categoryNode.getLinkStrength(word);
			}
			
			return count;
		} finally {
			// Unlock
			readLock.unlock();
		}
	}
	
	public void setTotalRows(int totalRows) {
		if( totalRows < 0 ) {
			return;
		}
		
		writeLock.lock();
		
		try {
			this.totalRows = totalRows;
		} finally {
			writeLock.unlock();
		}
	}
	
	public void setCategoryCount(T category, int count) {
		if( category == null || count < 0 ) {
			return;
		}
		
		writeLock.lock();
		
		try {
			_addCategory(category);
			categoryNodes.get(category).setCount(count);
		} finally {
			writeLock.unlock();
		}
	}
	
	public void setWordCount(String word, int count) {
		if( word == null || count < 0 ) {
			return;
		}
		
		writeLock.lock();
		
		try {
			_addWord(word);
			wordNodes.get(word).setCount(count);
		} finally {
			writeLock.unlock();
		}
	}

	public void setLinkWeight(T category, String word, int weight) {
		if( category == null || word == null || weight < 0 ) {
			return;
		}
		
		writeLock.lock();
		
		try {
			_addCategory(category);
			_addWord(word);
			CategoryNode<T> categoryNode = categoryNodes.get(category);
			WordNode<T> wordNode = wordNodes.get(word);
			Link<T> link = categoryNode.getLink(wordNode.getValue());
			if( link == null ) {
				link = new Link<T>(categoryNode, wordNode);
				categoryNode.addLink(link);
				wordNode.addLink(link);
			}
			
			link.setWeight(weight);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Add a category to this model. This method is not thread-safe.
	 * @param category The category to add
	 * @return TRUE if the category was successfully added. FALSE if the category already exists or is NULL.
	 */
	private boolean _addCategory(T category) {
		// Validate category parameter
		if( category == null ) {
			return false;
		}
		
		// Check if category node already exists
		if( !categoryNodes.containsKey(category) ) {
			// Category node does not exist, create it and add it
			CategoryNode<T> categoryNode = new CategoryNode<T>(category);
			categoryNodes.put(category, categoryNode);
			
			return true;
		}
		
		// Category node already exists, not added
		return false;
	}
	
	/**
	 * Add a word to this model. This method is not thread-safe.
	 * @param word The word to add
	 * @return TRUE if the word was successfully added. FALSE if the word already exists or is NULL.
	 */
	private boolean _addWord(String word) {
		// Validate word parameter
		if( word == null ) {
			return false;
		}
		
		// Check if the word node already exists
		if( !wordNodes.containsKey(word) ) {
			// Word node does not exist, create it and add it
			WordNode<T> wordNode = new WordNode<T>(word);
			wordNodes.put(word, wordNode);
			
			return true;
		}
		
		// Word node already exists, not added
		return false;
	}
	
	/**
	 * Add a row of data to this model. This method is not thread-safe.
	 * @param dataRow The row of data to add
	 * @return TRUE if the data row was successfully added
	 */
	private boolean _addDataRow(IDataRow<T> dataRow) {
		// Validate data row parameter
		if( dataRow == null ) {
			return false;
		}
		
		// Get data row information
		T rowCategory = dataRow.getCategory();
		Set<String> rowWords = dataRow.getUniqueSentenceWords();
		
		// Validate data row information
		if( rowCategory == null || rowWords == null ) {
			return false;
		}
		
		// Add category node if it doesn't already exist
		_addCategory(rowCategory);
		
		// Increase total row count
		++totalRows;
		
		// Increase category count
		CategoryNode<T> categoryNode = categoryNodes.get(rowCategory);
		categoryNode.setValue(categoryNode.getValue());
		categoryNode.setCount(categoryNode.getCount() + 1);
		
		// Iterate and add all row words
		for(String rowWord : rowWords) {
			// Add word node if it doesn't already exist
			_addWord(rowWord);
			
			// Increase word count
			WordNode<T> wordNode = wordNodes.get(rowWord);
			wordNode.setValue(wordNode.getValue());
			wordNode.setCount(wordNode.getCount() + 1);
			
			// Create link between nodes (if it doesn't exist) and increment the weight
			_linkNodes(categoryNode, wordNode);
		}
		
		return true;
	}
	
	/**
	 * Link a category node to a word node (if they are not already linked) and increase the link strength.
	 * This method is not thread-safe.
	 * @param categoryNode The category node to link
	 * @param wordNode The word node to link
	 */
	private void _linkNodes(CategoryNode<T> categoryNode, WordNode<T> wordNode) {
		// Find link from category node (should exist on both nodes
		Link<T> link = categoryNode.getLink(wordNode.getValue());
		if( link == null ) {
			// Create the link if it doesn't exist
			link = new Link<T>(categoryNode, wordNode);
			link.setWeight(0);
			
			// Add link to both nodes
			categoryNode.addLink(link);
			wordNode.addLink(link);
		}
		
		// Increase the weight of this link
		link.setWeight(link.getWeight() + 1);
	}
	
	/**
	 * Remove a category from the bayesian model. This method is not thread-safe.
	 * @param category The category to remove from the model
	 * @return The category that is removed or NULL if that category does not exist
	 */
	private T _removeCategory(T category) {
		// Validate category parameter
		if(category == null ) {
			return null;
		}
		
		// Remove category node if it exists
		CategoryNode<T> categoryNode = categoryNodes.remove(category);
		if( categoryNode == null ) {
			return null;
		}
		
		// Update total number of rows
		this.totalRows -= categoryNode.getCount();
			
		// Find all associated words
		Set<String> words = categoryNode.getLinkWords();
		for(String word : words) {
			// Remove word link to category and update word count
			WordNode<T> associatedWordNode = wordNodes.get(word);
			associatedWordNode.removeLink(category);
			associatedWordNode.setCount(associatedWordNode.getCount() - categoryNode.getLinkStrength(word));
				
			// Remove word node if it no longer has any associations
			if( associatedWordNode.getCount() == 0 && associatedWordNode.getLinksCount() == 0) {
				wordNodes.remove(word);
			}
		}
		
		// Return the removed category
		return categoryNode.getValue();
	}
}
