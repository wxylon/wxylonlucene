package org.apache.lucene.search;

import java.io.IOException;

/**
 * 此类描述的是：此抽象类定义的方法来迭代一套非减少文档的ID。 
 * @version 创建时间：Nov 14, 2009 12:58:57 PM
 */
public abstract class DocIdSetIterator {
    
	/**
     * 返回当前文件的编号 要求第一次{@link #next()}
     */
    public abstract int doc();
    
    /**
     * 当且仅当集合中有下一个docID,返回true
     */
    public abstract boolean next() throws IOException;
    
    /** Skips entries to the first beyond the current whose document number is
     * greater than or equal to <i>target</i>. <p>Returns true iff there is such
     * an entry.  <p>Behaves as if written: <pre>
     *   boolean skipTo(int target) {
     *     do {
     *       if (!next())
     *         return false;
     *     } while (target > doc());
     *     return true;
     *   }
     * </pre>
     * Some implementations are considerably more efficient than that.
     */
    public abstract boolean skipTo(int target) throws IOException;
}
