package org.jiangf.segmentation;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.SimpleSeg;
import com.chenlb.mmseg4j.Word;

public class MMSegmentation extends Segmentation {
	Dictionary dic = Dictionary.getInstance();
	SimpleSeg seg = new SimpleSeg(dic);
	String emoticon = "\\[[^\\]\\s]{1,10}\\]";
	static boolean globalInited = false;
	/**
	 * 
	 * @param dataPath: Init data directory, whick consists of configure files, pos files, map files, etc.
	 * @param encoding: Init file data format, GBK=0, UTF8=1, BIG5=2, GBK_FANTI=3
	 * @throws SegmentationException
	 */
	public MMSegmentation() throws SegmentationException {
	}


	@Override
	public String segment(String str, String encoding, int posTagged) throws UnsupportedEncodingException {
		str = str.replaceAll(emoticon, " $0 ");
		StringBuilder output = new StringBuilder();
		for (String piece : str.split("\\s")) {
			if (Pattern.matches(emoticon, piece))
				output.append(piece).append(" ");
			else {
				MMSeg mmSeg = new MMSeg(new StringReader(piece), seg);
				Word word = null;
				try {
					while ((word = mmSeg.next()) != null) {
						String w = word.getString();
						output.append(w).append(" ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return output.toString().trim().replaceAll("\\s+", " ");
	}

	@Override
	public String segment(String str) throws UnsupportedEncodingException {
		return segment(str, "UTF-8", 0);
	}

	@Override
	public int importUserDict(String path) {
		return 0;
	}
	
	@Override
	public void addUserWord(String word) throws UnsupportedEncodingException {
	}
}