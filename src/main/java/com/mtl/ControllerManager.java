package com.mtl;

import java.util.HashMap;
import java.util.Map;

/**
 * 说明：Cotroller管理
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/08 20:18
 */
public class ControllerManager {
    private static Map<String,Object> controllers=new HashMap<String, Object>();
    public static Object get(String name){
        Object o = controllers.get(name);
        return o;
    }
    public static void set(String name,Object controller){
        controllers.put(name,controller);
    }
}
