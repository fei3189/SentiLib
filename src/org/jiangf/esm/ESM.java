package org.jiangf.esm;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ESM {
	public abstract void  loadModel(Reader inputReader) throws IOException;
	
	public void train(ArrayList<Entry<String, Integer>> dataSet, double C) {
		
	}
	
	public void train(ArrayList<Entry<String, Integer>> dataSet, double C, double gamma) {
		
	}
	
	public void train(ArrayList<Entry<String, Integer>> dataSet) {
		
	}
	
	public abstract Map<Integer, Double> predict(String weibo);
	
	public abstract ArrayList<Map<Integer, Double>> predict(ArrayList<String> weiboList);
	
	public abstract int classify(String weibo);
	
	public abstract ArrayList<Integer> classify(ArrayList<String> weiboList);
	
	public abstract void saveModel(String filename) throws IOException;
}