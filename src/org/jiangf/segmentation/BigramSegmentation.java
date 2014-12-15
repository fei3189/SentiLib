package org.jiangf.segmentation;

import java.io.UnsupportedEncodingException;


public class BigramSegmentation extends Segmentation {

	@Override
	public String segment(String str) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length() - 1; ++i) {
			sb.append(str.substring(i, i + 2)).append(" ");
		}
		return sb.toString().trim();
	}

	@Override
	public String segment(String str, String encoding, int posTagged)
			throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length() - 1; ++i) {
			sb.append(str.substring(i, i + 2)).append(" ");
		}
		return sb.toString().trim();
	}

	@Override
	public int importUserDict(String path) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public void addUserWord(String word) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
	}
	
}