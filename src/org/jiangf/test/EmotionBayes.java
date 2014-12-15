package org.jiangf.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.jiangf.esm.EmotionESM;
import org.jiangf.featurepool.EmoticonSpaceFeatures;
import org.jiangf.segmentation.ICTSegmentation;
import org.jiangf.segmentation.Segmentation;
import org.jiangf.segmentation.SegmentationException;

public class EmotionBayes {
	private static Map<String, Integer> desc2id = new HashMap<String, Integer>();
	private static Map<Integer, String> id2desc = new HashMap<Integer, String>();
	private static ArrayList<Map<String, Double>> label2words = new ArrayList<Map<String, Double>>();
	static {
		desc2id.put("none", 0);
		desc2id.put("happiness", 1);
		desc2id.put("sadness", 2);
		desc2id.put("anger", 3);
		desc2id.put("like", 4);
		desc2id.put("surprise", 5);
		desc2id.put("disgust", 6);
		desc2id.put("fear", 7);
		for (String key : desc2id.keySet())
			id2desc.put(desc2id.get(key), key);
		for (int i = 0; i <= 7; ++i)
			label2words.add(new TreeMap<String, Double>());
	}
	
	static public ArrayList<Entry<String, Integer>> loadData(String filename) throws NumberFormatException, UnsupportedEncodingException, IOException, SegmentationException {
		Segmentation seg = new ICTSegmentation("resources/", 1);
		ArrayList<Entry<String, Integer>> labeledData = new ArrayList<Entry<String, Integer>>();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		for (String line; (line = reader.readLine()) != null; ) {
			line = line.trim().toLowerCase();
			if (!line.isEmpty()) {
				String []tokens = line.split("\\t");
				if (tokens.length == 2) {
					labeledData.add(new AbstractMap.SimpleEntry<String, Integer>(seg.segment(tokens[0]), desc2id.get(tokens[1])));
				} else {
					assert false;
				}
			}
		}
		reader.close();
		return labeledData;
	}
	
	static public void train(ArrayList<Entry<String, Integer>> data) {
		Map<String, Double> words2total = new HashMap<String, Double>();
		int seq = 0;
		for (Entry<String, Integer> e : data) {
			String weibo = e.getKey();
			Integer label = e.getValue();
			Map<String, Double> words = label2words.get(label);
			for (String w : weibo.split("\\s+")) {
				Double count = words.get(w);
				if (count == null)
					count = 0.0;
				count += 1;
				words.put(w, count);
				
				count = words2total.get(w);
				if (count == null)
					count = 0.0;
				count += 1;
				words2total.put(w, count);
			}
			System.out.println(++seq);
		}
		int SMOOTH = 1;
		for (int i = 1; i <= 7; ++i) {
			Map<String, Double> words = label2words.get(i);
			for (String w : words2total.keySet()) {
				Double total = words2total.get(w);
				Double count = words.get(w);
				if (count == null)
					count = 0.0;
				words.put(w, (SMOOTH + count) / (7 * SMOOTH + total));
			}
			System.out.println(i);
		}
	}
	
	static public void save(String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		Set<String> wset = label2words.get(1).keySet();
		for (String w : wset) {
			writer.write(w);
			for (int i = 1; i <= 7; ++i) {
				writer.write("\t" + label2words.get(i).get(w).doubleValue());
			}
			writer.newLine();
		}
		writer.close();
	}
	
	public static void main(String[] args) throws SegmentationException, IOException {
		ArrayList<Entry<String, Integer>> train = loadData("/home/jiangfei/labeled_data/emotion.train");
		train(train);
		save("resources/emotion.dict");
	}
}