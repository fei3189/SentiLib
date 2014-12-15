package org.jiangf.test;


import java.io.IOException;

import org.jiangf.segmentation.BigramSegmentation;
import org.jiangf.segmentation.ICTSegmentation;
import org.jiangf.segmentation.MMSegmentation;
import org.jiangf.segmentation.SegmentationException;
import org.jiangf.sentiment.Analysis;


public class TestAnalysis {
	public static void main(String args[]) throws IOException, SegmentationException {
		Analysis analysis = new Analysis(new MMSegmentation());
		System.out.println(analysis.classify("请该群的博友们付出点怜悯之心吧，尽您的微薄之心帮帮这么可怜的孩子吧，我会尽力去能够帮得上他们减轻一点负担是一点，孩子太可怜了，[泪] [泪] [泪] 请大家都来救救这个叫刘依晨的小女孩吧，两个来月时被查出先心病，父母凑了十几万做了手术可刚出院二十来天因肺炎和心脏并发症再次住院 。。。"));
	}
}