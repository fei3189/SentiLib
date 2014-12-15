package org.jiangf.esm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import org.jiangf.featurepool.EmoticonSpaceFeatures;
import org.jiangf.ml.SVMTool;

/**
 * Todo: bug:regular expression, Original emoticon
 * 
 * SentimentAnalysis for single weibo message
 * @author jiangfei
 *
 */
public class SubjectivityESM extends ESM {
	public EmoticonSpaceFeatures esm;
	public SVMTool model;
	
	// text1, text should be segmented
	public SubjectivityESM(EmoticonSpaceFeatures c) throws IOException {
		esm = c;
		model = new SVMTool();
	}
	
	public SubjectivityESM(EmoticonSpaceFeatures c, SVMTool m) throws IOException {
		esm = c;
		model = m;
	}
	
	public void loadModel(Reader inputReader) throws IOException {
		BufferedReader reader = null;
		if (inputReader instanceof BufferedReader) {
			reader = (BufferedReader)inputReader;
		} else 
			reader = new BufferedReader(inputReader);
		model = new SVMTool();
		model.load(reader);
	}
	
	public void train(ArrayList<Entry<String, Integer>> dataSet, double C, double gamma) {
		ArrayList<String> weiboList = new ArrayList<String>();
		ArrayList<Integer> labelList = new ArrayList<Integer>();
		for (Entry<String, Integer> e : dataSet) {
			weiboList.add(e.getKey());
			labelList.add(e.getValue());
		}
		ArrayList<double[]> features = esm.extractFeaturesAll(weiboList);
		model.train(features, labelList, C, gamma);
	}
	
	public void train(ArrayList<Entry<String, Integer>> dataSet) {
		train(dataSet, 10, 0.005);
	}
	
	public Map<Integer, Double> predict(String weibo) {
		double[] features = esm.extractFeatures(weibo);
		return model.predictProbability(features);
	}
	
	public ArrayList<Map<Integer, Double>> predict(ArrayList<String> weiboList) {
		ArrayList<Map<Integer, Double>> ret = new ArrayList<Map<Integer, Double>>();
		for (String weibo : weiboList) {
			ret.add(predict(weibo));
		}
		return ret;
	}
	
	public int classify(String weibo) {
		return model.predict(esm.extractFeatures(weibo));
	}
	
	public ArrayList<Integer> classify(ArrayList<String> weiboList) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (String weibo : weiboList)
			ret.add(classify(weibo));
		return ret;
	}
	
	public void saveModel(String filename) throws IOException {
		model.save(filename);
	}
}
