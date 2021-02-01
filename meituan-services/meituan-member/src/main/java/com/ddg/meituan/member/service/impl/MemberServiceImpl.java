package com.ddg.meituan.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.ddg.meituan.common.exception.MeituanCodeEnum;
import com.ddg.meituan.common.exception.MeituanSysException;
import com.ddg.meituan.common.utils.R;
import com.ddg.meituan.member.constant.MemberConstant;
import com.ddg.meituan.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ddg.meituan.common.utils.PageUtils;
import com.ddg.meituan.common.utils.Query;

import com.ddg.meituan.member.dao.MemberDao;
import com.ddg.meituan.member.entity.MemberEntity;
import com.ddg.meituan.member.service.MemberService;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberDao memberDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R register(MemberRegisterVo memberRegisterVo) throws MeituanSysException {
        MemberEntity memberEntity = new MemberEntity();
        // 进行注册：
        // 检查用户名称 手机号是否是唯一：
        String username = memberRegisterVo.getUserName();
        String phoneNum = memberRegisterVo.getPhoneNum();
        String password = memberRegisterVo.getPassword();
        // 密码 和手机号进行加密
        String digestPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        String digestPhoneNum = DigestUtils.md5DigestAsHex(phoneNum.getBytes());
        boolean isUnique = checkPhoneNum(digestPhoneNum);
        if (isUnique) {
            memberEntity.setUsername(username);
            memberEntity.setPassword(digestPassword);
            memberEntity.setMobile(digestPhoneNum);
            int insert = memberDao.insert(memberEntity);
            return R.ok().put("register", insert);
        } else {
            throw new MeituanSysException("手机号或者是用户名称已占用！");
        }
    }

    @Override
    public boolean checkPhoneNum(String phoneNum) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(MemberConstant.MOBILE_COLUMN, phoneNum);
        Integer count = memberDao.selectCount(wrapper);
        return count == 0;
    }

    @Override
    public boolean checkUsername(String username) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(MemberConstant.USERNAME_COLUMN, username);
        Integer count = memberDao.selectCount(wrapper);
        return count == 0;
    }


    @Override
    public R login(MemberRegisterVo memberRegisterVo) throws MeituanSysException {
        String username = memberRegisterVo.getUserName();
        String phoneCode = memberRegisterVo.getPhoneCode();
        String password = memberRegisterVo.getPassword();

        if (!StringUtils.isEmpty(phoneCode)) {
            // 要是传入的有验证码 使用手机号进行登录的
            String phoneCodeFromRedis = redisTemplate.opsForValue()
                    .get(MemberConstant.REDIS_PHONE_CODE_PREFIX + username);
            if (!StringUtils.isEmpty(phoneCodeFromRedis)) {
                phoneCodeFromRedis = phoneCodeFromRedis.split("_")[0];
            } else {
                if (MemberConstant.PHONE_CODE_MOCK.equals(phoneCode)) {
                    phoneCodeFromRedis = phoneCode;
                }
            }
            if (phoneCode.equals(phoneCodeFromRedis)) {
                // 验证成功 能进行登录：可以将登录的用户信息存储到 redis 中 这里的username 是手机号
                // 使用手机号进行查询
                QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
                String phoneNum = DigestUtils.md5DigestAsHex(username.getBytes());
                wrapper.eq(MemberConstant.MOBILE_COLUMN, phoneNum);
                MemberEntity memberEntity = memberDao.selectOne(wrapper);
                if (memberEntity == null) {
                    // 没有直接进行注册：
                    memberRegisterVo.setUserName(MemberConstant.MEITUAN_USERNAME_PREFIX + phoneCode);
                    memberRegisterVo.setPhoneNum(phoneNum);
                    register(memberRegisterVo);
                } else {
                    memberRegisterVo.setUserName(memberEntity.getUsername());
                    memberRegisterVo.setPhoneNum(memberEntity.getMobile());
                }

                redisTemplate.opsForHash().put(MemberConstant.REDIS_CACHE_LOGIN_USER_KEY, username,
                        JSON.toJSONString(memberRegisterVo));

                // 将验证码删除
                redisTemplate.delete(MemberConstant.REDIS_PHONE_CODE_PREFIX + username);
                return R.ok().put("login", memberRegisterVo);
            } else {
                R.error().put("login", "验证码失效！");
            }

        } else {
            // 使用账号密码进行登录的
            QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
            // 手机号 和 密码进行加密
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            //
            String usernameOrPhoneNum = DigestUtils.md5DigestAsHex(username.getBytes());
            wrapper.eq(MemberConstant.PASSWORD_COLUMN, password);

            wrapper.and(quarreler -> quarreler.eq(MemberConstant.MOBILE_COLUMN, usernameOrPhoneNum)
                    .or().eq(MemberConstant.USERNAME_COLUMN, usernameOrPhoneNum));

            MemberEntity memberEntity = memberDao.selectOne(wrapper);
            if (memberEntity != null) {
                // 同样的能就能进行登录：将用户信息进行缓存
                memberRegisterVo.setUserName(memberEntity.getUsername());
                memberRegisterVo.setPhoneNum(memberEntity.getMobile());
                memberRegisterVo.setPassword(memberEntity.getPassword());
                redisTemplate.opsForHash().put(MemberConstant.REDIS_CACHE_LOGIN_USER_KEY, username,
                        JSON.toJSONString(memberRegisterVo));
                return R.ok().put("login", memberRegisterVo);
            }
        }
        return R.error().put("login", "账号或者是密码不正确！");

    }

}