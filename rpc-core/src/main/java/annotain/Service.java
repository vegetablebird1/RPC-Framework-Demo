package annotain;

import java.lang.annotation.*;

/**
 * @author ming
 * @data 2021/6/15 20:09
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String name() default "";

}
