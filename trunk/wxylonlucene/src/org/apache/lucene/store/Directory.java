package org.apache.lucene.store;

import java.io.IOException;

/** 这个类就是关于目录操作的
 *	管理锁工厂及其锁实例；
 *	管理Directory目录实例的基本属性，主要是通过文件名称进行管理；
 *	管理与操作该目录相关的一些流对象；
 *	管理索引文件的拷贝。
 * @version 创建时间：2009-9-24 下午04:58:59
 */
public abstract class Directory {

	volatile boolean isOpen = true;

	// 持有一个LockFactory的实例（实现锁定这个目录实例）
	protected LockFactory lockFactory;

	// 返回该目录下的所有文件数组.如果这个目录下没有文件存在，或者存在权限问题不能访问，该方法可能返回Null
	public abstract String[] list() throws IOException;

	//返回指定名称的文件是不是存在
	public abstract boolean fileExists(String name) throws IOException;

	//返回指定名称的文件最后修改的时间
	public abstract long fileModified(String name) throws IOException;

	//设置指定文件名的文件最后的修改时间为当前时间
	public abstract void touchFile(String name) throws IOException;

	//删除指定文件。
	public abstract void deleteFile(String name) throws IOException;

	/**
	 * @deprecated
	 */
	public abstract void renameFile(String from, String to) throws IOException;

	//返回指定文件的长度
	public abstract long fileLength(String name) throws IOException;

	//在当前目录下使用给定的名称创建一个空的文件。并且返回一个流来写该文件
	public abstract IndexOutput createOutput(String name) throws IOException;

	//Lucene使用该方法确保所有的针对该文件的写操作都会存储到Index。并且阻止machine/OS发生故障 破坏该index
	public void sync(String name) throws IOException {
	}

	//获取已经存在的一个文件的IndexInput流操作该文件。
	public abstract IndexInput openInput(String name) throws IOException;

	//返回已经存在的一个文件、并且使用指定大小的缓冲的IndexInput，但是当前目录也可能忽略该缓冲池的大小，当前主要是考虑CompoundFileReader和FSDirectory对于次参数的需求。
	public IndexInput openInput(String name, int bufferSize) throws IOException {
		return openInput(name);
	}

	//创建一个指定名称的锁
	public Lock makeLock(String name) {
		return lockFactory.makeLock(name);
	}

	//清除指定的锁定（强行解锁和删除）这不仅要求在这个时候当前的锁一定不在使用。
	public void clearLock(String name) throws IOException {
		if (lockFactory != null) {
			lockFactory.clearLock(name);
		}
	}

	//结束这个store
	public abstract void close() throws IOException;

	//设置LockFactory，此目录实例应使其锁定执行。每个LockFactory实例只用于一个目录（即，不要共用一个实例在多个目录）
	public void setLockFactory(LockFactory lockFactory) {
		this.lockFactory = lockFactory;
		lockFactory.setLockPrefix(this.getLockID());
	}

	//获得LockFactory，此目录例实例使用其锁定执行。请注意，这可能是无效的目录执行，提供自己锁执行
	public LockFactory getLockFactory() {
		return this.lockFactory;
	}

	//过去锁实例的唯一表示ID的字符串描述
	public String getLockID() {
		return this.toString();
	}

	//拷贝源目录src下的文件，复制到目的目录dest下面，拷贝完成后关闭源目录src
	public static void copy(Directory src, Directory dest, boolean closeDirSrc)
			throws IOException {
		final String[] files = src.list();

		if (files == null)
			throw new IOException("cannot read directory " + src
					+ ": list() returned null");

		byte[] buf = new byte[BufferedIndexOutput.BUFFER_SIZE];
		for (int i = 0; i < files.length; i++) {
			IndexOutput os = null;
			IndexInput is = null;
			try {
				// create file in dest directory
				os = dest.createOutput(files[i]);
				// read current file
				is = src.openInput(files[i]);
				// and copy to dest directory
				long len = is.length();
				long readCount = 0;
				while (readCount < len) {
					int toRead = readCount + BufferedIndexOutput.BUFFER_SIZE > len ? (int) (len - readCount)
							: BufferedIndexOutput.BUFFER_SIZE;
					is.readBytes(buf, 0, toRead);
					os.writeBytes(buf, toRead);
					readCount += toRead;
				}
			} finally {
				// graceful cleanup
				try {
					if (os != null)
						os.close();
				} finally {
					if (is != null)
						is.close();
				}
			}
		}
		if (closeDirSrc)
			src.close();
	}

	/**
	 * @throws AlreadyClosedException
	 *             if this Directory is closed
	 */
	protected final void ensureOpen() throws AlreadyClosedException {
		if (!isOpen)
			throw new AlreadyClosedException("this Directory is closed");
	}
}
