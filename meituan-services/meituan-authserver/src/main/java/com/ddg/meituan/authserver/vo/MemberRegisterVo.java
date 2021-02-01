package com.ddg.meituan.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Description: 用户注册表单vo
 * ========================================================================
 * ------------------------------------------------------------------------
 *
 * @author Edison
 * @version 1.0
 * <p>
 * ========================================================================
 * @date 2021/1/31 17:48
 * @email: wangzhijie0908@gmail.com
 */
@Data
public class MemberRegisterVo {

    @NotEmpty(message = "用户名称必须提交")
    private String userName;

    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$/", message =
            "手机号格式不正确")
    @NotEmpty(message = "手机号必须提交")
    private String phoneNum;

    @NotEmpty(message = "验证码必须填写")
    private String phoneCode;

    @NotEmpty(message = "密码不能为空")
    @Length(min =6, max = 18, message = "密码长度必须是在6~18位")
    private String password;

}
