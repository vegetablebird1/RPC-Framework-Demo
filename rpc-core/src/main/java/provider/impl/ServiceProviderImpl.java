package provider.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import provider.ServiceProvider;

/**
 * @author ming
 * @data 2021/6/15 22:10
 */

public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProviderImpl.class);



    @Override
    public <T> void addServiceProvider(T service, String serviceName) {

    }

    @Override
    public Object getServiceProvider(String serviceName) {
        return null;
    }
}
