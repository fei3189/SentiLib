package org.jiangf.featurepool;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jiangf.tools.DictReader;

public class EmoticonSpaceFeatures implements Features {
	Map<String, double[]> word2dis = new HashMap<String, double[]>();
	int disDim = 0;
	
	/**
	 * Constructor
	 * @param lines: Each line is a representation of a word.
	 *               Format: word score1 score2 ... scoren 
	 * @throws IOException 
	 */
	public EmoticonSpaceFeatures(Reader inputReader) throws IOException {
		DictReader reader = new DictReader();
		word2dis = reader.readDict(inputReader);
		disDim = reader.getFieldNum();
		assert disDim > 0;
	}

	private double[] analysisDistance(String weibo) {
		String []words = weibo.split("\\s");
		int scale = 1;//6;
		int scale2 = 1;//5;
		double []dis = new double[disDim * 3];
		for (int i = 0; i < disDim; ++i)
			dis[i] = -scale2;
		for (int i = disDim; i < 2 * disDim; ++i)
			dis[i] = scale;
		for (int i = disDim * 2; i < 3 * disDim; ++i)
			dis[i] = 0;
		for (String tokens : words) {
			double[] tmp = word2dis.get(tokens);
			if (tmp != null) {
				for (int i = 0; i < disDim; ++i) {
					if (tmp[i] * scale2 > dis[i])
						dis[i] = tmp[i] * scale2;
					if (tmp[i] * scale < dis[i + disDim])
						dis[i + disDim] = tmp[i] * scale;
					dis[i + 2 * disDim] += tmp[i];
				}
			}
		}
		return dis;
	}
	
	private double[] averagePooling(String weibo) {
		String []words = weibo.split("\\s");
		double []dis = new double[disDim];
		int count = 0;
		for (String tokens : words) {
			double[] tmp = word2dis.get(tokens);
			if (tmp != null) {
				for (int i = 0; i < disDim; ++i) {
					dis[i] += tmp[i];
				}
				++count;
			}
		}
		for (int i = 0; i < disDim; ++i)
			dis[i] /= count;
		return dis;
	}
	
	private ArrayList<double[]> analysisAll(ArrayList<String> weiboList) {
		ArrayList<double[]> featuresAll = new ArrayList<double[]>();
		for (int i = 0; i < weiboList.size(); ++i)
			featuresAll.add(analysis(weiboList.get(i)));
		return featuresAll;
	}
	
	private double[] analysis(String weibo) {
		double [] dis = analysisDistance(weibo);
		return dis;
	}

	public double[] extractFeatures(String weibo) {
		return analysis(weibo);
	}

	public ArrayList<double[]> extractFeaturesAll(ArrayList<String> weiboList) {
		return analysisAll(weiboList);
	}
	
	public double[] esm(String weibo) {
		return averagePooling(weibo);
	}
}
