package com.mtl.vo;

import com.mtl.controller.Main;
import com.mtl.controller.MainController;
import com.mtl.zookeeper.ZookeeperConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.*;

/**
 * 说明：代表一个节点
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/06 19:20
 */
public class Node {
    private static Node root;//根节点
    private Node parent;//父节点
    private String name;//path
    private String data;//值
    private Stat stat=new Stat();//状态
    private Set<Node> children=new HashSet<Node>();//子节点

    public Node() {
    }

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public Node(Node parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public Set<Node> getChildren() {
        return children;
    }

    public void setChildren(Set<Node> children) {
        this.children = children;
    }

    public void addChild(Node node){
        children.add(node);
    }
    public void removeChid(Node node){
        children.remove(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return node.getName().equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static Node getRoot() {
        return root;
    }

    public static void setRoot(Node r) {
        root = r;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", stat=" + stat +
                ", children=" + children +
                '}';
    }

    /**
     * 从服务器获取子节点
     * @param zooKeeper
     */
    public void getChildren(ZooKeeper zooKeeper) throws Exception {
        List<String> children = zooKeeper.getChildren(name, false);
        if (children!=null&&children.size()>0){
            for (String child:children){
                String nodeName=null;
                if (this==Node.getRoot()){
                    nodeName=this.getName()+child;
                }else{
                    nodeName=this.getName()+"/"+child;
                }
                Node node=new Node(this,nodeName);
                byte[] data = zooKeeper.getData(node.getName(), false, node.getStat());
                node.setData(new String(data,"UTF-8"));
                this.addChild(node);
                //自己再获取自己子节点
                node.getChildren(zooKeeper);
            }
        }
    }

    /**
     * 初始化Node
     * @param zookeeper
     * @return
     * @throws Exception
     */
    public static Node init(ZooKeeper zookeeper)throws Exception{
        Node node=new Node("/");
        byte[] data = zookeeper.getData("/", false, node.getStat());
        node.setData(new String(data,"UTF-8"));
        List<String> children = zookeeper.getChildren("/", false);
        Node.setRoot(node);
        node.getChildren(zookeeper);
        return node;
    }

    /**
     * 重新载入Node
     * @param zookeeper
     * @return
     * @throws Exception
     */
    public static Node reload(ZooKeeper zookeeper)throws Exception{
        Node node=init(zookeeper);
        Node.setRoot(node);
        MainController.root=node;
        return node;
    }

    public static Node reload(TreeView<TreeNode> treeView){
        TreeItem<TreeNode> rootItem=new TreeItem<TreeNode>(new TreeNode(root));
        treeView.setRoot(rootItem);
        addChildren(rootItem,root);
        return root;
    }

    /**
     * 添加子节点
     * @param treeItem 界面上的节点
     * @param node zookeeper中的节点
     */
    public static void addChildren(TreeItem<TreeNode> treeItem, Node node){
        //待实现，动态更新获取节点（现在实现方式是一次性获取所有的节点）
        treeItem.expandedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println(observable);
                System.out.println(oldValue);
                System.out.println(newValue);
            }
        });
        Set<Node> children = node.getChildren();
        ObservableList<TreeItem<TreeNode>> treeItemChildren = treeItem.getChildren();
        if (children.size()>0){
            for (Node n:children){
                TreeNode treeNode=new TreeNode(n);
                TreeItem<TreeNode> ti=new TreeItem<TreeNode>(treeNode);
                treeItemChildren.add(ti);
                //再添加子节点
                addChildren(ti,n);
            }
        }
    }

}
