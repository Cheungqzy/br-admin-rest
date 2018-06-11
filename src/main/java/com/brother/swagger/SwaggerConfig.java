package com.brother.swagger;

import com.brother.web.frame.swagger.SwaggerConfigTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SwaggerConfig extends SwaggerConfigTools {

    @Value("${swagger.host}")
    private String host;

    @Bean
    public Docket api1() {
        return buildDocket(new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .host(host)
                .select()
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(getGlobalParam())
        );
    }

    private ApiInfo getApiInfo() {
        // Api文档信息
        String title = "br-admin-rest";
        String description = "br-admin-rest-rest document";
        String version = "1.0 build " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String termsOfServiceUrl = "";
        Contact contact = new Contact("", "", "");  // 创建联系人信息
        String license = "";
        String licenseUrl = "";
        ApiInfo apiInfo = new ApiInfo(
                title, description, version, termsOfServiceUrl, contact, license, licenseUrl
        );
        return apiInfo;
    }


    private List<Parameter> getGlobalParam() {
        // TODO 设置API通用参数
        List<Parameter> r = new ArrayList<>();
        r.add(new ParameterBuilder().name("access_token").description("登录令牌").modelRef(new ModelRef("string")).parameterType("query").required(false).build());
        r.add(new ParameterBuilder().name("timestamp").description("Unix时间戳，例如：1444118010").modelRef(new ModelRef("long")).parameterType("query").required(true).build());
        r.add(new ParameterBuilder().name("sign").description("签名").modelRef(new ModelRef("string")).parameterType("query").required(true).build());
        r.add(new ParameterBuilder().name("platform").description("平台 ios android").modelRef(new ModelRef("string")).parameterType("query").required(true).build());
        r.add(new ParameterBuilder().name("channel").description("渠道").modelRef(new ModelRef("string")).parameterType("query").required(true).build());
        r.add(new ParameterBuilder().name("v").description("客户端版本号").modelRef(new ModelRef("string")).parameterType("query").required(true).build());
        r.add(new ParameterBuilder().name("deviceid").description("从服务端获取的设备id").modelRef(new ModelRef("string")).parameterType("query").required(false).build());
        return r;
    }

}