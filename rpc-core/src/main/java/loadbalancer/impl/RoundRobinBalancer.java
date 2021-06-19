package loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import loadbalancer.LoadBalancer;

import java.util.List;

/**
 * @author ming
 * @data 2021/6/15 20:49
 */

public class RoundRobinBalancer implements LoadBalancer {

    //存在共享数据，不能使用单例工厂
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
