package com.star;

import com.star.annotation.RpcReference;
import com.star.entity.Animal;
import com.star.services.AnimalService;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-10 23:00
 */
@Component
public class AnimalController {
    @RpcReference(version = "version1", group = "test1")
    private AnimalService animalService;
    public void test() throws InterruptedException {
        String hello = this.animalService.introduce(new Animal("pig", 20));
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        //assert "pig age is 20".equals(hello);
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(animalService.introduce(new Animal("cat", 20)));
        }
    }
}
