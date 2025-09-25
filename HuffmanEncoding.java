import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.io.*;

/**
 * Lossless file compression method involving variable-length encoding of characters
 * as developed by Huffman, writes a compressed and decompressed file
 *
 * @author Eva Tate, Dartmouth CS10, Winter 2024
 */

public class HuffmanEncoding implements Huffman {
    /**
     * Read file provided in pathName and count how many times each character appears
     * @param pathName - path to a file to read
     * @return - Map with a character as a key and the number of times the character appears in the file as value
     * @throws IOException
     */
    @Override
    public Map<Character, Long> countFrequencies(String pathName) throws IOException{

        Map<Character, Long> frequencyMap = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathName));

            // Checks if the file is empty
            if (!reader.ready()) {
                reader.close();
                throw new IOException("Empty file");
            }

            int currentChar;
            // reads through each character in file;
            while ((currentChar = reader.read()) != -1) {
                char character = (char) currentChar; // casts to character
                // if contains character, increments frequency value
                if (frequencyMap.containsKey(character)) {
                    frequencyMap.put(character, frequencyMap.get(character) + 1);
                } else {
                    // inserts new character into map
                    frequencyMap.put(character, 1L);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return frequencyMap;
    }

    /**
     * Construct a code tree from a map of frequency counts
     *
     * @param frequencies a map of Characters with their frequency counts from countFrequencies
     * @return the code tree.
     */
    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        // Handles the case of an empty file
        if (frequencies.isEmpty()) {
            return new BinaryTree<>(null); // Return an empty tree
        }

        // creates priority queue with comparator that compares frequencies of nodes
        PriorityQueue<BinaryTree<CodeTreeElement>> priorityQueue = new PriorityQueue<>(new TreeComparator());

        // traverses through map and adds to tree
        for (Map.Entry<Character, Long> entry : frequencies.entrySet()) {
            CodeTreeElement element = new CodeTreeElement(entry.getValue(), entry.getKey());
            BinaryTree<CodeTreeElement> tree = new BinaryTree<>(element);
            priorityQueue.add(tree);
        }

        // loops to combine trees into one large tree
        while (priorityQueue.size() > 1) {
            BinaryTree<CodeTreeElement> tree1 = priorityQueue.poll();
            BinaryTree<CodeTreeElement> tree2 = priorityQueue.poll();
            long sum = tree1.getData().getFrequency() + tree2.getData().getFrequency(); // frequency of new tree to be created
            CodeTreeElement element = new CodeTreeElement(sum, null);
            BinaryTree<CodeTreeElement> combinedTree = new BinaryTree<>(element, tree1, tree2);
            priorityQueue.add(combinedTree); // inserts combines tree back into priority queue
        }

        return priorityQueue.poll();
    }

    /**
     * Computes the code for all characters in the tree and enters them
     * into a map where the key is a character and the value is the code of 1's and 0's representing
     * that character.
     *
     * @param codeTree the tree for encoding characters produced by makeCodeTree
     * @return the map from characters to codes
     */
    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        // Handles the case of an empty file
        if (codeTree.size() == 0) {
            return new HashMap<>(); // Return an empty map
        }

        // Handles the case of a single character
        if (codeTree.isLeaf()) {
            CodeTreeElement data = codeTree.getData();
            if (data != null && data.getChar() != null) {
                Map<Character, String> codeMap = new HashMap<>();
                codeMap.put(data.getChar(), "0");
                return codeMap;
            } else {
                // Handles the case where data is null or the character is null
                return new HashMap<>();
            }
        }

        Map<Character, String> codeMap = new HashMap<>();
        buildCodeMap(codeTree, "", codeMap);
        return codeMap;
    }

    /**
     * Recursively traverses through tree and adds to codeMap when a leaf is hit
     *
     */
    private void buildCodeMap(BinaryTree<CodeTreeElement> tree, String code, Map<Character, String> codeMap) {
        // Handles case of empty or null tree
        if (tree == null || tree.getData() == null) {
            return;
        }

        // Traverses through tree to create code map
        if (tree.isLeaf()) {
            codeMap.put(tree.getData().getChar(), code);
        } else {
            buildCodeMap(tree.getLeft(), code + "0", codeMap);
            buildCodeMap(tree.getRight(), code + "1", codeMap);
        }
    }

    /**
     * Compress the file pathName and store compressed representation in compressedPathName.
     * @param codeMap - Map of characters to codes produced by computeCodes
     * @param pathName - File to compress
     * @param compressedPathName - Store the compressed data in this file
     * @throws IOException
     */
    @Override
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException {
        try {
            BufferedReader input = new BufferedReader(new FileReader(pathName));
            BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName);

            // Check if the file is empty
            if (!input.ready()) {
                input.close();
                throw new IOException("Empty file");
            }

            // Handles the case of a single character
            if (codeMap.size() == 1) {
                BufferedBitWriter output = new BufferedBitWriter(compressedPathName);
                output.writeBit(false);
                output.close();
                return;
            }

            int character;
            // reads through file
            while ((character = input.read()) != -1) {
                String code = codeMap.get((char) character); // looks up code word
                // writes code sequence
                for (char bit : code.toCharArray()) {
                    bitOutput.writeBit(bit == '1');
                }
            }

            input.close();
            bitOutput.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Decompress file compressedPathName and store plain text in decompressedPathName.
     * @param compressedPathName - file created by compressFile
     * @param decompressedPathName - store the decompressed text in this file, contents should match the original file before compressFile
     * @param codeTree - Tree mapping compressed data to characters
     * @throws IOException
     */
    @Override
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
        try {
            BufferedBitReader bitReader = new BufferedBitReader(compressedPathName);
            BufferedWriter bitOutput = new BufferedWriter(new FileWriter (decompressedPathName));

            if (codeTree.isLeaf()) {
                // Handles the case of a single character repeated multiple times
                CodeTreeElement data = codeTree.getData();
                if (data != null && data.getChar() != null) {
                    int repetitions = data.getFrequency().intValue();
                    for (int i = 0; i < repetitions; i++) {
                        bitOutput.write(data.getChar());
                    }
                    bitOutput.close();
                    bitReader.close();
                    return;
                } else {
                    // Handle the case where getData() is null or the character is null
                    bitOutput.close();
                    bitReader.close();
                    return;
                }
            }

            BinaryTree<CodeTreeElement> current = codeTree;

            // reads through each bit
            while (bitReader.hasNext()) {
                boolean bit = bitReader.readBit();

                // goes to right if 1 and left if 0
                if (bit) {
                    current = current.getRight();
                } else {
                    current = current.getLeft();
                }

                // gets character at leaf and writes it to file
                if (current.isLeaf()) {
                    bitOutput.write(current.data.getChar());
                    current = codeTree; // Resets for next character
                }
            }
            bitReader.close();
            bitOutput.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        // Creates object for tests
        Huffman huffman = new HuffmanEncoding();

        // Empty file
        testFile("inputs/empty.txt","inputs/empty_compressed.txt", "inputs/empty_decompressed.txt", huffman);

        // File with a single character
        testFile("inputs/singleCharacter.txt","inputs/singleCharacter_compressed.txt", "inputs/singleCharacter_decompressed.txt", huffman);

        // File with single character repeated
        testFile("inputs/repeat.txt","inputs/repeat_compressed.txt", "inputs/repeat_decompressed.txt", huffman);

        // Test with the US Constitution and War & Peace
        testFile("inputs/USConstitution.txt","inputs/USConstitution_compressed.txt", "inputs/USConstitution_decompressed.txt", huffman);
        testFile("inputs/WarAndPeace.txt","inputs/WarAndPeace_compressed.txt", "inputs/WarAndPeace_decompressed.txt", huffman);
    }

    /**
     * Method for tests
     */
    private static void testFile(String filename, String compressedName, String decompressedName, Huffman huffman) {
        try {
            System.out.println("Filename: " + filename);

            // prints the frequency map
            Map<Character, Long> frequencies = huffman.countFrequencies(filename);
            System.out.println("Frequency Map: " + frequencies);

            // prints the code tree
            BinaryTree<CodeTreeElement> codeTree = huffman.makeCodeTree(frequencies);
            System.out.println("Code Tree: " + codeTree);

            // prints the code map
            Map<Character, String> codeMap = huffman.computeCodes(codeTree);
            System.out.println("Code Map: " + codeMap);

            // compresses file
            huffman.compressFile(codeMap, filename, compressedName);

            // decompresses file
            huffman.decompressFile(compressedName, decompressedName, codeTree);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
