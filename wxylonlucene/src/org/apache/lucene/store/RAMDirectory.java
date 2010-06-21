package org.apache.lucene.store;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 此类描述的是：一个对于Directory的内存实现的类 锁工厂是有SingleInstanceLockFactory来实现，但是锁工厂可以更改
 * @version 创建时间：2009-9-25 上午11:43:32
 */
public class RAMDirectory extends Directory implements Serializable {

	private static final long serialVersionUID = 1l;
	// 首先定了保存文件名以及的Map
	HashMap fileMap = new HashMap();
	//文件占用的字节数   
	long sizeInBytes = 0;
	
	/**
	 * 构造函数。指定LockFactory为该类的一个实例SingleInstanceLockFactory，
	 * SingleInstanceLockFactory类的特点是，
	 * 所有的加锁操作必须通过该SingleInstanceLockFactory的一个实例而发生，
	 * 也就是说，在进行加锁操作的时候，必须获取到这个SingleInstanceLockFactory的实例
	 * 创建一个新的实例 RAMDirectory
	 */
	public RAMDirectory() {
		setLockFactory(new SingleInstanceLockFactory());
	}

	/**
	 * 定义一个新的RAMDirectory，并且指定其目录，这个做的目的可以将一个文件系统的索引加载到内存中，
	 * 这个只能使用在该索引能够被全部加在到内存中。这次只是将系统文件的索引拷贝一份到内存中，
	 * 加载后，系统文件索引发生变化，将不会在该实例中体现。
	 * 创建一个新的实例 RAMDirectory
	 * @param dir
	 * @throws IOException
	 */
	public RAMDirectory(Directory dir) throws IOException {
		this(dir, false);
	}
	
	/**
	 * 根据目录，以及参数将该目录中的所有文件复制到内存中，需要复制的文件已经存在，则直接覆盖。
	 * closeDir表示是否关闭源目录
	 * 创建一个新的实例 RAMDirectory
	 * @param dir
	 * @param closeDir
	 * @throws IOException
	 */
	private RAMDirectory(Directory dir, boolean closeDir) throws IOException {
		this();
		Directory.copy(dir, this, closeDir);
	}

	/**
	 * 根据提供的文件路径，将该文件路径下的所有的文件复制到内存中，并且创建一个RAMDirectory对象。
	 * 如果需要复制的对象已经存在，则覆盖原来的文件
	 * 创建一个新的实例 RAMDirectory
	 * @param dir
	 * @throws IOException
	 */
	public RAMDirectory(File dir) throws IOException {
		this(FSDirectory.getDirectory(dir), true);
	}

	/**
	 * 根据提供的String路径，将该文件路径下的所有的文件复制到内存中，并且创建一个RAMDirectory对象。
	 * 如果需要复制的对象已经存在，则覆盖原来的文件
	 * 创建一个新的实例 RAMDirectory
	 * @param dir
	 * @throws IOException
	 */
	public RAMDirectory(String dir) throws IOException {
		this(FSDirectory.getDirectory(dir), true);
	}

	/**返回所有该文件该内存索引中的所有文件  */  
	public synchronized final String[] list() {
		ensureOpen();
		Set fileNames = fileMap.keySet();
		String[] result = new String[fileNames.size()];
		int i = 0;
		Iterator it = fileNames.iterator();
		while (it.hasNext())
			result[i++] = (String) it.next();
		return result;
	}

	/**如果指定文件名的文件在索引中存在，则返回true，否则返回false*/  
	public final boolean fileExists(String name) {
		ensureOpen();
		RAMFile file;
		synchronized (this) {
			file = (RAMFile) fileMap.get(name);
		}
		return file != null;
	}

	/**返回指定文件最后的修改时间   */  
	public final long fileModified(String name) throws IOException {
		ensureOpen();
		RAMFile file;
		synchronized (this) {
			file = (RAMFile) fileMap.get(name);
		}
		if (file == null)
			throw new FileNotFoundException(name);
		return file.getLastModified();
	}

	/**将指定文件的最后修改时间设置为现在 */  
	public void touchFile(String name) throws IOException {
		ensureOpen();
		RAMFile file;
		synchronized (this) {
			file = (RAMFile) fileMap.get(name);
		}
		if (file == null)
			throw new FileNotFoundException(name);

		long ts2, ts1 = System.currentTimeMillis();
		do {
			try {
				Thread.sleep(0, 1);
			} catch (InterruptedException e) {
			}
			ts2 = System.currentTimeMillis();
		} while (ts1 == ts2);

		file.setLastModified(ts2);
	}

	/** 返回指定文件的大小*/  
	public final long fileLength(String name) throws IOException {
		ensureOpen();
		RAMFile file;
		synchronized (this) {
			file = (RAMFile) fileMap.get(name);
		}
		if (file == null)
			throw new FileNotFoundException(name);
		return file.getLength();
	}

	/** 返回当前目录中所有的文件大小*/  
	public synchronized final long sizeInBytes() {
		ensureOpen();
		return sizeInBytes;
	}

	/** 删除指定文件名的文件*/  
	public synchronized void deleteFile(String name) throws IOException {
		ensureOpen();
		RAMFile file = (RAMFile) fileMap.get(name);
		if (file != null) {
			fileMap.remove(name);
			file.directory = null;
			sizeInBytes -= file.sizeInBytes; // updates to
												// RAMFile.sizeInBytes
												// synchronized on directory
		} else
			throw new FileNotFoundException(name);
	}

	/**重命名 */  
	public synchronized final void renameFile(String from, String to)
			throws IOException {
		ensureOpen();
		RAMFile fromFile = (RAMFile) fileMap.get(from);
		if (fromFile == null)
			throw new FileNotFoundException(from);
		RAMFile toFile = (RAMFile) fileMap.get(to);
		if (toFile != null) {
			sizeInBytes -= toFile.sizeInBytes; // updates to
												// RAMFile.sizeInBytes
												// synchronized on directory
			toFile.directory = null;
		}
		fileMap.remove(from);
		fileMap.put(to, fromFile);
	}

	 /**根据制定的名称，创建一个空文件，并且返回操作该文件的输出流。 */  
	public IndexOutput createOutput(String name) throws IOException {
		ensureOpen();
		RAMFile file = new RAMFile(this);
		synchronized (this) {
			RAMFile existing = (RAMFile) fileMap.get(name);
			if (existing != null) {
				sizeInBytes -= existing.sizeInBytes;
				existing.directory = null;
			}
			fileMap.put(name, file);
		}
		return new RAMOutputStream(file);
	}

	/** 根据指定的文件名返回一个操作该文件的输入流 */
	public IndexInput openInput(String name) throws IOException {
		ensureOpen();
		RAMFile file;
		synchronized (this) {
			file = (RAMFile) fileMap.get(name);
		}
		if (file == null)
			throw new FileNotFoundException(name);
		return new RAMInputStream(file);
	}

	/** 关闭并且释放索引占用的内存 */  
	public void close() {
		isOpen = false;
		fileMap = null;
	}
}
