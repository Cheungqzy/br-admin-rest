# api-rest脚手架使用说明

该脚手架定义了用于构建对外提供RESTful接口项目的基础内容。

## 基础内容

- 签名过滤器：SignCheckFilter
- 服务调用跟踪过滤器：YHTraceFilter
- accessToken校验拦截器：AccessApiInterceptor
- 请求日志拦截器：LogInterceptor
- `config.properties`中的应用名默认使用`artifactId`的值
- 示例Controller：`DemoController`

## 快速上手

- Swagger配置：根据需要修改`SwaggerConfig`，比如它的公共参数等
- AccessToken的校验：在`Controller`中使用`@AccessRequired`注解来被拦截器处理，同时可以通过`request.getAttribute("memberId")`或BaseController中的`getMemberId`方法获取用户id

