package com.mtl;

import com.mtl.controller.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * 说明：
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/07 16:50
 */
public class FxmlManager {
    /**
     * 保存所有可以缓存的页面
     */
    private static Map<String,Parent> parents=new HashMap<String, Parent>();


    private static void add(String name,Parent parent){
        parents.put(name,parent);
    }
    public static Parent get(String name){
        return parents.get(name);
    }

    public static void add(String key,String url){
        try {
            Parent p = FXMLLoader.load(Main.class.getResource(url));
            parents.put(key,p);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int addFromPath(String config){
        try {
            InputStream resourceAsStream = Main.class.getResourceAsStream(config);
            Properties properties=new Properties();
            properties.load(resourceAsStream);
            Iterator<Map.Entry<Object, Object>> iterator = properties.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Object, Object> next = iterator.next();
                if (next.getKey()!=null&&!next.getKey().equals("")){
                    add(next.getKey().toString(), next.getValue().toString());
                }
            }
            return properties.size();
        }catch (Exception e){
            throw new RuntimeException("读取fxml文件错误！",e);
        }
    }

    public static Map<String,Parent> getAll(){
        return parents;
    }
}
