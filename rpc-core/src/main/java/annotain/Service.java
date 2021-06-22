package annotain;

import java.lang.annotation.*;

/**
 * 注解提供服务的实现类
 * @author ming
 * @data 2021/6/15 20:09
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String name() default "";

}
