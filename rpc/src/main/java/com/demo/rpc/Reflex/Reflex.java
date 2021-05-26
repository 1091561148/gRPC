package com.demo.rpc.Reflex;

import com.demo.rpc.Annotation.MyRPC;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/19:50
 * @Description:
 */
public class Reflex {
    private final static Logger log = Logger.getLogger("reflex");

    private static List<String> classNames = new ArrayList<>();
    private static Map<String, Object> ioc = new HashMap<>();

    public void doScanner(String packageName) {
        log.info("do scanner packageName:"+ packageName);
        URL url  = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        if(url == null){
            return;
        }
        File dir = new File(url.getFile());
        if(dir.exists()){
            File[] fileList = dir.listFiles();
            if(fileList == null || fileList.length <= 0){
                return;
            }
            for (File file : fileList) {
                if(file.isDirectory()){
                    //递归读取包
                    doScanner(packageName+"."+file.getName());
                }else{
                    String className =packageName +"." +file.getName().replace(".class", "");
                    classNames.add(className);
                }
            }
        }
    }

    /**
     * 初始化RPC方法
     */
    public void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            try {
                //把类搞出来,反射来实例化(只有加@MyRPC需要实例化)
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(MyRPC.class)){
                    MyRPC myService = clazz.getAnnotation(MyRPC.class);
                    String beanName = myService.value();
                    if ("".equals(beanName.trim())){
                        beanName = toLowerFirstWord(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);
                    //返回接口类
                    Class[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces){
                        ioc.put(i.getName(), instance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Object test(String name){
        ioc.forEach((key,value)->{
            System.out.println("key:"+key+"--value:"+value);
        });
        return ioc.get(name);
    }

    /**
     * Description:  将字符串中的首字母小写
     * Params:
     */
    private String toLowerFirstWord(String name){
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }
}
