package annotain;

import java.lang.annotation.*;

/**
 * @author ming
 * @data 2021/6/15 20:16
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceScan {

    String value() default "";

}
