package com.ddg.meituan.common.aspect;

import com.ddg.meituan.common.annotation.RedisCache;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description: 设置切面实现注解缓存
 * ========================================================================
 * ------------------------------------------------------------------------
 *
 * @author Edison
 * @version 1.0
 * <p>
 * ========================================================================
 * @date 2021/1/29 16:36
 * @email: wangzhijie0908@gmail.com
 */
@Slf4j
@Component
@Aspect
public class RedisCacheAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;  //注入redis模板
    // 默认不进行空值缓存
    @Value("${meituan.rediscache.cachenull:false}")
    private boolean cacheNull;

    @Pointcut("@annotation(com.ddg.meituan.common.annotation.RedisCache)") //定义切点
    public void redisCachePointCut() {
    }

    @Around("redisCachePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RedisCache redisCache = method.getAnnotation(RedisCache.class);
        try {
            if (redisCache != null) {
                // 注解上的描述
                Gson gson = new Gson();
                String redisKey = redisCache.redisKey();
                Integer timeOut = Integer.parseInt(redisCache.timeOut());
                Class resultObjClass = redisCache.resClass();
                boolean isList = redisCache.isList();
                String realValue = stringRedisTemplate.opsForValue().get(redisKey); //获取redis数据
                if (!StringUtils.isEmpty(realValue)) {
                    log.info("redisValue:{}", realValue);
                    if (isList) {//如果是list类型的返回数据
                        JsonParser jsonParser = new JsonParser();
                        JsonArray jsonArray = jsonParser.parse(realValue).getAsJsonArray();

                        List resultList = new ArrayList();
                        if (jsonArray != null) {
                            for (JsonElement element : jsonArray) {
                                resultList.add(gson.fromJson(element, resultObjClass));
                            }
                        }
                        return resultList;
                    }
                    return gson.fromJson(realValue, resultObjClass);
                } else {
                    Object result = point.proceed();
                    realValue = new Gson().toJson(result);
                    // 配置是否是可以缓存空值：要是 list类型的数据返回空List 返回的可能是"[]"
                    if (cacheNull || (!"[]".equals(realValue) && !StringUtils.isEmpty(realValue))){
                        stringRedisTemplate.opsForValue().set(redisKey, realValue, timeOut, TimeUnit.SECONDS);
                    }
                    return result;
                }
            }
        }catch (Exception e){
            log.error("redisCachePointCutError!", e.getMessage());
            return point.proceed();  //异常直接执行方法
        }
        return null;
    }
}
