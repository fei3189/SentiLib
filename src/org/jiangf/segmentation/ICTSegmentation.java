package org.jiangf.segmentation;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import kevin.zhang.*;

public class ICTSegmentation extends Segmentation {
	NLPIR tokenizer;
	String emoticon = "\\[[^\\]\\s]{1,10}\\]";
	static boolean globalInited = false;
	/**
	 * 
	 * @param dataPath: Init data directory, whick consists of configure files, pos files, map files, etc.
	 * @param encoding: Init file data format, GBK=0, UTF8=1, BIG5=2, GBK_FANTI=3
	 * @throws SegmentationException
	 */
	public ICTSegmentation(String dataPath, int encoding) throws SegmentationException {
		if (!globalInited && !NLPIR.NLPIR_Init(dataPath.getBytes(), encoding)) {
			throw new SegmentationException();
		}
		globalInited = true;
		tokenizer = new NLPIR();
	}
	
	public ICTSegmentation(String dataPath) throws SegmentationException {
		this(dataPath, 1);
	}

	@Override
	public String segment(String str, String encoding, int posTagged) throws UnsupportedEncodingException {
		str = str.replaceAll(emoticon, " $0 ");
		StringBuffer output = new StringBuffer();
		for (String piece : str.split("\\s")) {
			if (Pattern.matches(emoticon, piece))
				output.append(piece).append(" ");
			else {
				byte[] result = tokenizer.NLPIR_ParagraphProcess(piece.getBytes("UTF-8"), posTagged);
				output.append(new String(result, 0, result.length - 1, "UTF-8")).append(" ");
//				for (int i = 0; i < piece.length(); ++i)
//					output += piece.charAt(i) + " ";
			}
		}
		//The third argument should be set to result.length-1, for the bug of NLPIR

		return output.toString().trim().replaceAll("\\s+", " ");
	}

	@Override
	public String segment(String str) throws UnsupportedEncodingException {
		return segment(str, "UTF-8", 0);
	}

	@Override
	public int importUserDict(String path) {
		return tokenizer.NLPIR_ImportUserDict(path.getBytes());
	}
	
	@Override
	public void addUserWord(String word) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		tokenizer.NLPIR_AddUserWord(word.getBytes("GBK"));
	}
}