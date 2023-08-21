package spell;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class SpellCorrector implements ISpellCorrector {
    private Trie dictionary = new Trie();

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        File file = new File(dictionaryFileName);
        Scanner scanner = new Scanner(file);
        while(scanner.hasNext()){
            String str = scanner.next();
            //Add word to TrieNode
            dictionary.add(str);
        }
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        //check if word is empty
        if (inputWord == "") {return null;}
        //check if word is already in tree
        inputWord = inputWord.toLowerCase();
        INode findResult = dictionary.find(inputWord);
        if (findResult != null) {return inputWord;}

        //get a list of edit distance 1 words
        List<String> toTry = getEditDistance(inputWord);
        List<Integer> counts = new ArrayList<Integer>();
        List<String> found = new ArrayList<String>();
        for (int i = 0; i < toTry.size(); ++i) {
            if (dictionary.find(toTry.get(i)) != null) {
                found.add(toTry.get(i));
                TrieNode tn = (TrieNode)dictionary.find(toTry.get(i));
                counts.add(tn.getValue());
            }
        }
        if (found.size() == 1) {return found.get(0);}
        if (found.isEmpty()) {
            //Try edit distance 2 words
            toTry = editDistanceTwo(toTry);
            for (int i = 0; i < toTry.size(); ++i) {
                if (dictionary.find(toTry.get(i)) != null) {
                    found.add(toTry.get(i));
                    TrieNode tn = (TrieNode)dictionary.find(toTry.get(i));
                    counts.add(tn.getValue());
                }
            }
        }
        int posLargest = -1;
        int largest = 0;
        String word = "";
        for (int i = 0; i < counts.size(); ++i) {
            if (counts.get(i) > largest) {
                largest = counts.get(i);
                posLargest = i;
                word = toTry.get(i);
            }
            if (counts.get(i) == largest) {
                if (toTry.get(i).compareTo(word) > 0) {
                    word = toTry.get(i);
                    posLargest = i;
                }
            }
        }
        if (posLargest != -1) {return found.get(posLargest);}
        else{return null;}
    }

    public List<String> editDistanceTwo(List<String> inp) {
        List<String> editTwo = new ArrayList<String>();
        for (int i = 0; i < inp.size(); ++i) {
            editTwo.addAll(getEditDistance(inp.get(i)));
        }
        return editTwo;
    }

    public List<String> getEditDistance(String inputWord) {
        List<String> toTry = new ArrayList<String>();
        //Condition 1: incorrect character
        for(int i = 0; i < inputWord.length(); ++i) {
            StringBuilder sb = new StringBuilder(inputWord);
            for (int j = 0; j < 26; ++j) {
                char letter = (char)('a' + j);
                sb.setCharAt(i, letter);
                toTry.add(sb.toString());
            }
        }
        //Condition 2: omit a character
        StringBuilder sb2 = new StringBuilder(inputWord);
        for (int i = 0; i < inputWord.length(); ++i) {
            char c = inputWord.charAt(i);
            sb2.deleteCharAt(i);
            toTry.add(sb2.toString());
            sb2.insert(i, c);
        }
        //Condition 3: insert extra character
        StringBuilder sb3 = new StringBuilder(inputWord);
        for (int i = 0; i < inputWord.length() + 1; ++i) {
            for (int j = 0; j < 26; ++j) {
                sb3.insert(i, (char)(j+'a'));
                toTry.add(sb3.toString());
                sb3.deleteCharAt(i);
            }
        }
        //Condition 4: transpose two adjacent characters
        StringBuilder sb4 = new StringBuilder(inputWord);
        for (int i = 0; i < inputWord.length()-1; ++i) {
            char c = sb4.charAt(i);
            sb4.deleteCharAt(i);
            sb4.insert((i+1), c);
            toTry.add(sb4.toString());
            sb4.deleteCharAt(i+1);
            sb4.insert(i, c);
        }
        return toTry;
    }
}