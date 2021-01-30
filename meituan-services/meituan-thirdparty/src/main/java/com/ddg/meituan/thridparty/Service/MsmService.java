package com.ddg.meituan.thridparty.Service;

import java.util.Map;

/**
 * Description:
 * ========================================================================
 * ------------------------------------------------------------------------
 *
 * @author Edison
 * @version 1.0
 * <p>
 * ========================================================================
 * @date 2021/1/30 17:58
 * @email: wangzhijie0908@gmail.com
 */
public interface MsmService {


    boolean sendCodeByAly(Map<String, String> param, String phoneNum);
}
