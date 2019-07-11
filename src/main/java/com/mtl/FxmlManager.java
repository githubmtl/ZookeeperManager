package com.mtl;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public static void add(File file){
        try {
            Parent p = FXMLLoader.load(file.toURI().toURL());
            parents.put(file.getName(),p);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int addFromPath(String path){
        URL resource = FxmlManager.class.getResource(path);
        File file=new File(resource.getPath());
        File[] files = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.endsWith(".fxml")&&name.startsWith("init"))
                    return true;
                return false;
            }
        });
        if (files!=null&&files.length>0){
            for (File f:files){
                add(f);
            }
        }
        return files.length;
    }

    public static Map<String,Parent> getAll(){
        return parents;
    }
}
