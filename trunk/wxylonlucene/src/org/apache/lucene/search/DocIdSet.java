package org.apache.lucene.search;

/**
 * <b>DocIdSet</b>包含一个id得set集合,实现类必须提供一个 <b>DocIdSetIterator</b> 来访问集合
 */
public abstract class DocIdSet {
	public abstract DocIdSetIterator iterator();
}
