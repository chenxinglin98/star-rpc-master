package com.star;

import com.star.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.star"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        //通过注解扫描方式新建IOC容器对象,类似于springboot
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        AnimalController animalController = (AnimalController) applicationContext.getBean("animalController");
        animalController.test();
    }
}