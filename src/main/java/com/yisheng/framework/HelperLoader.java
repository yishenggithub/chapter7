package com.yisheng.framework;

import com.yisheng.framework.helper.BeanHelper;
import com.yisheng.framework.helper.ClassHelper;
import com.yisheng.framework.helper.ControllerHelper;
import com.yisheng.framework.helper.IocHelper;
import com.yisheng.framework.util.ClassUtil;

/**
 * Created by easom on 2017/3/27.
 */
public final class HelperLoader {
    public static void init(){
        Class<?>[]classList= {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for(Class<?>cls:classList){
            ClassUtil.loadClass(cls.getName(),true);
        }

    }
}
