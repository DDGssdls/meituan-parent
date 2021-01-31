package com.ddg.meituan.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author 
 * @email 
 * @date 2021-01-31 16:44:02
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

