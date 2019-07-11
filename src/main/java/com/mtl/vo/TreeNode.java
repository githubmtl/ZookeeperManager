package com.mtl.vo;

/**
 * 说明：树形节点
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/06 21:37
 */
public class TreeNode {
    private Node node;
    private String name;

    public TreeNode(Node node) {
        this.node = node;
        String nodeName=node.getName();
        if (node==Node.getRoot()){
            name=nodeName.substring(nodeName.lastIndexOf("/"));
        }else{
            name=nodeName.substring(nodeName.lastIndexOf("/")+1);
        }
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
