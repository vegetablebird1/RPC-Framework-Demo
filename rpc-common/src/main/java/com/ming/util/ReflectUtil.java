package com.ming.util;

import sun.net.www.protocol.jar.JarURLConnection;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 反射工具包,实现服务扫描
 * @author ming
 * @data 2021/6/17 15:26
 */

public class ReflectUtil {

    //跟踪方法调用的堆栈信息
    public static String getStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        return stackTraceElements[stackTraceElements.length - 1].getClassName();
    }

    //扫描存在service注解的包及其子包，获得全部类。
    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageDirName = packageName.replace(".","/");
        Enumeration<URL> dirs; //存放目录
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageName);
            //迭代获得所有
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                //获得协议，如文件还是目录...
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    //获得包路径
                    String filePath = URLDecoder.decode(url.getFile(),"UTF-8");
                    //扫描包下所有文件，加入到集合中
                    findAndAddClassesInPackageByFile(packageName,filePath,recursive,classes);
                } else if ("jar".equals(protocol)) {
                    //是jar文件
                    JarFile jarFile;
                    //获得jar包
                    jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                    //获得枚举类
                    Enumeration<JarEntry> entries = jarFile.entries();

                    //循环
                    while (entries.hasMoreElements()) {
                        //获得jar文件内的内容，可以是目录，MATE-INF等文件
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith("/")) {
                            //获得后面字符串
                            name = name.substring(1);
                        }
                        //前半部分和目标包名相同
                        if (name.startsWith(packageDirName)) {
                            int index = name.lastIndexOf('/');
                            //以‘/’结尾的，是一个包
                            if (index != -1){
                                // /换成.
                                packageName = name.substring(0,index).replace('/','.');
                            }
                            //是一个包
                            if (index != -1 || recursive) {
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    //去掉.class获得真正的类名
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);

                                    try {
                                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 根据文件路径获得所有类并加入到set中
     * @param packageName 包名
     * @param packagePath 包路径
     * @param recursive 是否递归
     * @param classes 类集合
     */
    private static void findAndAddClassesInPackageByFile(String packageName,
                                                         String packagePath,
                                                         final boolean recursive,
                                                         Set<Class<?>> classes) {
        //建立目录
        File dir = new File(packagePath);
        //不存在或者不是目录返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        //存在则获取所有的类或目录,可设置过滤一些非class,或非目录文件
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return ((recursive && pathname.isDirectory()) || pathname.getName().endsWith(".class"));
            }
        });

        //循环所有文件
        for (File file : dirFiles) {
            //是目录，递归处理
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(
                        packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            } else {
                //为.class文件，只留下类名
                String className = file.getName().substring(0,file.getName().length() - 6);
                try {
                    //loadClass比Class.forName好，不会触发static方法
                    // classes.add(Class.forName(packageName + "." + className));
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
