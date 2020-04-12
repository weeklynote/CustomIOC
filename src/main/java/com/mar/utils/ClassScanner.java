package com.mar.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: 刘劲
 * @Date: 2020/4/12 12:16
 */
public class ClassScanner {

    private static final String CLASS_SUFFIX = ".class";
    private static final String FILE_PROTOCOL = "file";
    private static FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            // 目录或者(class文件，并且不是内部类文件)
            return pathname.isDirectory() || (pathname.getName().endsWith(CLASS_SUFFIX) && !pathname.getName().contains("$"));
        }
    };

    public static Set<Class<?>> scan(String[] pkgs, Class<? extends Annotation> annoClazzes) throws IOException, ClassNotFoundException {
        Set<Class<?>> annotationClassesSet = new HashSet<>(16);
        if (pkgs == null || pkgs.length == 0) {
            return annotationClassesSet;
        }
        for (String pkg : pkgs) {
            String newPkg = pkg.replace(".", File.separator);
            final ClassLoader classLoader = ClassScanner.class.getClassLoader();
            final Enumeration<URL> resources = classLoader.getResources(newPkg);
            while (resources.hasMoreElements()){
                final URL url = resources.nextElement();
                if (FILE_PROTOCOL.equals(url.getProtocol())){
                    String path = url.getPath();
                    // 包路径那里有个斜杠的编码(%5c)
                    File dir = new File(path.replace("%5c", File.separator));
                    // 如果不存在说明配置错误，直接报错
                    if (!dir.exists()) {
                        throw new IllegalArgumentException(String.format("未找到%s包路径，请检查是否正确", pkg));
                    }
                    if (dir.isFile() && dir.getName().endsWith(CLASS_SUFFIX)) {
                        String fileName = dir.getAbsolutePath().replace(File.separator, ".");
                        int length = fileName.length();
                        fileName = fileName.substring(0, length - CLASS_SUFFIX.length());
                        int index = fileName.indexOf(pkg);
                        String clazName = fileName.substring(index);
                        Class<?> claz = Class.forName(clazName);
                        final Annotation annotation = claz.getAnnotation(annoClazzes);
                        if (annotation != null){
                            annotationClassesSet.add(claz);
                        }
                        continue;
                    }
                    final File[] files = dir.listFiles(fileFilter);
                    if (files == null || files.length == 0) {
                        continue;
                    }
                    scan(annotationClassesSet, files, pkg, annoClazzes);
                }
            }
        }
        return annotationClassesSet;
    }

    public static Set<Class<?>> scanField(String[] pkgs, Class<? extends Annotation> annoClazzes) throws IOException, ClassNotFoundException {
        Set<Class<?>> annotationClassesSet = new HashSet<>(16);
        if (pkgs == null || pkgs.length == 0) {
            return annotationClassesSet;
        }
        for (String pkg : pkgs) {
            String newPkg = pkg.replace(".", File.separator);
            final ClassLoader classLoader = ClassScanner.class.getClassLoader();
            final Enumeration<URL> resources = classLoader.getResources(newPkg);
            while (resources.hasMoreElements()){
                final URL url = resources.nextElement();
                if (FILE_PROTOCOL.equals(url.getProtocol())){
                    String path = url.getPath();
                    // 包路径那里有个斜杠的编码(%5c)
                    File dir = new File(path.replace("%5c", File.separator));
                    // 如果不存在说明配置错误，直接报错
                    if (!dir.exists()) {
                        throw new IllegalArgumentException(String.format("未找到%s包路径，请检查是否正确", pkg));
                    }
                    if (dir.isFile() && dir.getName().endsWith(CLASS_SUFFIX)) {
                        String fileName = dir.getAbsolutePath().replace(File.separator, ".");
                        int length = fileName.length();
                        fileName = fileName.substring(0, length - CLASS_SUFFIX.length());
                        int index = fileName.indexOf(pkg);
                        String clazName = fileName.substring(index);
                        Class<?> claz = Class.forName(clazName);
                        final Field[] declaredFields = claz.getDeclaredFields();
                        for (Field declaredField : declaredFields) {
                            final Annotation annotation = declaredField.getAnnotation(annoClazzes);
                            if (annotation != null){
                                annotationClassesSet.add(claz);
                                break;
                            }
                        }
                        continue;
                    }
                    final File[] files = dir.listFiles(fileFilter);
                    if (files == null || files.length == 0) {
                        continue;
                    }
                    scanFiled(annotationClassesSet, files, pkg, annoClazzes);
                }
            }
        }
        return annotationClassesSet;
    }

    private static void scan(Set<Class<?>> annotationClassesSet, File[] files, String pkg, Class<? extends Annotation> annoClazzes) throws ClassNotFoundException {
        for (File file : files) {
            if (file.isDirectory()) {
                Set<Class<?>> set = getDiretoryClass(file, pkg, annoClazzes);
                if (set != null && set.size() > 0){
                    annotationClassesSet.addAll(set);
                }
                continue;
            }
            String clazName = file.getAbsolutePath().replace(File.separator, ".");
            int length = clazName.length();
            clazName = clazName.substring(0, length - CLASS_SUFFIX.length());
            int index = clazName.indexOf(pkg);
            clazName = clazName.substring(index);
            Class<?> claz = Class.forName(clazName);
            Annotation annotation = claz.getAnnotation(annoClazzes);
            if (annotation != null){
                annotationClassesSet.add(claz);
            }
        }
    }

    private static void scanFiled(Set<Class<?>> annotationClassesSet, File[] files, String pkg, Class<? extends Annotation> annoClazzes) throws ClassNotFoundException {
        for (File file : files) {
            if (file.isDirectory()) {
                Set<Class<?>> set = getDiretoryClassField(file, pkg, annoClazzes);
                if (set != null && set.size() > 0){
                    annotationClassesSet.addAll(set);
                }
                continue;
            }
            String clazName = file.getAbsolutePath().replace(File.separator, ".");
            int length = clazName.length();
            clazName = clazName.substring(0, length - CLASS_SUFFIX.length());
            int index = clazName.indexOf(pkg);
            clazName = clazName.substring(index);
            Class<?> claz = Class.forName(clazName);
            final Field[] declaredFields = claz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                final Annotation annotation = declaredField.getAnnotation(annoClazzes);
                if (annotation != null){
                    annotationClassesSet.add(claz);
                    break;
                }
            }
        }
    }

    public static Set<Class<?>> getDiretoryClass(File directory, String pkg, Class<? extends Annotation> annoClazzes) throws ClassNotFoundException {
        Set<Class<?>> annotationClassesSet = new HashSet<>(16);
        final File[] files = directory.listFiles(fileFilter);
        if (files == null || files.length == 0) {
            return null;
        }
        scan(annotationClassesSet, files, pkg, annoClazzes);
        return annotationClassesSet;
    }

    public static Set<Class<?>> getDiretoryClassField(File directory, String pkg, Class<? extends Annotation> annoClazzes) throws ClassNotFoundException {
        Set<Class<?>> annotationClassesSet = new HashSet<>(16);
        final File[] files = directory.listFiles(fileFilter);
        if (files == null || files.length == 0) {
            return null;
        }
        scanFiled(annotationClassesSet, files, pkg, annoClazzes);
        return annotationClassesSet;
    }
}
