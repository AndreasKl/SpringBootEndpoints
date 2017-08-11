package io.fdlessard.codebites.endpoints;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class EndpointApplication {

    public static void main(String[] args) {
        SpringApplication.run(EndpointApplication.class, args);
    }

    @Configuration
    public static class WebConfig extends WebMvcConfigurationSupport {

        @Bean
        public HttpMessageConverter<?> constrainedJackson2HttpMessageConverter() {
            ConstrainedMappingJackson2HttpMessageConverter jsonConverter
                    = new ConstrainedMappingJackson2HttpMessageConverter(Wrapper.class);
            ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
            SimpleModule module = new SimpleModule();
            module.addSerializer(Wrapper.class, new IndicatorSerializer());
            module.setMixInAnnotation(Price.class, PriceMixin.class);
            objectMapper.registerModule(module);
            jsonConverter.setObjectMapper(objectMapper);
            return jsonConverter;
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(constrainedJackson2HttpMessageConverter());
            super.addDefaultHttpMessageConverters(converters);
        }
    }

    public static abstract class PriceMixin {

        @JsonProperty("priceId") abstract int getId();

    }

    public static class IndicatorSerializer extends StdSerializer<Wrapper> {

        public IndicatorSerializer() {
            super(Wrapper.class);
        }

        @Override
        public void serialize(Wrapper indicator, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(indicator.getValue());
        }
    }

    @Data
    @AllArgsConstructor
    public static class Wrapper<T> {
        private T value;

        public static <T> Wrapper<T> wrap(T value) {
            return new Wrapper<>(value);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Price {
        private String id;
        private String description;
    }

    @RestController
    @RequestMapping(value = "/controller1")
    static class PriceController1 {

        @GetMapping(value = "/price")
        public Price getPrice() {
            return new Price("id", "Description");
        }
    }

    @RestController
    @RequestMapping(value = "/controller2")
    static class PriceController2 {

        @GetMapping(value = "/price")
        public Wrapper<Price> getPrice() {
            return Wrapper.wrap(new Price("id", "Description"));
        }
    }
}
