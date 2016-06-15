package com.jnhyxx.umenglibrary;

import com.umeng.socialize.PlatformConfig;

public class UmengLib {

    public static void init() {
        //各个平台的配置，建议放在全局Application或者程序入口

        //微信    wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
        PlatformConfig.setWeixin("wx1de55e278c2a76db", "22632d783963e97c1446f08851932962");
        //豆瓣RENREN平台目前只能在服务器端配置
        //新浪微博
        PlatformConfig.setSinaWeibo("546184142", "ae5b3c3c2cc18ab7d4b5a451c10aef29");
        //易信
        //PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
        PlatformConfig.setQQZone("1105469310", "DcEYIGxqIN07krsr");
        //PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
        //PlatformConfig.setAlipay("2015111700822536");
        //PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
        //PlatformConfig.setPinterest("1439206");
    }
}
