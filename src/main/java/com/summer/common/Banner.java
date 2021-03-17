package com.summer.common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/*
打印启动的Banner
 */
public class Banner {
    // banner made by https://www.bootschool.net/ascii
    public static final String DEFAULT_BANNER_NAME = "default-banner.txt";
    public static final String CUSTOM_BANNER_NAME = "banner.txt";

    public static  void print(){
        URL url = Thread.currentThread().getContextClassLoader().getResource(CUSTOM_BANNER_NAME);
        if (url != null) {
            try{
                Path path = Paths.get(url.toURI());
                Files.lines(path).forEach(System.out::println);
            } catch (URISyntaxException | IOException e) {}
        } else {
            url = Thread.currentThread().getContextClassLoader().getResource(DEFAULT_BANNER_NAME);
            try {
                Path path = Paths.get(url.toURI());
                Files.lines(path).forEach(System.out::println);
            } catch (URISyntaxException | IOException e){ }
        }

    }
}
