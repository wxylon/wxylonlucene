package org.apache.lucene.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.IndexFileNameFilter;
import org.apache.lucene.index.IndexWriter;

public class FSDirectory extends Directory {

	// 主要是用来缓存FilePath与FSDirectory，主要是保证针对统一目录，只能有一个FSDirectory实例
	private static final Map DIRECTORIES = new HashMap();
	
	//主要判断是否给目录上锁
	private static boolean disableLocks = false;

	//设置是否给目录上锁，一般只能用户只读目录。
	public static void setDisableLocks(boolean doDisableLocks) {
		FSDirectory.disableLocks = doDisableLocks;
	}

	//f返回是否给目录上锁
	public static boolean getDisableLocks() {
		return FSDirectory.disableLocks;
	}

	/**
	 * Directory specified by <code>org.apache.lucene.lockDir</code> or
	 * <code>java.io.tmpdir</code> system property.
	 * 
	 * @deprecated As of 2.1, <code>LOCK_DIR</code> is unused because the
	 *             write.lock is now stored by default in the index directory.
	 *             If you really want to store locks elsewhere you can create
	 *             your own {@link SimpleFSLockFactory} (or
	 *             {@link NativeFSLockFactory}, etc.) passing in your preferred
	 *             lock directory. Then, pass this <code>LockFactory</code>
	 *             instance to one of the <code>getDirectory</code> methods
	 *             that take a <code>lockFactory</code> (for example,
	 *             {@link #getDirectory(String, LockFactory)}).
	 */
	public static final String LOCK_DIR = System.getProperty(
			"org.apache.lucene.lockDir", System.getProperty("java.io.tmpdir"));

	//主要是获取FSDirectory的实例
	private static Class IMPL;
	static {
		try {
			String name = System.getProperty(
					"org.apache.lucene.FSDirectory.class", FSDirectory.class
							.getName());
			//首先获取该类的名称，然后在获取该类的Class实例
			IMPL = Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("cannot load FSDirectory class: "
					+ e.toString(), e);
		} catch (SecurityException se) {
			try {
				//直接使用JAVA反射获取该类Class实例
				IMPL = Class.forName(FSDirectory.class.getName());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(
						"cannot load default FSDirectory class: "
								+ e.toString(), e);
			}
		}
	}
	//获取加密
	private static MessageDigest DIGESTER;

