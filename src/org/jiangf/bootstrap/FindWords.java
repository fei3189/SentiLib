package org.jiangf.bootstrap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class FindWords {
	public static void main(String []args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("/home/jiangfei/deep_nlp/code/word2vec/posneg1.txt"));
		Map<String, double[]> map = new HashMap<String, double[]>();
		for (String line; (line = reader.readLine()) != null; ) {
//			System.out.println(line);
			String []tokens = line.split("\\t");
			if (tokens.length != 2)
				continue;
			for (String word : tokens[0].split("\\s+")) {
				if (word.isEmpty())
					continue;
				double[] count = map.get(word);
				if (count == null) {
					count = new double[3];
					count[0] = count[1] = count[2] = 0;
				}
				if (tokens[1].equals("1"))
					count[1]++;
				else if (tokens[1].equals("-1"))
					count[2]++;
				count[0]++;
				map.put(word, count);
//				System.out.println(word);
			}
		}
		Map<String, double[]> filtered = new HashMap<String, double[]>();
		for (String word : map.keySet()) {
			double[] count = map.get(word);
			if (count[0] > 50)
				filtered.put(word, count);
		}
		for (String word : filtered.keySet()) {
			double[] count = map.get(word);
			double sum = count[0];
			count[0] = count[1] / sum;
			if (count[0] < 0.5)
				count[0] = 1 - count[0];
		}
		ArrayList<Entry<String, double[]>> words = new ArrayList<Entry<String, double[]>>(filtered.entrySet());
		Collections.sort(words, new Comparator<Entry<String, double[]>>() {

			@Override
			public int compare(Entry<String, double[]> o1,
					Entry<String, double[]> o2) {
				if (o1.getValue()[0] < o2.getValue()[0]) {
					return 1;
				} else if (o1.getValue()[0] > o2.getValue()[0]) {
					return -1;
				}
				return 0;
			}
		});
		System.out.println(words.size());
		for (int i = 0; i < 100 && i < words.size(); ++i) {
			System.out.println(words.get(i).getKey() + "\t" + words.get(i).getValue()[0]);
		}
		reader.close();
	}
}