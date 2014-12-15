package org.jiangf.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jiangf.segmentation.ICTSegmentation;
import org.jiangf.segmentation.Segmentation;
import org.jiangf.segmentation.SegmentationException;
import org.jiangf.sentiment.Analysis1;
import org.jiangf.sentiment.Analysis1.AnalysisType;


public class NLPCC2014Doc1 {
	private static Map<String, Integer> desc2id = new HashMap<String, Integer>();
	private static Map<Integer, String> id2desc = new HashMap<Integer, String>();
	private static Map<String, Map<String, Double>> cp = new HashMap<String, Map<String, Double>>();
	
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
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/home/jiangfei/NLPCC2014/graph.txt"));
			for (String line; (line = reader.readLine()) != null; ) {
				String[] tokens = line.split("\\t");
				Map<String, Double> m = cp.get(tokens[0]);
				if (m == null)
					m = new HashMap<String, Double>();
				if (tokens[1].equals("none"))
					tokens[1] = "neutral";
				Double d = m.get(tokens[1]);
				assert d == null;
				if (tokens[1].equals("neutral"))
					m.put(tokens[1], Double.parseDouble(tokens[2]) * 1.1);
				else
					m.put(tokens[1], Double.parseDouble(tokens[2]));
				cp.put(tokens[0], m);
			}
			System.out.println(cp);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static ArrayList<String> readWeibo(String fileName) throws IOException, SegmentationException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		ArrayList<String> ret = new ArrayList<String>();
		String content = "";
		for (String line; (line = reader.readLine()) != null; ) {
			line = line.trim();
			String[] tokens = line.split("\t");
			if (tokens.length == 1) {
				ret.add(content);
				content = "";
			} else if (tokens.length == 4) {
				content = "";
			} else if (tokens.length == 5) {
				content += tokens[4];
			} else if (tokens.length == 7) {
				content += tokens[6];
			}
		}
		reader.close();
		return ret;
	}
	
	public static void main(String args[]) throws IOException, SegmentationException {
		ArrayList<String> weiboList = readWeibo("/home/jiangfei/NLPCC2014/test.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/jiangfei/NLPCC2014/THUIR_EMOTION_ANALYSIS_1_2.txt"));
		Analysis1 analysis = new Analysis1();
		for (int i = 0; i < weiboList.size(); ++i) {
			Map<String, Double> map = analysis.analysis(weiboList.get(i), AnalysisType.ANALYSIS_EIGHT_TYPES);
		//	System.out.println(map);
			String first = null, second = null;
			double firstScore = 0, secondScore = 0;
			for (String key : map.keySet()) {
				if (key.equals("neutral"))
					continue;
				if (map.get(key) > firstScore) {
					secondScore = firstScore;
					firstScore = map.get(key);
					second = first;
					first = key;
				} else if (map.get(key) > secondScore) {
					secondScore = map.get(key);
					second = key;
				}
			}
			String line = "1\tTHUIR\t1\tO\t" + (i + 1) + "\tY\t" + first + "\t" + second;
			writer.write(line);
			writer.newLine();
		}
		
		writer.close();
	}
}