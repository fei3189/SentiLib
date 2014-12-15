package org.jiangf.encode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * confuse three models (polar, senti, triclass) and the posneg dict.
 * 
 * @author caq
 * 
 */
public class Encode {
	static byte[] randomBytes(int size) {
		Random r = new Random();
		byte[] bytes = new byte[size];
		r.nextBytes(bytes);
		return bytes;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException,
			FileNotFoundException, IOException {

		String []files = new String[] {
			"resources/polarity.dict",
			"resources/subjectivity.dict",
			"resources/emotion.dict",
			"resources/polarity.model",
			"resources/subjectivity.model",
			"resources/emotion.model",
			"resources/word2dis.txt",
		};
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(new GZIPOutputStream(bos));
		
		writer.writeInt(files.length);
		for (String fn : files) {
			File f = new File(fn);
			writer.writeLong(f.length());
			FileInputStream fis = new FileInputStream(f);
			byte[] buf = new byte[10000];
			int numRead = -1;
			while ((numRead = fis.read(buf)) != -1) {
				for (int i = 0; i < buf.length; ++i)
					buf[i] ^= 0xFC;
				writer.write(buf, 0, numRead);
			}
			fis.close();
		}
		writer.close();
		
		FileOutputStream ff = new FileOutputStream("resources/sentimentanalysis.dat");
		ff.write(bos.toByteArray());
		ff.close();
		System.exit(-1);
		DataInputStream dis = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(bos.toByteArray())));
		int numFiles = dis.readInt();
		for (int i = 0; i < numFiles; ++i) {
			long size = dis.readLong();
			byte[] arr = new byte[10000];
			int left = (int)size;
			
			FileOutputStream fos = new FileOutputStream(new Integer(i).toString());
			while (left > 0) {
				int batch = left > 10000 ? 10000 : left;
				int r = dis.read(arr, 0, batch);
				for (int j = 0; j < arr.length; ++j)
					arr[j] ^= 0xFC;
				fos.write(arr, 0, r);
				left -= r;
			}
			fos.close();
		}
	}

}
