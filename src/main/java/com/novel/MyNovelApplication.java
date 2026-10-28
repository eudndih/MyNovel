package com.novel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.novel.mapper")
public class MyNovelApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyNovelApplication.class, args);
    }

}
