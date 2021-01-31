package com.ddg.meituan.authserver.controller;

import com.ddg.meituan.authserver.feign.ThirdPartyFeignService;
import com.ddg.meituan.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 * ========================================================================
 * ------------------------------------------------------------------------
 *
 * @author Edison
 * @version 1.0
 * <p>
 * ========================================================================
 * @date 2021/1/31 13:37
 * @email: wangzhijie0908@gmail.com
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @GetMapping("/send/code/{phoneNum}")
    public R sendCOde(@PathVariable("phoneNum")String phoneNum){

        return thirdPartyFeignService.getSendPhoneNum(phoneNum);

    }
}
