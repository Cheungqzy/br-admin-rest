package com.brother.interceptor;

import java.lang.annotation.*;

/**
 * Created by Coldmoon on 2015/11/20.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessRequired {

}
