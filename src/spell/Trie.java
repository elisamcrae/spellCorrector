package spell;

public class Trie implements ITrie{
    private final TrieNode root = new TrieNode();
    private int wordCount;
    private int nodeCount = 1;
    private Boolean toReturn = true;

    @Override
    public void add(String word) {
        boolean createdNewNode = false;
        TrieNode currentNode = root;
        for (int i = 0; i < word.length(); ++i) {
            int ind = word.charAt(i) - 'a';
            if (currentNode.getChildren()[ind] == null) {
                currentNode.getChildren()[ind] = new TrieNode();
                ++nodeCount;
                createdNewNode = true;
            }
            currentNode = (TrieNode)currentNode.getChildren()[ind];
        }
        if (createdNewNode | currentNode.getValue() == 0) {++wordCount;}
        currentNode.incrementValue();
    }

    @Override
    public INode find(String word) {
        TrieNode currentNode = root;
        for(int i = 0; i < word.length(); ++i) {
            int ind = Character.toLowerCase(word.charAt(i)) - 'a';
            if (currentNode.getChildren()[ind] == null) {return null;}
            currentNode = (TrieNode)currentNode.getChildren()[ind];
        }
        if (currentNode.getValue() != 0) {return currentNode;}
        else {return null;}
    }

    @Override
    public int getWordCount() {return wordCount;}

    @Override
    public int getNodeCount() {return nodeCount;}

    @Override
    public String toString() {
        StringBuilder curWord = new StringBuilder();
        StringBuilder output = new StringBuilder();
        toStringHelper(root, curWord, output);
        return output.toString();
    }

    //Root is never null, which is why I can pass it into my helper function
    private void toStringHelper(TrieNode n, StringBuilder curWord, StringBuilder output) {
        if (n.getValue() > 0) {
            output.append(curWord.toString());
            output.append("\n");
        }
        for (int i = 0; i < 26; ++i) {
            INode child = n.getChildren()[i];
            if (child != null) {
                char childLetter = (char)('a' + i);
                curWord.append(childLetter);
                toStringHelper((TrieNode)child, curWord, output);
                curWord.deleteCharAt(curWord.length() -1);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {return false;}
        if (o == this) {return true;}
        if (o.getClass() != this.getClass()) {return false;}
        Trie d = (Trie)o;
        if (d.nodeCount != this.nodeCount | d.wordCount != this.wordCount) {
            return false;
        }
        return equalsHelper(d.root, this.root);
    }

    private boolean equalsHelper(TrieNode n, TrieNode p) {
        if (!this.toReturn) {return false;}
        if (n == null && p == null) {return false;}
        if (n == null | p == null) {
            this.toReturn = false;
            return false;
        }
        if (n.getValue() != p.getValue()) {
            this.toReturn = false;
            return false;
        }
        //do they both have non-null children in exactly the same indexes?
        for (int i = 0; i < 26; ++i) {
            equalsHelper((TrieNode)n.getChildren()[i], (TrieNode)p.getChildren()[i]);
        }
        return this.toReturn;
    }

    @Override
    public int hashCode() {
        int toAdd = 0;
        for (int i = 0; i < 26; ++i) {
            if (root.getChildren()[i] != null) {toAdd += i;}
        }
        return (wordCount * nodeCount * toAdd);
    }
}