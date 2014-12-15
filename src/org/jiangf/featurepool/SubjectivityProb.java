package org.jiangf.featurepool;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jiangf.tools.DictReader;

public class SubjectivityProb implements Features {
	Map<String, double[]> subjectivity = new HashMap<String, double[]>();
	
	public SubjectivityProb(Reader inputReader) throws IOException {
		DictReader reader = new DictReader();
		subjectivity = reader.readDict(inputReader);
	}
	
	@Override
	public double[] extractFeatures(String weibo) {
		double[] features = new double[2]; // non-neutral, neutral
		for (int i = 0; i < 2; ++i)
			features[i] = 0.5;
		for (String word : weibo.split("\\s")) {
			double[] score = subjectivity.get(word);
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
	public ArrayList<double[]> extractFeaturesAll(ArrayList<String> weiboList)
			throws UnsupportedEncodingException {
		ArrayList<double[]> features = new ArrayList<double[]>();
		for (String weibo : weiboList)
			features.add(extractFeatures(weibo));
		return features;
	}
	
}