package org.jiangf.segmentation;

import java.io.UnsupportedEncodingException;

abstract public class Segmentation {
	/**
	 * segment input string
	 * @param str: input string
	 * @return segmented text string
	 */
	abstract public String segment(String str) throws UnsupportedEncodingException;
	
	/**
	 * segment input string
	 * @param str: input string
	 * @param encoding: input string encoding
	 * @param posTagged: whether or not to postag the input string
	 * @return segmented string
	 */
	abstract public String segment(String str, String encoding, int posTagged) throws UnsupportedEncodingException;
	
	/**
	 * Import user dictionary
	 * @param path: the directory of the dictionary
	 * @return The number of lexical entry imported successfully
	 */
	abstract public int importUserDict(String path);
	
	abstract public void addUserWord(String word) throws UnsupportedEncodingException ;
}