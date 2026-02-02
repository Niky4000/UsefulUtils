package ru.bergstart.bergstart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.bergstart.bergstart.bean.ItemBean;
import ru.bergstart.bergstart.bean.ItemNode;
import ru.bergstart.bergstart.bean.SearchBean;

public class SearchTree {

    Map<Character, ItemNode> nodeMap = new HashMap<>();

    public void buildTree(List<ItemBean> items) {
        nodeMap = new HashMap<>();
        buildTree(items, nodeMap);
    }

    public void buildTree(List<ItemBean> items, Map<Character, ItemNode> nodeMap) {
        for (ItemBean item : items) {
            if (item.getItems() != null) {
                buildTree(item.getItems(), nodeMap);
            }
            String code = item.getCode();
            char key = code.charAt(code.length() - 1);
            ItemNode currentNode = nodeMap.computeIfAbsent(key, k -> new ItemNode(key));
            for (int i = code.length() - 1; i > 0; i--) {
                Character charAt = code.charAt(i - 1);
                if (!charAt.equals(".".charAt(0))) {
                    currentNode.getNodeMap().putIfAbsent(charAt, new ItemNode(charAt));
                    currentNode = currentNode.getNodeMap().get(charAt);
                }
            }
            currentNode.setItem(item);

        }
    }

    public SearchBean search(String str) {
        char key = str.charAt(str.length() - 1);
        ItemBean theMostRelevantItem = null;
        ItemNode currentNode = nodeMap.get(key);
        StringBuilder keyOfCoincidence = new StringBuilder(key + "");
        if (currentNode != null) {
            for (int i = str.length() - 1; i > 0; i--) {
                Character charAt = str.charAt(i - 1);
                if (!charAt.equals(".".charAt(0))) {
                    currentNode = currentNode.getNodeMap().get(charAt);
                    if (currentNode == null) {
                        break;
                    } else {
                        keyOfCoincidence.append(charAt);
                        theMostRelevantItem = currentNode.getItem();
                    }
                }
            }
        }
        return new SearchBean(keyOfCoincidence.reverse().toString(), theMostRelevantItem);
    }
}
