package org.alvin.config;

import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class MilvusConfiguration {

    @Value("${milvus.config.ipAddr}")
    private String ipAddr;

    @Value("${milvus.config.port}")
    private Integer port;

    @Bean
    @Scope("singleton")
    public MilvusServiceClient milvusClient() {
        return getMilvusFactory().getMilvusServiceClient();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public MilvusRestClientFactory  getMilvusFactory() {
        return MilvusRestClientFactory.build(ipAddr, port);
    }

}