	static {
		try {
			DIGESTER = MessageDigest.getInstance("MD5"); //使用MD5加密
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	/** A buffer optionally used in renameTo method */
	private byte[] buffer = null;

	/**静态方法，根据基于指定目录，返回该目录中对应的一个FSDirectory实例 */
	public static FSDirectory getDirectory(String path) throws IOException {
		return getDirectory(new File(path), null);
	}

	/** 静态方法，根据指定的路径path以及锁工厂LockFactory参数，返回该路径的一个FSDirectiry实例 */
	public static FSDirectory getDirectory(String path, LockFactory lockFactory)
			throws IOException {
		return getDirectory(new File(path), lockFactory);
	}

	/**静态方法，根据指定的File对象，返回该路径的一个FSDirectiry实例 */
	public static FSDirectory getDirectory(File file) throws IOException {
		return getDirectory(file, null);
	}

	/**静态方法，根据指定File对象以及锁工厂LockFactory参数，返回该路径的一个FSDirectiry实例
	其他方法，都最终转化为该方法来实现 */
	public static FSDirectory getDirectory(File file, LockFactory lockFactory)
			throws IOException {
		//定义一个File对象，以传递的参数作为目录
		file = new File(file.getCanonicalPath());
		//首先判断文件存在时，是否是目录
		if (file.exists() && !file.isDirectory())
			throw new IOException(file + " not a directory");
		//如果文件不存在。则创建
		if (!file.exists())
			if (!file.mkdirs())//创建所有目录
				throw new IOException("Cannot create directory: " + file);

		FSDirectory dir;//定义一个类 FSDirectory
		synchronized (DIRECTORIES) {
			dir = (FSDirectory) DIRECTORIES.get(file);
			if (dir == null) {
				try {
					//调用静态内部类IMPL获取一个与文件系统目录有关的Directory类，并加载该类
					dir = (FSDirectory) IMPL.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(
							"cannot load FSDirectory class: " + e.toString(), e);
				}
				// 根据指定的file和lockFactory，调用该类Directory的init方法，进行FSDirectory的初始化初始化工作
				dir.init(file, lockFactory);
				//将该实例放置到HashMap中
				DIRECTORIES.put(file, dir);
			} else {
				//如果该目录dir管理所用的锁工厂实例为空，或者不是同一个锁工厂实例，抛出异常
				if (lockFactory != null && lockFactory != dir.getLockFactory()) {
					throw new IOException(
							"Directory was previously created with a different LockFactory instance; please pass null as the lockFactory instance and use setLockFactory to change it");
				}
			}
		}
		synchronized (dir) {
			//用于记录该目录dir被引用的计数增加1
			dir.refCount++;
		}
		return dir;
	}

	/**
	 * @deprecated
	 */
	public static FSDirectory getDirectory(String path, boolean create)
			throws IOException {
		return getDirectory(new File(path), create);
	}

	/**
	 * @deprecated
	 */
	public static FSDirectory getDirectory(File file, boolean create)
			throws IOException {
		FSDirectory dir = getDirectory(file, null);
		if (create) {
			dir.create();
		}

		return dir;
	}

	private void create() throws IOException {
		if (directory.exists()) {
			String[] files = directory.list(IndexFileNameFilter.getFilter()); // clear
																				// old
																				// files
			if (files == null)
				throw new IOException("cannot read directory "
						+ directory.getAbsolutePath()
						+ ": list() returned null");
			for (int i = 0; i < files.length; i++) {
				File file = new File(directory, files[i]);
				if (!file.delete())
					throw new IOException("Cannot delete " + file);
			}
		}
		lockFactory.clearLock(IndexWriter.WRITE_LOCK_NAME);
	}
	//File directory是FSDirectory类的一个成员
	private File directory = null;
	//用于记录该目录dir被引用的计数增加1
	private int refCount;

	protected FSDirectory() {
	}; // permit subclassing

	private void init(File path, LockFactory lockFactory) throws IOException {

		//根据指定的file和lockFactory，调用该类Directory的init方法，进行FSDirectory的初始化初始化工作 
		directory = path;

		boolean doClearLockID = false;

		if (lockFactory == null) {//锁工厂实例为null
			if (disableLocks) {//如果锁不可以使用
				//调用NoLockFactory类，获取NoLockFactory实例，为当前的锁工厂实例。其实NoLockFactory是一个单态(singleton)模式的工厂类，应用中只能有一个锁实例，不需要进行同步
				lockFactory = NoLockFactory.getNoLockFactory();
			} else {//如果锁可以使用，获取锁工厂类名称的字符串描述
				String lockClassName = System
						.getProperty("org.apache.lucene.store.FSDirectoryLockFactoryClass");
				//如果获取的锁工厂类名称的字符串描述不为null，而且者不为空
				if (lockClassName != null && !lockClassName.equals("")) {
					Class c;

					try {
						//创建一个Class对象，加载该锁工厂类
						c = Class.forName(lockClassName);
					} catch (ClassNotFoundException e) {
						throw new IOException("unable to find LockClass "
								+ lockClassName);
					}
					try {
						//获取一个锁工厂的实例  
						lockFactory = (LockFactory) c.newInstance();
					} catch (IllegalAccessException e) {
						throw new IOException(
								"IllegalAccessException when instantiating LockClass "
										+ lockClassName);
					} catch (InstantiationException e) {
						throw new IOException(
								"InstantiationException when instantiating LockClass "
										+ lockClassName);
					} catch (ClassCastException e) {
						throw new IOException("unable to cast LockClass "
								+ lockClassName + " instance to a LockFactory");
					}
					// 根据获取的锁工厂实例的类型来设置对文件File path加锁的方式
					if (lockFactory instanceof NativeFSLockFactory) {
						((NativeFSLockFactory) lockFactory).setLockDir(path);
					} else if (lockFactory instanceof SimpleFSLockFactory) {
						((SimpleFSLockFactory) lockFactory).setLockDir(path);
					}
				} else {
					// 没有其他的锁工厂类可用，则使用默认的锁工厂类创建一个锁工厂实例     
					lockFactory = new SimpleFSLockFactory(path);
					doClearLockID = true;
				}
			}
		}
		//设置当前FSDirectory相关锁工厂实例
		setLockFactory(lockFactory);

		if (doClearLockID) {
			// Clear the prefix because write.lock will be
			// stored in our directory:
			lockFactory.setLockPrefix(null);
		}
	}

	/** 返回所有的在当前目录下的Lucene索引文件名，并且保存在数组中*/
	public String[] list() {
		ensureOpen();
		return directory.list(IndexFileNameFilter.getFilter());
	}

	/**检查指定名称的文件是否存在 */
	public boolean fileExists(String name) {
		ensureOpen();
		File file = new File(directory, name);
		return file.exists();
	}

	/**  返回指定文件最后修改的时间*/
	public long fileModified(String name) {
		ensureOpen();
		File file = new File(directory, name);
		return file.lastModified();
	}

	/** 返回指定目录和文件名的文件最后修改的时间*/
	public static long fileModified(File directory, String name) {
		File file = new File(directory, name);
		return file.lastModified();
	}

	/**设置指定文件最后修改的时间为当前时间*/
	public void touchFile(String name) {
		ensureOpen();
		File file = new File(directory, name);
		file.setLastModified(System.currentTimeMillis());
	}

	/**返回指定文件的长度*/
	public long fileLength(String name) {
		ensureOpen();
		File file = new File(directory, name);
		return file.length();
	}

	/**删除当前目录中指定文件名的文件*/
	public void deleteFile(String name) throws IOException {
		ensureOpen();
		File file = new File(directory, name);
		if (!file.delete())
			throw new IOException("Cannot delete " + file);
	}

	/**
	 * @deprecated
	 */
	public synchronized void renameFile(String from, String to)
			throws IOException {
		ensureOpen();
		File old = new File(directory, from);
		File nu = new File(directory, to);

		/*
		 * This is not atomic. If the program crashes between the call to
		 * delete() and the call to renameTo() then we're screwed, but I've been
		 * unable to figure out how else to do this...
		 */

		if (nu.exists())
			if (!nu.delete())
				throw new IOException("Cannot delete " + nu);

		// Rename the old file to the new one. Unfortunately, the renameTo()
		// method does not work reliably under some JVMs. Therefore, if the
		// rename fails, we manually rename by copying the old file to the new
		// one
		if (!old.renameTo(nu)) {
			java.io.InputStream in = null;
			java.io.OutputStream out = null;
			try {
				in = new FileInputStream(old);
				out = new FileOutputStream(nu);
				// see if the buffer needs to be initialized. Initialization is
				// only done on-demand since many VM's will never run into the
				// renameTo
				// bug and hence shouldn't waste 1K of mem for no reason.
				if (buffer == null) {
					buffer = new byte[1024];
				}
				int len;
				while ((len = in.read(buffer)) >= 0) {
					out.write(buffer, 0, len);
				}

				// delete the old file.
				old.delete();
			} catch (IOException ioe) {
				IOException newExc = new IOException("Cannot rename " + old
						+ " to " + nu);
				newExc.initCause(ioe);
				throw newExc;
			} finally {
				try {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							throw new RuntimeException(
									"Cannot close input stream: "
											+ e.toString(), e);
						}
					}
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							throw new RuntimeException(
									"Cannot close output stream: "
											+ e.toString(), e);
						}
					}
				}
			}
		}
	}

	/** 创建一个名称为name的文件，返回一个输出流，以便对该文件进行写入操作 */
	public IndexOutput createOutput(String name) throws IOException {
		ensureOpen();
		File file = new File(directory, name);
		if (file.exists() && !file.delete()) // delete existing, if any
			throw new IOException("Cannot overwrite: " + file);

		return new FSIndexOutput(file);
	}

	public void sync(String name) throws IOException {
		ensureOpen();
		File fullFile = new File(directory, name);
		boolean success = false;
		int retryCount = 0;
		IOException exc = null;
		while (!success && retryCount < 5) {
			retryCount++;
			RandomAccessFile file = null;
			try {
				try {
					file = new RandomAccessFile(fullFile, "rw");
					file.getFD().sync();
					success = true;
				} finally {
					if (file != null)
						file.close();
				}
			} catch (IOException ioe) {
				if (exc == null)
					exc = ioe;
				try {
					// Pause 5 msec
					Thread.sleep(5);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
		if (!success)
			// Throw original exception
			throw exc;
	}

	// Inherit javadoc
	public IndexInput openInput(String name) throws IOException {
		ensureOpen();
		return openInput(name, BufferedIndexInput.BUFFER_SIZE);
	}

	// Inherit javadoc
	public IndexInput openInput(String name, int bufferSize) throws IOException {
		ensureOpen();
		return new FSIndexInput(new File(directory, name), bufferSize);
	}

	/**
	 * So we can do some byte-to-hexchar conversion below
	 */
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public String getLockID() {
		ensureOpen();
		String dirName; // name to be hashed
		try {
			dirName = directory.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}

		byte digest[];
		synchronized (DIGESTER) {
			digest = DIGESTER.digest(dirName.getBytes());
		}
		StringBuffer buf = new StringBuffer();
		buf.append("lucene-");
		for (int i = 0; i < digest.length; i++) {
			int b = digest[i];
			buf.append(HEX_DIGITS[(b >> 4) & 0xf]);
			buf.append(HEX_DIGITS[b & 0xf]);
		}

		return buf.toString();
	}

	/** Closes the store to future operations. */
	public synchronized void close() {
		if (isOpen && --refCount <= 0) {
			isOpen = false;
			synchronized (DIRECTORIES) {
				DIRECTORIES.remove(directory);
			}
		}
	}

	public File getFile() {
		ensureOpen();
		return directory;
	}

	/** For debug output. */
	public String toString() {
		return this.getClass().getName() + "@" + directory;
	}

	protected static class FSIndexInput extends BufferedIndexInput {

		protected static class Descriptor extends RandomAccessFile {
			// remember if the file is open, so that we don't try to close it
			// more than once
			protected volatile boolean isOpen;
			long position;
			final long length;

			public Descriptor(File file, String mode) throws IOException {
				super(file, mode);
				isOpen = true;
				length = length();
			}

			public void close() throws IOException {
				if (isOpen) {
					isOpen = false;
					super.close();
				}
			}

			protected void finalize() throws Throwable {
				try {
					close();
				} finally {
					super.finalize();
				}
			}
		}

		protected final Descriptor file;
		boolean isClone;

		public FSIndexInput(File path) throws IOException {
			this(path, BufferedIndexInput.BUFFER_SIZE);
		}

		public FSIndexInput(File path, int bufferSize) throws IOException {
			super(bufferSize);
			file = new Descriptor(path, "r");
		}

		/** IndexInput methods */
		protected void readInternal(byte[] b, int offset, int len)
				throws IOException {
			synchronized (file) {
				long position = getFilePointer();
				if (position != file.position) {
					file.seek(position);
					file.position = position;
				}
				int total = 0;
				do {
					int i = file.read(b, offset + total, len - total);
					if (i == -1)
						throw new IOException("read past EOF");
					file.position += i;
					total += i;
				} while (total < len);
			}
		}

		public void close() throws IOException {
			// only close the file if this is not a clone
			if (!isClone)
				file.close();
		}

		protected void seekInternal(long position) {
		}

		public long length() {
			return file.length;
		}

		public Object clone() {
			FSIndexInput clone = (FSIndexInput) super.clone();
			clone.isClone = true;
			return clone;
		}

		/**
		 * Method used for testing. Returns true if the underlying file
		 * descriptor is valid.
		 */
		boolean isFDValid() throws IOException {
			return file.getFD().valid();
		}
	}

	protected static class FSIndexOutput extends BufferedIndexOutput {
		RandomAccessFile file = null;

		// remember if the file is open, so that we don't try to close it
		// more than once
		private volatile boolean isOpen;

		public FSIndexOutput(File path) throws IOException {
			file = new RandomAccessFile(path, "rw");
			isOpen = true;
		}

		/** output methods: */
		public void flushBuffer(byte[] b, int offset, int size)
				throws IOException {
			file.write(b, offset, size);
		}

		public void close() throws IOException {
			// only close the file if it has not been closed yet
			if (isOpen) {
				boolean success = false;
				try {
					super.close();
					success = true;
				} finally {
					isOpen = false;
					if (!success) {
						try {
							file.close();
						} catch (Throwable t) {
							// Suppress so we don't mask original exception
						}
					} else
						file.close();
				}
			}
		}

		/** Random-access methods */
		public void seek(long pos) throws IOException {
			super.seek(pos);
			file.seek(pos);
		}

		public long length() throws IOException {
			return file.length();
		}

		public void setLength(long length) throws IOException {
			file.setLength(length);
		}
	}
}
