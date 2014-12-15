package org.jiangf.featurepool;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jiangf.tools.DictReader;

public class EmotionProb implements Features {
	Map<String, double[]> emotion = new HashMap<String, double[]>();
	int dim = -1;
	
	public EmotionProb(Reader inputReader) throws IOException {
		DictReader reader = new DictReader();
		emotion = reader.readDict(inputReader);
		dim = reader.getFieldNum();
	}
	
	@Override
	public double[] extractFeatures(String weibo) {
		double[] features = new double[dim]; //Positive, negative, non-neutral, neutral
		for (int i = 0; i < dim; ++i)
			features[i] = 1.0 / dim;
		for (String word : weibo.split("\\s")) {
			double[] score = emotion.get(word);
			if (score != null) {
				double sum = 0;
				for (int i = 0; i < dim; ++i) {
					features[i] *= score[i];
					sum += features[i];
				}
				for (int i = 0; i < dim; ++i) {
					features[i] /= sum;
				}
			}
		}
		return features;
	}

	@Override
	public ArrayList<double[]> extractFeaturesAll(ArrayList<String> weiboList) {
		ArrayList<double[]> features = new ArrayList<double[]>();
		for (String weibo : weiboList)
			features.add(extractFeatures(weibo));
		return features;
	}

	public int classify(String weibo) {
		double[] prob = extractFeatures(weibo);
		int label = -1;
		double max = -2;
		for (int i = 0; i < prob.length; ++i) {
			double p = prob[i];
			if (p > max) {
				max = p;
				label = i + 1;
			}
		}
		return label;
	}
	
	public ArrayList<Integer> classify(ArrayList<String> weiboList) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (String weibo : weiboList)
			ret.add(classify(weibo));
		return ret;
	}
	
}