package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		
		Scanner scDoc = new Scanner(new File(docFile));
		HashMap<String, Occurrence> currMap = new HashMap<String, Occurrence>(1000, 2.0f);
		
		while (scDoc.hasNext()) {
			
			String currWord = getKeyword(scDoc.next());
			
			if (currWord != null){
				
				if (currMap.containsKey(currWord)){
					
					currMap.get(currWord).frequency++;
				}
				
				else{
					
					currMap.put(currWord, new Occurrence(docFile, 1));
				}
			}
		}
		
		scDoc.close();
		return currMap;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		
		for (String currWord: kws.keySet()){
			
			Occurrence currOccurrence = kws.get(currWord);
			
			if (keywordsIndex.containsKey(currWord)){
				
				ArrayList<Occurrence> occs = keywordsIndex.get(currWord);
				occs.add(currOccurrence);
				ArrayList<Integer> temp = insertLastOccurrence(occs);
				keywordsIndex.put(currWord, occs);
			}
			
			else {
				
				ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
				occs.add(currOccurrence);
				keywordsIndex.put(currWord, occs);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		
		String wordStripped = word;
		
		for (int i = word.length() - 1; i != -1; i--){
			
			if (Character.isLetter(word.charAt(i))){
				
				break;
			}
			
			wordStripped = wordStripped.substring(0, wordStripped.length() - 1);
		}
		
		boolean hashable = true;
		
		for (int i = 0; i != wordStripped.length(); i++){
			
			if (!Character.isLetter(wordStripped.charAt(i))){
				
				hashable = false;
				break;
			}
		}
		
		if (hashable && !noiseWords.contains(wordStripped) && !wordStripped.equals("")){
			
			return wordStripped.toLowerCase();
		}
		
		return null;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		
		ArrayList<Integer> mSequence = new ArrayList<Integer>();
		Occurrence currOccurrence  = occs.get(occs.size() - 1);
		int currFreq = currOccurrence.frequency;
		int l = 0;
		int r = occs.size() - 2;
		int m = l + r / 2;
		
		while (l <= r){
			
			m = (l + r / 2);
			
			if (occs.get(m).frequency == currFreq){
				
				mSequence.add(m);
				break;
			}
			
			else if (occs.get(m).frequency > currFreq){
				
				r = m - 1;
				mSequence.add(m);
			}
			
			else{
				
				l = m + 1;
				mSequence.add(m);
			}
		}
		
		for (int i = occs.size() - 1; i > m; i--){
			
			occs.set(i, occs.get(i - 1));
		}
		
		occs.set(m, currOccurrence);
		return mSequence;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		
		if (!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)){
			
			return null;
		}
		
		ArrayList<Occurrence> occsKW1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> occsKW2 = keywordsIndex.get(kw2);
		ArrayList<String> top5 = new ArrayList<String>(5);
		int currdexKW1 = occsKW1.size() - 1;
		int currdexKW2 = occsKW2.size() - 1;
		
		if (!keywordsIndex.containsKey(kw1)){
			
			while (currdexKW2 >= 0){
				
				top5.add(occsKW2.get(currdexKW2).document);
				currdexKW2--;
			}
		}
		
		else if (!keywordsIndex.containsKey(kw2)){
			
			while (currdexKW1 >= 0){
				
				top5.add(occsKW1.get(currdexKW1).document);
				currdexKW1--;
			}
		}
		
		else{
			
			int counter = 0;
			
			while (currdexKW1 >= 0 && currdexKW2 >= 0 && counter < 5){
		
				if (occsKW1.get(currdexKW1).frequency < occsKW2.get(currdexKW2).frequency){
			
					if (!top5.contains(occsKW2.get(currdexKW2).document)){
					
						top5.add(occsKW2.get(currdexKW2).document);
					}
			
					currdexKW2--;
					counter++;
				}
		
				else{
			
					if (!top5.contains(occsKW1.get(currdexKW1).document)){
					
						top5.add(occsKW1.get(currdexKW1).document);
					}
			
					currdexKW1--;
					counter++;
				}
			}
			
			if (currdexKW1 >= 0 && counter < 5){
				
				while (currdexKW1 >= 0 && counter < 5){
					
					if (!top5.contains(occsKW1.get(currdexKW1).document)){
						
						top5.add(occsKW1.get(currdexKW1).document);
					}
			
					currdexKW1--;
					counter++;
				}
			}
			
			if (currdexKW2 >= 0 && counter < 5){
				
				while (currdexKW2 >= 0 && counter < 5){
					
					if (!top5.contains(occsKW2.get(currdexKW2).document)){
						
						top5.add(occsKW2.get(currdexKW2).document);
					}
			
					currdexKW2--;
					counter++;
				}
			}
		}
		
		return top5;
	}
}
