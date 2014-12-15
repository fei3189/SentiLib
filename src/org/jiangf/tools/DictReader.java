package org.jiangf.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class DictReader {
	int length = -1; // Length of scores, typically number of fields - 1, for
                     // the first field is word.
	public Map<String, double[]> readDict(Reader inputReader) throws IOException {
		Map<String, double[]> dict = new HashMap<String, double[]>();
		
		BufferedReader reader = null;
		if (inputReader instanceof BufferedReader) {
			reader = (BufferedReader) inputReader;
		} else {
			reader = new BufferedReader(inputReader);
		}
		
		length = -1;  
		for (String line; (line = reader.readLine()) != null;) {
			line = line.trim();
			if (line.isEmpty())
				continue;
			String []tokens = line.split("\\s+");
			if (tokens[0].isEmpty())
				continue;
			if (length > 0 && tokens.length != length + 1) {
				reader.close();
				length = -1;
				return null;
			}
			length = tokens.length - 1;
			if (length <= 0) {
				reader.close();
				length = -1;
				return null;
			}
			double[] scores = new double[tokens.length - 1];
			for (int i = 0; i < length; ++i)
				scores[i] = Double.parseDouble(tokens[i  +1]);
			dict.put(tokens[0], scores);
		}
		reader.close();
		return dict;
	}
	
	public int getFieldNum() {
		return length;
	}
}