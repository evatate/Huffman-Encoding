import java.util.Comparator;

/**
 * Provides a comparator for the priority queue that compares frequencies
 *
 * @author Eva Tate, Dartmouth CS10, Winter 2024
 */

public class TreeComparator implements Comparator<BinaryTree<CodeTreeElement>>{

    /**
     * takes two tree nodes and returns -1, 0, or 1 depending on whether the
     * first has a smaller frequency count, the counts are equal, or the second has the smaller frequency count.
     *
     * @return the comparator
     */
    public int compare(BinaryTree<CodeTreeElement> tree1, BinaryTree<CodeTreeElement> tree2) {
        return Long.compare(tree1.getData().getFrequency(), tree2.getData().getFrequency());
    }
}
