package org.jiangf.segmentation;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class UnigramSegmentation extends Segmentation {
	String emoticon = "\\[[^\\]\\s]{1,10}\\]";
	static boolean globalInited = false;

	public UnigramSegmentation() {
		globalInited = true;
	}

	@Override
	public String segment(String str, String encoding, int posTagged)
			throws UnsupportedEncodingException {
		str = str.replaceAll(emoticon, " $0 ");
		String output = "";
		for (String piece : str.split("\\s+")) {
			if (Pattern.matches(emoticon, piece))
				output += piece + " ";
			else {
				for (int i = 0; i < piece.length(); ++i)
					output += piece.charAt(i) + " ";
			}
		}
		// The third argument should be set to result.length-1, for the bug of
		// NLPIR
		output = output.trim();
		output = output.replaceAll("\\s+", " ");
		return output;
	}

	@Override
	public String segment(String str) throws UnsupportedEncodingException {
		return segment(str, "UTF-8", 0);
	}

	@Override
	public int importUserDict(String path) {
		return -1;
	}

	@Override
	public void addUserWord(String word) throws UnsupportedEncodingException {
	}
}