package com.example.socket.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 陈振东
 * @create 2020/6/5 13:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@Slf4j
public class ServerProperties {

    @Value("${ecf.toB.server.stockServerUrl}")
    private String stockServerUrl = "";


}
