package com.star.serviceimpl;

import com.star.annotation.RpcService;
import com.star.entity.Animal;
import com.star.services.AnimalService;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-10 23:12
 */
@Slf4j
public class AnimalServiceImpl2 implements AnimalService {
    static {
        System.out.println("AnimalServiceImpl2被创建");
    }

    @Override
    public String introduce(Animal animal) {
        log.info("AnimalServiceImpl收到: {}.", animal.getName());
        String result = animal.getName() + " age is " + animal.getAge();
        log.info("AnimalServiceImpl返回: {}.", result);
        return result;
    }
}
