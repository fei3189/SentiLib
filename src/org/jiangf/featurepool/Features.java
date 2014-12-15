package org.jiangf.featurepool;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public interface Features {
	/*
	 * Extract features from a single weibo post, the post must be processed by CWS first.
	 */
	public double[] extractFeatures(String weibo)
			throws UnsupportedEncodingException;

	/*
	 * Extract features from multiple weibo posts, the post must be processed by CWS first.
	 */
	public ArrayList<double[]> extractFeaturesAll(ArrayList<String> weiboList)
			throws UnsupportedEncodingException;
}