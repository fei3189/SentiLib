package org.jiangf.sentiment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.jiangf.featurepool.EmoticonSpaceFeatures;
import org.jiangf.featurepool.EmotionProb;
import org.jiangf.featurepool.PolarityProb;
import org.jiangf.featurepool.SubjectivityProb;
import org.jiangf.ml.SVMTool;
import org.jiangf.segmentation.ICTSegmentation;
import org.jiangf.segmentation.Segmentation;
import org.jiangf.segmentation.SegmentationException;

public class Analysis {
	public enum AnalysisType { ANALYSIS_ALL_TYPES, ANALYSIS_THREE_TYPES, ANALYSIS_EIGHT_TYPES }
	
	private EmoticonSpaceFeatures senti;

	private Emotion emotion;
	private Polarity polarity;
	private Subjectivity subjectivity;
	
	Segmentation seg;
	
	private final String[] emotionMap = {
			"neutral", "happiness", "sadness", "anger", "like", "surprise", "disgust", "fear"
			};
	
	public Analysis(Segmentation s) throws SegmentationException, IOException {
		seg = s;
		init();
	}
	
	public Analysis(String segLib) throws IOException, SegmentationException {
		seg = new ICTSegmentation(segLib, 1);
		init();
	}
	
	private void init() throws IOException {
		DataInputStream dis = new DataInputStream(new GZIPInputStream(this.getClass().getClassLoader().getResourceAsStream("resources/sentimentanalysis.dat")));
		int numFiles = dis.readInt();
		ByteArrayOutputStream[] bos = new ByteArrayOutputStream[numFiles];
		int BUFFER_SIZE = 10000;
		for (int i = 0; i < numFiles; ++i) {
			bos[i] = new ByteArrayOutputStream();
		}
		for (int i = 0; i < numFiles; ++i) {
			long size = dis.readLong();
			byte[] arr = new byte[BUFFER_SIZE];
			int left = (int)size;
			
			while (left > 0) {
				int batch = left > BUFFER_SIZE ? BUFFER_SIZE : left;
				int r = dis.read(arr, 0, batch);
				for (int j = 0; j < arr.length; ++j)
					arr[j] ^= 0xFC;
				bos[i].write(arr, 0, r);
				left -= r;
			}
			bos[i].close();
		}
		dis.close();

		senti = new EmoticonSpaceFeatures(new InputStreamReader(new ByteArrayInputStream(bos[6].toByteArray())));
		
		SVMTool polaritySVM = new SVMTool();
		SVMTool subjectivitySVM = new SVMTool();
		SVMTool emotionSVM = new SVMTool();
		polaritySVM.load(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bos[3].toByteArray()))));
		subjectivitySVM.load(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bos[4].toByteArray()))));
		emotionSVM.load(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bos[5].toByteArray()))));
		
		PolarityProb polarityProb = new PolarityProb(new InputStreamReader(new ByteArrayInputStream(bos[0].toByteArray())));
		SubjectivityProb subjectivityProb = new SubjectivityProb(new InputStreamReader(new ByteArrayInputStream(bos[1].toByteArray())));
		EmotionProb emotionProb = new EmotionProb(new InputStreamReader(new ByteArrayInputStream(bos[2].toByteArray())));
		
		subjectivity = new Subjectivity(senti, subjectivitySVM, subjectivityProb);
		polarity = new Polarity(senti, polaritySVM, polarityProb);
		emotion = new Emotion(senti, emotionSVM, emotionProb);
	}
	
	/**
	 * 
	 * @param weibo
	 * @return
	 */
	private Map<String, Double> emotionAnalysis(String weibo) {
		Map<Integer, Double> subjRes = subjectivity.predict(weibo);
		Map<Integer, Double> emoRes = emotion.predict(weibo);
		Map<String, Double> res = new TreeMap<String, Double>();
		double s = subjRes.get(1);
		
		/*
		 * Emotion Map:
		 * 		happiness : 1
		 * 		sadness  : 2
		 * 		anger 	: 3
		 * 		like	: 4
		 * 		surprise : 5
		 * 		disgust : 6
		 * 		fear : 7
		 */
		res.put("neutral", subjRes.get(0));
		res.put("happiness", emoRes.get(1) * s);
		res.put("sadness", emoRes.get(2) * s);
		res.put("anger", emoRes.get(3) * s);
		res.put("like", emoRes.get(4) * s);
		res.put("surprise", emoRes.get(5) * s);
		res.put("disgust", emoRes.get(6) * s);
		res.put("fear", emoRes.get(7) * s);
		
		return res;
	}
	
	private String emotionClassify(String weibo) {
		int result = subjectivity.classify(weibo);
		if (result == 1) {
			result = emotion.classify(weibo);
		}
		return emotionMap[result];
	}
	
	private String sentimentClassify(String weibo) {
		int result = subjectivity.classify(weibo);
		if (result == 0) {
			return "neutral";
		}
		result = polarity.classify(weibo);
		if (result == 1) {
			return "positive";
		} else {
			return "negative";
		}
	}
	
	
	private Map<String, Double> sentimentAnalysis(String weibo) {
		Map<Integer, Double> subjRes = subjectivity.predict(weibo);
		Map<Integer, Double> polarRes = polarity.predict(weibo);
		Map<String, Double> res = new TreeMap<String, Double>();
		double subjScore = subjRes.get(0);
		double positiveScore = subjRes.get(1) * polarRes.get(1);
		double negativeScore = subjRes.get(1) * polarRes.get(-1);
		res.put("positive", positiveScore);
		res.put("negative", negativeScore);
		res.put("neutral", subjScore);
		res.put("sentiment", positiveScore - negativeScore);
		return res;
	}
	
	private Map<String, Double> sentimentEmotionAnalysis(String weibo) {
		Map<Integer, Double> subjRes = subjectivity.predict(weibo);
		Map<Integer, Double> polarRes = polarity.predict(weibo);
		Map<String, Double> res = new TreeMap<String, Double>();
		double subjScore = subjRes.get(0);
		double objScore = subjRes.get(1);
		double positiveScore = objScore * polarRes.get(1);
		double negativeScore = objScore * polarRes.get(-1);
		res.put("positive", positiveScore);
		res.put("negative", negativeScore);
		res.put("neutral", subjScore);
		res.put("sentiment", positiveScore - negativeScore);
		
		/*
		 * Emotion Map:
		 * 		happiness : 1
		 * 		sadness  : 2
		 * 		anger 	: 3
		 * 		like	: 4
		 * 		surprise : 5
		 * 		disgust : 6
		 * 		fear : 7
		 */
		Map<Integer, Double> emoRes = emotion.predict(weibo);
		res.put("happiness", emoRes.get(1) * objScore);
		res.put("sadness", emoRes.get(2) * objScore);
		res.put("anger", emoRes.get(3) * objScore);
		res.put("like", emoRes.get(4) * objScore);
		res.put("surprise", emoRes.get(5) * objScore);
		res.put("disgust", emoRes.get(6) * objScore);
		res.put("fear", emoRes.get(7) * objScore);
		
		return res;
	}
	
	public Map<String, Double> analysis(String weibo, AnalysisType type) throws UnsupportedEncodingException {
		weibo = seg.segment(weibo);
		if (type == AnalysisType.ANALYSIS_THREE_TYPES)
			return sentimentAnalysis(weibo);
		else if (type == AnalysisType.ANALYSIS_EIGHT_TYPES)
			return emotionAnalysis(weibo);
		else
			return sentimentEmotionAnalysis(weibo);
	}
	
	public Map<String, Double> analysis(String weibo) throws UnsupportedEncodingException {
		return analysis(weibo, AnalysisType.ANALYSIS_THREE_TYPES);
	}
	
	public String classify(String weibo, AnalysisType type) throws UnsupportedEncodingException {
		weibo = seg.segment(weibo);
		if (type == AnalysisType.ANALYSIS_THREE_TYPES)
			return sentimentClassify(weibo);
		else
			return emotionClassify(weibo);
	}
	
	public String classify(String weibo) throws UnsupportedEncodingException {
		return classify(weibo, AnalysisType.ANALYSIS_THREE_TYPES);
	}
	
	public double[] esm(String weibo) throws UnsupportedEncodingException {
		return senti.esm(seg.segment(weibo));
	}
}