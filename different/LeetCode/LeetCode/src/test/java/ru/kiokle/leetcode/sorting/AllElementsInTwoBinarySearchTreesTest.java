package ru.kiokle.leetcode.sorting;

import ru.kiokle.leetcode.sorting.AllElementsInTwoBinarySearchTrees;
import java.util.List;
import org.junit.Test;
import ru.kiokle.leetcode.sorting.bean.TreeNode;

public class AllElementsInTwoBinarySearchTreesTest {

	@Test
	public void test() {
		List<Integer> allElements = new AllElementsInTwoBinarySearchTrees().getAllElements(new TreeNode(2, new TreeNode(1), new TreeNode(4)), new TreeNode(1, new TreeNode(0), new TreeNode(3)));
		allElements.forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
		List<Integer> allElements2 = new AllElementsInTwoBinarySearchTrees().getAllElements(new TreeNode(1, null, new TreeNode(8)), new TreeNode(8, new TreeNode(1), null));
		allElements2.forEach(n -> System.out.print(n + " "));
		System.out.println("-----------");
	}
}
