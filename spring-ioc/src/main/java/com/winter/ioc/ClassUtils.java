package com.winter.ioc;

import com.winter.ioc.annotation.Component;
import com.winter.ioc.annotation.Import;
import com.winter.ioc.bean.BeanDefinition;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

public class ClassUtils {

    /**
     * 根据包路径获取路径下的所有类
     *
     * @param packageName
     * @return
     */
    public static List<BeanDefinition> getAllClassByPakcage(String packageName) throws IOException {
        List<BeanDefinition> classList = new ArrayList<>();
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
    public static void findAllClassByPath(String packageName, String filePath, List<BeanDefinition> classList) {
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
                    classList.addAll(Arrays.stream(value).map(BeanDefinition::new).collect(Collectors.toList()));
                }
                if (existAnnotation(aClass, Component.class)) {
                    classList.add(new BeanDefinition(aClass));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean existAnnotation(Class<?> aClass, Class<? extends Annotation> targetAnnotation) {
        return findAnnotation(aClass, targetAnnotation) != null;
    }

    public static <T extends Annotation> T findAnnotation(Class<?> aClass, Class<T> targetAnnotation) {
        T annotation = aClass.getAnnotation(targetAnnotation);
        if (Objects.nonNull(annotation)) {
            return annotation;
        }
        Annotation[] annotations = aClass.getAnnotations();
        for (Annotation childAnnotation : annotations) {
            Class<? extends Annotation> annotationType = childAnnotation.annotationType();
            String name = annotationType.getName();
            if (name.equals(Retention.class.getName())
                    || name.equals(Documented.class.getName())
                    || name.equals(Target.class.getName())) {
                continue;
            }
            annotation = findAnnotation(annotationType, targetAnnotation);
            if (Objects.nonNull(annotation)) {
                return annotation;
            }
        }
        return null;
    }


    public static boolean existAnnotation(Field field, Class<? extends Annotation> targetAnnotation) {
        return field.getAnnotation(targetAnnotation) != null;
    }
}
