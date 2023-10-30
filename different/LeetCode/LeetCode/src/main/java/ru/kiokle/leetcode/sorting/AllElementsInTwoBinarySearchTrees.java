package ru.kiokle.leetcode.sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import ru.kiokle.leetcode.sorting.bean.TreeNode;

public class AllElementsInTwoBinarySearchTrees {

	public List<Integer> getAllElements(TreeNode root1, TreeNode root2) {
		List<Integer> list1 = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		treeToList(root1, list1);
		treeToList(root2, list2);
		List<Integer> mergedList = merge(list1, list2);
		return mergedList;
	}

	private List<Integer> merge(List<Integer> list1, List<Integer> list2) {
		if (list1.isEmpty()) {
			return list2;
		} else if (list2.isEmpty()) {
			return list1;
		}
		int index1 = 0;
		int index2 = 0;
		List<Integer> list = new ArrayList<>(list1.size() + list2.size());
		for (int i = 0; i < list1.size() + list2.size(); i++) {
			if (index1 == list1.size() && index2 < list2.size()) {
				list.add(list2.get(index2++));
			} else if (index2 == list2.size() && index1 < list1.size()) {
				list.add(list1.get(index1++));
			} else if (list1.get(index1) > list2.get(index2)) {
				list.add(list2.get(index2++));
			} else {
				list.add(list1.get(index1++));
			}
		}
		return list;
	}

	private void treeToList(TreeNode treeNode, List<Integer> list) {
		if (treeNode != null) {
			if (treeNode.left != null) {
				treeToList(treeNode.left, list);
			}
			list.add(treeNode.val);
			if (treeNode.right != null) {
				treeToList(treeNode.right, list);
			}
		}
	}

	public List<Integer> getAllElements2(TreeNode root1, TreeNode root2) {
		Stack<TreeNode> st1 = new Stack<>();
		Stack<TreeNode> st2 = new Stack<>();
		List<Integer> res = new ArrayList<>();
		while (root1 != null || root2 != null || !st1.empty() || !st2.empty()) {
			while (root1 != null) {
				st1.push(root1);
				root1 = root1.left;
			}
			while (root2 != null) {
				st2.push(root2);
				root2 = root2.left;
			}
			if (st2.empty() || (!st1.empty() && st1.peek().val <= st2.peek().val)) {
				root1 = st1.pop();
				res.add(root1.val);
				root1 = root1.right;
			} else {
				root2 = st2.pop();
				res.add(root2.val);
				root2 = root2.right;
			}
		}
		return res;
	}
}
