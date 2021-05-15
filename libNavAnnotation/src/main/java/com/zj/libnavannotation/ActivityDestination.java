package com.zj.libnavannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author 张锦
 */
@Target(ElementType.TYPE)
public @interface ActivityDestination {

    /**
     * 页面在路由中的路径
     *
     * @return 路由路径
     */
    String pageUrl();

    /**
     * 是否为默认启动的页面
     *
     * @return true 是
     */
    boolean asStarter() default false;

    /**
     * 是否需要登录
     *
     * @return true 是
     */
    boolean needLogin() default false;

}
