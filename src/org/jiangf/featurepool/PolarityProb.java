package org.jiangf.featurepool;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jiangf.tools.DictReader;

public class PolarityProb implements Features {
	Map<String, double[]> polarity = new HashMap<String, double[]>();
	
	public PolarityProb(Reader inputReader) throws IOException {
		DictReader reader = new DictReader();
		polarity = reader.readDict(inputReader);
	}
	
	@Override
	public double[] extractFeatures(String weibo) {
		double[] features = new double[2]; //Positive, negative, non-neutral, neutral
		for (int i = 0; i < 2; ++i)
			features[i] = 0.5;
		for (String word : weibo.split("\\s")) {
			double[] score = polarity.get(word);
			if (score != null) {
				features[0] *= score[0];
				features[1] *= score[1];
				double sum = features[0] + features[1];
				features[0] /= sum;
				features[1] /= sum;
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
	
}