package com.brother.controller;

import com.brother.security.TokenRecognizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by kainlin on 2015/11/5.
 */
public class BaseController {

    @Autowired
    private TokenRecognizer tokenRecognizer;

    @ModelAttribute("version")
    protected String getVersion(@RequestParam(value = "v", required = false) String v) {
        return v;
    }

    private Boolean isTestEnv;

    protected Boolean isTestEnv() {
        if (isTestEnv == null) {
            Resource resource = null;
            Properties props = null;
            try {
                resource = new ClassPathResource("/config.properties");
                props = PropertiesLoaderUtils.loadProperties(resource);
                String env = (String) props.get("env");
                switch (env) {
                    case "dev":
                    case "integration":
                    case "stage":
                    case "prod":
                        isTestEnv = true;
                        break;
                    default:
                        isTestEnv = false;
                }

            } catch (IOException e) {
                isTestEnv = false;
            }
        }
        return isTestEnv;
    }

    protected Long getMemberId(String token, boolean ignoreNotLogged) {
        return tokenRecognizer.getMemberId(token, ignoreNotLogged);
    }

}
