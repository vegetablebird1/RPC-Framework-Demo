package hook;

import com.ming.factory.ThreadPoolFactory;
import com.ming.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ming
 * @data 2021/6/15 21:00
 */

public class ShutdownHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook(){
        LOGGER.info("关闭jvm前将注销所有服务中,清除状态");
        //关闭jvm前执行
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutdownAllThreadPool();
        }));
    }

}
