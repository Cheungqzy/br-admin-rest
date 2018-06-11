package com.brother.controller;

import com.brother.common.constants.Constant;
import com.brother.common.constants.SaleChannelEnum;
import com.brother.security.TokenRecognizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.com.brother.common.exception.BaseKnownException;
import javax.com.brother.common.exception.log.ExceptionLogger;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by kainlin on 2015/11/5.
 */
public class BaseController {

    @Autowired
    private TokenRecognizer tokenRecognizer;


    @ModelAttribute("saleChannelId")
    protected Integer getSaleChannelId(@RequestParam(value = "platform") String platform) {

        if (platform.compareToIgnoreCase("ios") == 0) {
            return SaleChannelEnum.APPLE.value();
        }

        if (platform.compareToIgnoreCase("android") == 0) {
            return SaleChannelEnum.ANDROID.value();
        }

        throw BaseKnownException.getUserErrorException(Constant.ERRORCODE_INVALID_SYSTEM_PARAMETER,
                String.format(Constant.ERRORMESSAGE_INVALID_SYSTEM_PARAMETER, "platform"));
    }

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
                ExceptionLogger.log(e, null);
                isTestEnv = false;
            }
        }
        return isTestEnv;
    }

    protected Long getMemberId(String token, boolean ignoreNotLogged) {
        return tokenRecognizer.getMemberId(token, ignoreNotLogged);
    }

}
