package com.star.entity;

import lombok.*;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-02-09 22:51
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Animal {
    private String name;
    private int age;
}
