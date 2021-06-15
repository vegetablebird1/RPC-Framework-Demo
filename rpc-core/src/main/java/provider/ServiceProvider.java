package provider;

/**
 * @author ming
 * @data 2021/6/15 22:08
 */

public interface ServiceProvider {

    <T> void addServiceProvider(T service,String serviceName);

    Object getServiceProvider(String serviceName);

}
