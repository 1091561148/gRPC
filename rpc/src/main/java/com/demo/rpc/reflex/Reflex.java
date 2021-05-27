package com.demo.rpc.reflex;

import com.demo.rpc.annotation.MyAutowired;
import com.demo.rpc.annotation.MyRPC;
import com.demo.rpc.annotation.MyTest;
import com.demo.rpc.rpc.CalculateRpcRequest;
import com.demo.rpc.rpc.RPCObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/19:50
 * @Description:
 */
public class Reflex {
    private final static Logger logger = LoggerFactory.getLogger(Reflex.class);

    public Reflex(String packageName){
        doScanner(this.getClass().getClassLoader(), packageName);
    }

    public void init(){
        doInstance();
        doAutowired();
    }

    private static List<String> classNames = new ArrayList<>();
    private static Map<String, Object> ioc = new HashMap<>();

    /**
     * 初始化所有需要被扫描的类方法
     */
    public void doScanner(ClassLoader classLoader, String packageName) {
        logger.info("do scanner packageName:"+ packageName);
        URL url  = classLoader.getResource(packageName.replaceAll("\\.", "/"));
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
                    doScanner(classLoader,packageName+"."+file.getName());
                }else{
                    String className = packageName +"." +file.getName().replace(".class", "");
                    logger.info("do scanner packageName:"+ packageName + " className:" + className);
                    classNames.add(className);
                }
            }
        }
    }

    /**
     * 初始化IOC容器方法
     */
    private void doInstance() {
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
                        beanName = clazz.getName();
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);
//                    //返回接口类
                    Class[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces){
                        ioc.put(i.getName(), getProxy(beanName, i, instance));
                    }

                }else if(clazz.isAnnotationPresent(MyTest.class)){
                    ioc.put(className, clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:JDK 动态反射
     * return: void
     */
    @SuppressWarnings("unchecked")
    private <T> T getProxy(String beanName, Class<T> clazz, Object o) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},  (proxy, method, args) -> {
            logger.info("method:" + method.getName());
            //排除toString
            if ("toString".equals(method.getName())) {
                return method.invoke(o, args);
            }

            RPCObject rpc = new RPCObject();
            //方法名：findAll
            rpc.setMethod(method.getName());
            //接口对象的实现类
            rpc.setMyClass(beanName);
            //准备参数2 ： params:args
            rpc.setParamValues(args);
            rpc.setParameterTypes(method.getParameterTypes());
            // 获取被调用方法的返回值类型
            rpc.setReturnTypes(method.getReturnType());
            CalculateRpcRequest request = new CalculateRpcRequest();
            return request.getResponse(rpc);
        });

    }
    /**
     * Description:自动化的依赖注入
     * return: void
     */
    private void doAutowired(){

        if (ioc.isEmpty()){
            return;
        }
        for (Map.Entry<String,Object> entry : ioc.entrySet()){
            //包括私有的方法，在spring中没有隐私，@MyAutowired可以注入public、private字段
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields){
                if (!field.isAnnotationPresent(MyAutowired.class)){
                    continue;
                }
                MyAutowired autowired = field.getAnnotation(MyAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                //反射的对象在使用时应该取消 Java 语言访问检查
                field.setAccessible(true);
                try {
                    logger.info("doAutowired:{},getBeanName:{}" , entry.getValue() , (beanName));
                    field.set(entry.getValue(),ioc.get(beanName));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 从 IOC 容器里面取值
     * @param name bean name
     * @return bean对象
     */
    public static Object getBean(String name){
//        ioc.forEach((key,value)->{
//            log.info("key:"+key+"--value:"+value);
//        });
        return ioc.get(name);
    }

}
