package pl.mrndesign.matned.app.service.common;

public interface PropertiesService {

    String getProperty(String path);

    String getProperty(String path, String defaultValue);

    <T> T getProperty(String path, Class<T> targetType);

}
