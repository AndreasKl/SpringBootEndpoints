package io.fdlessard.codebites.endpoints;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;

import java.lang.reflect.Type;

public class ConstrainedMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private final Class<?> type;

    public ConstrainedMappingJackson2HttpMessageConverter(Class<?> type) {
        Assert.notNull(type, "Type must not be null!");
        this.type = type;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return type.isAssignableFrom(clazz) && super.canRead(clazz, mediaType);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return this.type.isAssignableFrom(getJavaType(type, contextClass).getRawClass())
                && super.canRead(type, contextClass, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return type.isAssignableFrom(clazz) && super.canWrite(clazz, mediaType);
    }
}