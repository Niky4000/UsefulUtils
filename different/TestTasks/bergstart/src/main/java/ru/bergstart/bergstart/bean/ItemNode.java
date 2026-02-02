package ru.bergstart.bergstart.bean;

import java.util.HashMap;
import java.util.Map;

public class ItemNode {

    private char key;
    private ItemBean item;
    private final Map<Character, ItemNode> nodeMap;

    public ItemNode(char key) {
        this.key = key;
        this.item = null;
        this.nodeMap = new HashMap<>();
    }

    public void setItem(ItemBean item) {
        this.item = item;
    }

    public ItemBean getItem() {
        return item;
    }

    public Map<Character, ItemNode> getNodeMap() {
        return nodeMap;
    }

    public void setNextNode(Character character, ItemNode next) {
        nodeMap.put(character, next);
    }

    @Override
    public String toString() {
        return "ItemNode{" + "key=" + key + ", item=" + item + ", nodeMap=\n" + nodeMap + "\n}";
    }
}
