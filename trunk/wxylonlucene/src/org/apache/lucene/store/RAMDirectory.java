package org.apache.lucene.store;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * �����������ǣ�һ������Directory���ڴ�ʵ�ֵ��� ����������SingleInstanceLockFactory��ʵ�֣��������������Ը���
 * @version ����ʱ�䣺2009-9-25 ����11:43:32
 */
public class RAMDirectory extends Directory implements Serializable {

	private static final long serialVersionUID = 1l;
	// ���ȶ��˱����ļ����Լ���Map
	HashMap fileMap = new HashMap();
	//�ļ�ռ�õ��ֽ���   
	long sizeInBytes = 0;
	
	/**
	 * ���캯����ָ��LockFactoryΪ�����һ��ʵ��SingleInstanceLockFactory��
	 * SingleInstanceLockFactory����ص��ǣ�
	 * ���еļ�����������ͨ����SingleInstanceLockFactory��һ��ʵ����������
	 * Ҳ����˵���ڽ��м���������ʱ�򣬱����ȡ�����SingleInstanceLockFactory��ʵ��
	 * ����һ���µ�ʵ�� RAMDirectory
	 */
	public RAMDirectory() {
		setLockFactory(new SingleInstanceLockFactory());
	}

	/**
	 * ����һ���µ�RAMDirectory������ָ����Ŀ¼���������Ŀ�Ŀ��Խ�һ���ļ�ϵͳ���������ص��ڴ��У�
	 * ���ֻ��ʹ���ڸ������ܹ���ȫ�����ڵ��ڴ��С����ֻ�ǽ�ϵͳ�ļ�����������һ�ݵ��ڴ��У�
	 * ���غ�ϵͳ�ļ����������仯���������ڸ�ʵ�������֡�
	 * ����һ���µ�ʵ�� RAMDirectory
	 * @param dir
	 * @throws IOException
	 */
	public RAMDirectory(Directory dir) throws IOException {
		this(dir, false);
	}
	
	/**
	 * ����Ŀ¼���Լ���������Ŀ¼�е������ļ����Ƶ��ڴ��У���Ҫ���Ƶ��ļ��Ѿ����ڣ���ֱ�Ӹ��ǡ�
	 * closeDir��ʾ�Ƿ�ر�ԴĿ¼
	 * ����һ���µ�ʵ�� RAMDirectory
	 * @param dir
	 * @param closeDir
	 * @throws IOException
	 */
	private RAMDirectory(Directory dir, boolean closeDir) throws IOException {
		this();
		Directory.copy(dir, this, closeDir);
	}

	/**
	 * �����ṩ���ļ�·���������ļ�·���µ����е��ļ����Ƶ��ڴ��У����Ҵ���һ��RAMDirectory����
	 * �����Ҫ���ƵĶ����Ѿ����ڣ��򸲸�ԭ�����ļ�
	 * ����һ���µ�ʵ�� RAMDirectory
	 * @param dir
	 * @throws IOException
	 */
	public RAMDirectory(File dir) throws IOException {
		this(FSDirectory.getDirectory(dir), true);
	}

	/**
	 * �����ṩ��String·���������ļ�·���µ����е��ļ����Ƶ��ڴ��У����Ҵ���һ��RAMDirectory����
	 * �����Ҫ���ƵĶ����Ѿ����ڣ��򸲸�ԭ�����ļ�
	 * ����һ���µ�ʵ�� RAMDirectory
	 * @param dir
	 * @throws IOException
	 */
	public RAMDirectory(String dir) throws IOException {
		this(FSDirectory.getDirectory(dir), true);
	}

	/**�������и��ļ����ڴ������е������ļ�  */  
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

	/**���ָ���ļ������ļ��������д��ڣ��򷵻�true�����򷵻�false*/  
	public final boolean fileExists(String name) {
		ensureOpen();
		RAMFile file;
		synchronized (this) {
			file = (RAMFile) fileMap.get(name);
		}
		return file != null;
	}

	/**����ָ���ļ������޸�ʱ��   */  
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

	/**��ָ���ļ�������޸�ʱ������Ϊ���� */  
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

	/** ����ָ���ļ��Ĵ�С*/  
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

	/** ���ص�ǰĿ¼�����е��ļ���С*/  
	public synchronized final long sizeInBytes() {
		ensureOpen();
		return sizeInBytes;
	}

	/** ɾ��ָ���ļ������ļ�*/  
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

	/**������ */  
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

	 /**�����ƶ������ƣ�����һ�����ļ������ҷ��ز������ļ���������� */  
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

	/** ����ָ�����ļ�������һ���������ļ��������� */
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

	/** �رղ����ͷ�����ռ�õ��ڴ� */  
	public void close() {
		isOpen = false;
		fileMap = null;
	}
}
