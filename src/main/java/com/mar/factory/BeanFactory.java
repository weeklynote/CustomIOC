package com.mar.factory;

import com.mar.annotation.MyAutoWired;
import com.mar.annotation.MyService;
import com.mar.annotation.MyTransactional;
import com.mar.impl.AnnotationServiceImpl;
import com.mar.utils.ClassScanner;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author: 刘劲
 * @Date: 2020/4/7 22:28
 */
public class BeanFactory {
    private static Map<String, Object> hashMap = new HashMap<>(16);
    static {
        final InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        SAXReader saxReader = new SAXReader();
        try {
            final Document document = saxReader.read(resourceAsStream);
            final Element rootElement = document.getRootElement();
            final List<Element> elements = rootElement.selectNodes("//bean");
            if (!CollectionUtils.isEmpty(elements)){
                for (Element e : elements){
                    String id = e.attributeValue("id");
                    String clazz = e.attributeValue("class");
                    final Object o = Class.forName(clazz).newInstance();
                    hashMap.put(id, o);
                }
            }
            final List<Element> propertys = rootElement.selectNodes("//property");
            if (!CollectionUtils.isEmpty(propertys)){
                for (Element property : propertys) {
                    final String name = property.attributeValue("name");
                    final String ref = property.attributeValue("ref");
                    final Element parent = property.getParent();
                    String id = parent.attributeValue("id");
                    Object obj = hashMap.get(id);
                    String methodName = "set" + name;
                    Method[] methods = obj.getClass().getMethods();
                    for (int j = 0; j < methods.length; j++) {
                        Method method = methods[j];
                        if(method.getName().equalsIgnoreCase(methodName)) {
                            method.invoke(obj, hashMap.get(ref));
                        }
                    }
                    hashMap.put(id, obj);
                }
            }
            final List<Element> scanners = rootElement.selectNodes("component-scan");
            for (Element scanner : scanners) {
                final String basePackage = scanner.attributeValue("base-package");
                String[] basePkgs = basePackage.split(",");
                final Set<Class<?>> serviceClass = ClassScanner.scan(basePkgs, MyService.class);
                if (serviceClass != null && serviceClass.size() > 0){
                    for (Class<?> claz : serviceClass) {
                        final MyService annotation = claz.getAnnotation(MyService.class);
                        String id = annotation.value();
                        if (isEmpty(id)){
                            id = claz.getName();
                        }
                        final Object o = claz.newInstance();
                        hashMap.put(id, o);
                    }
                }
                final Set<Class<?>> autoWiredClass = ClassScanner.scanField(basePkgs, MyAutoWired.class);
                if (autoWiredClass != null && autoWiredClass.size() > 0){
                    for (Class<?> wiredClass : autoWiredClass) {
                        final Field[] declaredFields = wiredClass.getDeclaredFields();
                        for (Field declaredField : declaredFields) {
                            final MyAutoWired annotation = declaredField.getAnnotation(MyAutoWired.class);
                            if (annotation != null){
                                final Object obj = getBean(wiredClass);
                                String id = annotation.value();
                                Object bean = null;
                                if (!isEmpty(id)){
                                    bean = getBean(id);
                                }else {
                                    bean = getBean(Class.forName(declaredField.getType().getName()));
                                }
                                declaredField.setAccessible(true);
                                declaredField.set(obj, bean);
                            }
                        }
                    }
                }
                final Set<Class<?>> transactionalClass = ClassScanner.scan(basePkgs, MyTransactional.class);
                if (transactionalClass != null && transactionalClass.size() > 0){
                    for (Class<?> claz : transactionalClass) {
                        final Object bean = getBean(claz);
                        ProxyFactory proxyFactory = (ProxyFactory) getBean(ProxyFactory.class);
                        final Class<?>[] interfaces = claz.getInterfaces();
                        String key = getBeanName(bean.getClass());
                        Object proxy = null;
                        if (interfaces != null && interfaces.length > 0){
                            // 实现了接口使用JDK代理
                            proxy = proxyFactory.getJdkProxy(bean);
                        }else {
                            proxy = proxyFactory.getCglibProxy(bean);
                        }
                        hashMap.put(key, proxy);
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resourceAsStream != null){
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isEmpty(CharSequence sequence){
        return sequence == null || "".equals(sequence.toString().trim());
    }

    public static <T> T getBean(String id){
        System.err.println("get:" + id);
        final Object o = hashMap.get(id);
        System.err.println(o instanceof AnnotationServiceImpl);
        return (T) o;
    }

    private static Object getBean(Class<?> claz){
        for (Object o : hashMap.values()) {
            Class<?> clazz = o.getClass();
            // 处理继承关系的依赖注入
            if (claz == clazz || clazz.getSuperclass() == claz){
                return o;
            }
            final Class<?>[] interfaces = clazz.getInterfaces();
            // 处理实现接口的依赖注入
            if (interfaces != null && interfaces.length > 0){
                for (Class<?> anInterface : interfaces) {
                    if (claz == anInterface){
                        return o;
                    }
                }
            }
        }
        return null;
    }

    private static String getBeanName(Class<?> claz){
        final Iterator<Map.Entry<String, Object>> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()){
            final Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Object bean = next.getValue();
            if (claz == bean.getClass()){
                return key;
            }
        }
        return null;
    }
}
