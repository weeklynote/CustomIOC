
import com.mar.factory.BeanFactory;
import com.mar.impl.AnnotationServiceImpl;
import org.junit.Test;

/**
 * @Author: 刘劲
 * @Date: 2020/4/7 22:48
 */
public class IOCTest {

    @Test
    public void testAnnotation() throws Exception {
        // jdk代理模式，注意下需要定义成接口类，直接使用实现类会出问题，还需要实现AnnotationService接口
//        AnnotationService annotationService = BeanFactory.getBean("com.mar.impl.AnnotationServiceImpl");
//        annotationService.updateAccountByCardNo("11", "22", 1000);
        // cglib方式
        AnnotationServiceImpl annotationService = BeanFactory.getBean("com.mar.impl.AnnotationServiceImpl");
        annotationService.updateAccountByCardNo("11", "22", 1000);
    }

}
