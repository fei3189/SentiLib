package org.jiangf.bootstrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.jiangf.segmentation.SegmentationException;
import org.jiangf.segmentation.SpaceSegmentation;
import org.jiangf.sentiment.Analysis;

public class Classify {
	public static void main(String [] args) throws IOException, SegmentationException {
		BufferedReader reader = new BufferedReader(new FileReader("/home/jiangfei/deep_nlp/code/word2vec/corpus.large"));
		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/jiangfei/deep_nlp/code/word2vec/posneg.txt"));
		Analysis senti = new Analysis("/home/jiangfei/lib/");
		int count = 0;
		for (String line; (line = reader.readLine()) != null; ) {
			Map<String, Double> map = senti.analysis(line);
			if (map.get("positive") > map.get("negative") && map.get("positive") > map.get("neutral")) {
				writer.write(line + "\t1");
				writer.newLine();
				++count;
			} else if (map.get("negative") > map.get("positive") && map.get("negative") > map.get("neutral")) {
				writer.write(line + "\t-1");
				writer.newLine();
				++count;
			}
			if (count > 1000000)
				break;
			if (count % 1000 == 0)
				System.out.println(count);
		}
		writer.close();
		reader.close();
	}
}