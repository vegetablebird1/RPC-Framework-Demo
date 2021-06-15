package loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡
 * @author ming
 * @data 2021/6/15 20:47
 */

public interface LoadBalancer {

    Instance select(List<Instance> instances);

}
