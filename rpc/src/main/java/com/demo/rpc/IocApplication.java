package com.demo.rpc;

import com.demo.rpc.annotation.MyAutowired;
import com.demo.rpc.annotation.MyRPC;
import com.demo.rpc.annotation.MyTest;
import com.demo.rpc.reflex.JdkProxy;
import com.demo.rpc.rpc.CalculateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/27/21:39
 * @Description:
 */
public class IocApplication {
    private final static Logger logger = LoggerFactory.getLogger(IocApplication.class);
    /**
     * RPC处理handler
     */
    private CalculateRequest calculateRequest;

    public IocApplication(String packageName, CalculateRequest request){
        this.calculateRequest = request;
        doScanner(this.getClass().getClassLoader(), packageName);
    }

    public void init(){
        doInstance();
        doAutowired();
    }

    private static List<String> classNames = new ArrayList<>();
    /**
     * IOC 容器
     */
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
                    JdkProxy jdkProxy = new JdkProxy();
//                    //返回接口类
                    Class[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces){
                        ioc.put(i.getName(), jdkProxy.getProxy(calculateRequest, beanName, i, instance));
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
