package org.jiangf.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.jiangf.esm.ESM;
import org.jiangf.esm.PolarityESM;
import org.jiangf.esm.SubjectivityESM;
import org.jiangf.featurepool.EmoticonSpaceFeatures;
import org.jiangf.featurepool.PolarityProb;
import org.jiangf.segmentation.ICTSegmentation;
import org.jiangf.segmentation.Segmentation;
import org.jiangf.segmentation.SegmentationException;
import org.jiangf.sentiment.Polarity;

public class TestPolar {
	static public ArrayList<Entry<String, Integer>> loadData(String filename) throws NumberFormatException, UnsupportedEncodingException, IOException, SegmentationException {
		Segmentation seg = new ICTSegmentation("resources/", 1);
		ArrayList<Entry<String, Integer>> labeledData = new ArrayList<Entry<String, Integer>>();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		for (String line; (line = reader.readLine()) != null; ) {
			line = line.trim().toLowerCase();
			if (!line.isEmpty()) {
				String []tokens = line.split("\\t");
				if (tokens.length == 2) {
					labeledData.add(new AbstractMap.SimpleEntry<String, Integer>(seg.segment(tokens[0]), new Integer(tokens[1])));
				} else {
					assert false;
				}
			}
		}
		reader.close();
		return labeledData;
	}
	
	public static void main(String[] args) throws SegmentationException, IOException {
		ArrayList<Entry<String, Integer>> train = loadData("/home/jiangfei/labeled_data/polarity_train");
		ArrayList<Entry<String, Integer>> test = loadData("/home/jiangfei/labeled_data/polarity_test");

		EmoticonSpaceFeatures esm = new EmoticonSpaceFeatures(new FileReader("resources/word2dis.senti"));

		
		PolarityESM sa = new PolarityESM(esm);
		sa.train(train);
//		sa.saveModel("resources/polarity.model");
//		System.exit(-1);
//		sa.loadModel(new FileReader("resources/polar.model"));
		
		PolarityProb prob = new PolarityProb(new FileReader("resources/polarity.dict"));
		Polarity polar = new Polarity(esm, sa.model, prob);
		
		int count = 0;
		for (Entry<String, Integer> e : test) {
			if (polar.classify(e.getKey()) == e.getValue())
				++count;
		}
		System.out.println("accuracy = " + 1.0 * count / test.size());
	}
}