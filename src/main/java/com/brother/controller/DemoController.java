package com.brother.controller;

import com.brother.common.web.WebResult;
import com.brother.interceptor.AccessRequired;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 翟永超
 * @create 2016/11/10.
 * @blog http://blog.didispace.com
 */
@RestController
public class DemoController extends BaseController {

    @ApiOperation(value = "demo", notes = "最简单的一个请求响应")
    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public WebResult<String> demo() {
        return WebResult.ok("api-rest : this is a demo");
    }

    @AccessRequired
    @ApiOperation(value = "具有token校验的请求", notes = "accessToken的创建在api-rest中，其他api应用实现校验即可。后续这些逻辑会移入api-gateway统一管理，api应用实现无状态化。")
    @RequestMapping(value = "/hasAuth", method = RequestMethod.POST)
    public WebResult<Long> previewOrder(
            @ModelAttribute("version") String version,
            @ModelAttribute("saleChannelId") Integer saleChannelId,
            HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        return WebResult.ok(memberId);
    }

}
