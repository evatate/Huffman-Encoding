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
