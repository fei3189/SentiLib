package org.jiangf.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jiangf.esm.EmotionESM;
import org.jiangf.esm.SubjectivityESM;
import org.jiangf.featurepool.EmoticonSpaceFeatures;
import org.jiangf.featurepool.EmotionProb;
import org.jiangf.featurepool.SubjectivityProb;
import org.jiangf.segmentation.ICTSegmentation;
import org.jiangf.segmentation.Segmentation;
import org.jiangf.segmentation.SegmentationException;
import org.jiangf.sentiment.Emotion;
import org.jiangf.sentiment.Subjectivity;

public class TestEmotion {
	private static Map<String, Integer> desc2id = new HashMap<String, Integer>();
	private static Map<Integer, String> id2desc = new HashMap<Integer, String>();
	
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
	
	public static void main(String[] args) throws SegmentationException, IOException {
//		ArrayList<Entry<String, Integer>> train = loadData("/home/jiangfei/labeled_data/emotion.train.all");
		ArrayList<Entry<String, Integer>> train = loadData("/home/jiangfei/NLPCC2014/newdata/emo.txt");
		ArrayList<Entry<String, Integer>> test = loadData("/home/jiangfei/labeled_data/emotion.test");

		EmoticonSpaceFeatures esm = new EmoticonSpaceFeatures(new FileReader("resources/word2dis.subj"));

		EmotionESM sa = new EmotionESM(esm);
		sa.train(train);
		sa.saveModel("resources/emotion_ss.model");
//		sa.loadModel(new FileReader("resources/emotion.model"));
		
//		EmotionProb prob = new EmotionProb(new FileReader("resources/emotion.dict"));
//		Emotion emotion = new Emotion(esm, sa.model, prob);
		
		int count = 0;
		for (Entry<String, Integer> e : test) {
			System.out.println(sa.classify(e.getKey()) + " " + e.getValue());
			if (sa.classify(e.getKey()) == e.getValue())
				++count;
		}
		System.out.println("accuracy = " + 1.0 * count / test.size());
	}
}