package org.jiangf.sentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.jiangf.esm.EmotionESM;
import org.jiangf.featurepool.EmoticonSpaceFeatures;
import org.jiangf.featurepool.EmotionProb;
import org.jiangf.ml.SVMTool;

/**
 * Todo: bug:regular expression, Original emoticon
 * 
 * SentimentAnalysis for single weibo message
 * @author jiangfei
 *
 */
public class Emotion {
	EmotionESM esm;
	EmotionProb prob;
	
	public Emotion(EmoticonSpaceFeatures e, SVMTool model, EmotionProb p) throws IOException {
		esm = new EmotionESM(e, model);
		prob = p;
	}
	
	public Map<Integer, Double> predict(String weibo) {
		Map<Integer, Double> ret = esm.predict(weibo);
		double[] ps = prob.extractFeatures(weibo);
		for (Integer cls : ret.keySet()) {
			// Note, there is hyper parameters here.
			ret.put(cls, 0.9 * ret.get(cls) + 0.1 * ps[cls-1]);
		}
		return ret;
	}
	
	public ArrayList<Map<Integer, Double>> predict(ArrayList<String> weiboList) {
		ArrayList<Map<Integer, Double>> ret = new ArrayList<Map<Integer, Double>>();
		for (String weibo : weiboList) {
			ret.add(predict(weibo));
		}
		return ret;
	}
	
	public int classify(String weibo) {
		Map<Integer, Double> prob = predict(weibo);
		int label = -1;
		double max = -2;
		for (Integer cls : prob.keySet()) {
			double p = prob.get(cls);
			if (p > max) {
				max = p;
				label = cls;
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
