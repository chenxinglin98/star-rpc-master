package com.star.extension;

import java.lang.annotation.*;

//给ExtensionLoader使用检测
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {
}
