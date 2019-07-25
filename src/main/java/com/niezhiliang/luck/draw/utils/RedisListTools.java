package com.niezhiliang.luck.draw.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @GitHub https://github.com/niezhiliang
 * @Date 2019/07/24 12:33
 */
@Component
@Slf4j
public class RedisListTools<T>  {

    @Resource
    private ListOperations<String,T> listOperations;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 移除左边第一个元素
     * @param key
     * @return
     */
    public T rightPop(String key) {
        return listOperations.rightPop(key);
    }

    /**
     * list末尾加上一个元素
     * @param key
     * @param value
     */
    public void leftPush(String key,T value) {
        listOperations.leftPush(key,value);
    }

    /**
     * list末尾加上一系列元素
     * @param key
     * @param value
     */
    public void leftPushList(String key, List<T> value) {
        listOperations.leftPushAll(key,value);
    }

    /**
     * 获取左边第一个元素并加到list末尾
     * @param key
     * @return
     */
    public T getAll (String key) {
        return listOperations.rightPopAndLeftPush(key,key);
    }

    /**
     * 通过下标获取值
     * @param key
     * @param index
     * @return
     */
    public T getIndexValue(String key,Integer index) {
       return listOperations.index(key, index);
    }


    /**
     * 获取当前集合的大小
     * @param key
     * @return
     */
    public Long getListSize(String key) {
        return listOperations.size(key);
    }

    /**
     * 计数器自增
     * @param key
     * @return
     */
    public Long increCount(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();
        log.info("累加后的值为:{}",increment);
        return increment;
    }

    /**
     * 初始化计数器的值
     * @param key
     * @return
     */
    public Long initIncreCount(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        entityIdCounter.set(0l);
        Long increment = entityIdCounter.getAndIncrement();
        log.info("累加后的值为:{}",increment);
        return increment;
    }



    /**
     * 删除集合中的所有值为value中的左边第一个
     * @param key
     * @param value
     */
    public void removeFirstValue(String key,String value) {
        listOperations.remove(key,1,value);
    }

    /**
     * 删除list集合
     * @param key
     */
    public void delete(String key) {
        //保留集合中索引0，0之间的值，其余全部删除  所以list只有有一个值存在
        listOperations.trim(key,0,0);
        //将list中的剩余的一个值也删除
        listOperations.leftPop(key);
    }


}