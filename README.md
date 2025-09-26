# Huffman Encoding

This repository contains an implementation of **Huffman Encoding**, a lossless file compression method based on variable-length encoding of characters. The algorithm builds a binary tree where frequent characters are assigned shorter codes and infrequent characters longer codes, resulting in reduced overall file size.  

The project supports **compressing** and **decompressing** text files, with correct handling of edge cases such as empty files and single-character files.

---

## Materials Provided

- **BufferedBitReader.java** – utility for reading bits from compressed files  
- **BufferedBitWriter.java** – utility for writing bits to compressed files  
- **CodeTreeElement.java** – class representing nodes in the Huffman code tree  
- **Huffman.java** – interface defining required Huffman operations  

---

## Core Components

### HuffmanEncoding

`HuffmanEncoding.java` implements the Huffman interface. It provides methods for:  

- Counting character frequencies in a file  
- Constructing a Huffman code tree using a priority queue  
- Computing binary codes for each character  
- Compressing a file into a binary representation  
- Decompressing a binary file back into the original text  

#### Usage
```bash
javac *.java
java HuffmanEncoding
```

#### Example Workflow

1. Read input file and count character frequencies  
2. Build a Huffman code tree with `makeCodeTree`  
3. Generate codewords for characters with `computeCodes`  
4. Compress the file with `compressFile`  
5. Decompress the compressed file with `decompressFile`

---

### TreeComparator

`TreeComparator.java` provides a comparator for the priority queue used when constructing the Huffman code tree. It ensures trees are combined based on ascending frequency.

`public int compare(BinaryTree<CodeTreeElement> tree1, BinaryTree<CodeTreeElement> tree2) {`  
    `return Long.compare(tree1.getData().getFrequency(), tree2.getData().getFrequency());`  
`}`

---

## File Descriptions

* **HuffmanEncoding.java** – main implementation of the Huffman algorithm  
* **TreeComparator.java** – comparator for priority queue ordering  
* **BinaryTree.java** – generic binary tree class (assumed provided)  
* **BufferedBitReader.java** – bit-level file reader  
* **BufferedBitWriter.java** – bit-level file writer  
* **CodeTreeElement.java** – tree node for Huffman encoding

---

## Testing

The `main` method in `HuffmanEncoding` runs several test cases:

* Empty file (`empty.txt`)  
* File with a single character (`singleCharacter.txt`)  
* File with repeated single character (`repeat.txt`)  
* Large texts such as the **US Constitution** and **War & Peace**

Each test outputs:

* Character frequency map  
* Constructed code tree  
* Generated code map  
* Verification that decompressed file matches the original

---

## Limitations

* Tree can become inefficient with highly skewed distributions (e.g., one character dominating).  
* Compression ratio depends heavily on input file characteristics.  
* Current implementation reads entire file character-by-character (not buffered at higher level).

