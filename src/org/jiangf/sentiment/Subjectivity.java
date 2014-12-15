package org.jiangf.sentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.jiangf.esm.EmotionESM;
import org.jiangf.esm.SubjectivityESM;
import org.jiangf.featurepool.EmoticonSpaceFeatures;
import org.jiangf.featurepool.EmotionProb;
import org.jiangf.featurepool.SubjectivityProb;
import org.jiangf.ml.SVMTool;

/**
 * Todo: bug:regular expression, Original emoticon
 * 
 * SentimentAnalysis for single weibo message
 * @author jiangfei
 *
 */
public class Subjectivity {
	SubjectivityESM esm;
	SubjectivityProb prob;
	
	public Subjectivity(EmoticonSpaceFeatures e, SVMTool model, SubjectivityProb p) throws IOException {
		esm = new SubjectivityESM(e, model);
		prob = p;
	}
	
	public Map<Integer, Double> predict(String weibo) {
		Map<Integer, Double> ret = esm.predict(weibo);
		double[] ps = prob.extractFeatures(weibo);
		for (Integer cls : ret.keySet()) {
			// Note, there is hyper parameters here.
			ret.put(cls, 0.7 * ret.get(cls) + 0.3 * ps[cls]);
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
