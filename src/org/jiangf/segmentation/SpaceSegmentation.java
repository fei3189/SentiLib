package org.jiangf.segmentation;

import java.io.UnsupportedEncodingException;

public class SpaceSegmentation extends Segmentation {
	String emoticon = "\\[[^\\]\\s]{1,10}\\]";
	static boolean globalInited = false;

	public SpaceSegmentation() {
		globalInited = true;
	}

	@Override
	public String segment(String str, String encoding, int posTagged)
			throws UnsupportedEncodingException {
		return str;
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