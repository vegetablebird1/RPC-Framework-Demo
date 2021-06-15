package loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import loadbalancer.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @author ming
 * @data 2021/6/15 20:53
 */

public class RandomBalancer implements LoadBalancer {

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
