package com.winter.ioc;

import com.winter.ioc.annotation.Import;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class ClassUtils {

    /**
     * 根据包路径获取路径下的所有类
     *
     * @param packageName
     * @return
     */
    public static List<Class<?>> getAllClassByPakcage(String packageName) throws IOException {
        List<Class<?>> classList = new ArrayList<>();
        String packagePath = packageName.replace(".", "/");

        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String protocol = url.getProtocol();

            if ("file".equals(protocol)) {
                System.out.println("file类型的扫描");
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                findAllClassByPath(packageName, filePath, classList);
            } else if ("jar".equals(protocol)) {
                System.out.println("jar类型的扫描");
            }
        }

        return classList;
    }

    /**
     * 获取给定路径下的所有class
     *
     * @param packageName 包名
     * @param filePath    路径
     * @param classList   返回列表
     */
    public static void findAllClassByPath(String packageName, String filePath, List<Class<?>> classList) {
        try {
            File rootFile = new File(filePath);
            if (!rootFile.exists()) {
                throw new RuntimeException(filePath);
            }


            if (rootFile.isDirectory()) {
                Arrays.stream(rootFile.listFiles(file -> (StringUtils.endsWith(file.getName(), "class") || file.isDirectory())))
                        .forEach(file -> findAllClassByPath(packageName + "." + file.getName(), file.getAbsolutePath(), classList));
            } else{
                String className = StringUtils.substringBeforeLast(packageName, ".");
                Class<?> aClass = Class.forName(className);

                Import annotation = aClass.getAnnotation(Import.class);
                if (Objects.nonNull(annotation)) {
                    Class<?>[] value = annotation.value();
                    classList.addAll(Arrays.asList(value));
                }
                classList.add(aClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
