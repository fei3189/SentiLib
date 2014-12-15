package org.jiangf.sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

import org.jiangf.featurepool.EmoticonSpaceFeatures;
import org.jiangf.featurepool.EmotionProb;
import org.jiangf.featurepool.PolarityProb;
import org.jiangf.featurepool.SubjectivityProb;
import org.jiangf.ml.SVMTool;
import org.jiangf.segmentation.ICTSegmentation;
import org.jiangf.segmentation.Segmentation;
import org.jiangf.segmentation.SegmentationException;

public class Analysis1 {
	public enum AnalysisType { ANALYSIS_ALL_TYPES, ANALYSIS_THREE_TYPES, ANALYSIS_EIGHT_TYPES }
	
	private EmoticonSpaceFeatures subj;
	private EmoticonSpaceFeatures senti;

	private Emotion emotion;
	private Polarity polarity;
	private Subjectivity subjectivity;
	
	Segmentation seg;
	
	public Analysis1() throws IOException, SegmentationException {
		seg = new ICTSegmentation("resources/", 1);
		
		senti = new EmoticonSpaceFeatures(new FileReader("resources/word2dis.subj"));
		subj = new EmoticonSpaceFeatures(new FileReader("resources/word2dis.subj"));
		
		SVMTool polaritySVM = new SVMTool();
		SVMTool subjectivitySVM = new SVMTool();
		SVMTool emotionSVM = new SVMTool();
		polaritySVM.load(new BufferedReader(new FileReader("resources/polarity.model")));
//		subjectivitySVM.load(new BufferedReader(new FileReader("resources/subjectivity_s.model")));
//		emotionSVM.load(new BufferedReader(new FileReader("resources/emotion_ss.model")));
		subjectivitySVM.load(new BufferedReader(new FileReader("resources/subjectivity.model")));
		emotionSVM.load(new BufferedReader(new FileReader("resources/emotion.model")));
		
		SubjectivityProb subjectivityProb = new SubjectivityProb(new FileReader("resources/subjectivity.dict"));
		PolarityProb polarityProb = new PolarityProb(new FileReader("resources/polarity.dict"));
		EmotionProb emotionProb = new EmotionProb(new FileReader("resources/emotion.dict"));
		
		subjectivity = new Subjectivity(subj, subjectivitySVM, subjectivityProb);
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
}