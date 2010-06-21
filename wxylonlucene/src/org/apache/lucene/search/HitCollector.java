package org.apache.lucene.search;



/** Lower-level search API.
 * <br>对检索结果进行选择的一个工具，并将选择后的结果保存在其中
 * @see Searcher#search(Query,HitCollector)
 * @version $Id: HitCollector.java 596462 2007-11-19 22:03:22Z hossman $
 */
public abstract class HitCollector {
  /** Called once for every document matching a query, with the document
   * number and its raw score.
   *
   * <P>If, for example, an application wished to collect all of the hits for a
   * query in a BitSet, then it might:<pre>
   *   Searcher searcher = new IndexSearcher(indexReader);
   *   final BitSet bits = new BitSet(indexReader.maxDoc());
   *   searcher.search(query, new HitCollector() {
   *       public void collect(int doc, float score) {
   *         bits.set(doc);
   *       }
   *     });
   * </pre>
   *
   * <p>Note: This is called in an inner search loop.  For good search
   * performance, implementations of this method should not call
   * {@link Searcher#doc(int)} or
   * {@link org.apache.lucene.index.IndexReader#document(int)} on every
   * document number encountered.  Doing so can slow searches by an order
   * of magnitude or more.
   * <p>Note: The <code>score</code> passed to this method is a raw score.
   * In other words, the score will not necessarily be a float whose value is
   * between 0 and 1.
   */
  public abstract void collect(int doc, float score);
}
