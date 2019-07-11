package com.mtl;

import com.mtl.vo.Node;

/**
 * 说明：切换连接失败时，返回此异常
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/11 21:25
 */
public class NoneException extends Exception{
    private Node root;

    public NoneException(Node root,String msg,Throwable t) {
        super(msg,t);
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }
}
