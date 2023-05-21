package com.intelligent.bot.utils.sys;


import cn.hutool.core.util.RandomUtil;
import com.intelligent.bot.constant.CommonConst;

public class PasswordUtil {

    public static String getRandomPassword(){
        return  RandomUtil.randomString(CommonConst.CAPITAL + CommonConst.LOWERCASE_LETTERS + CommonConst.DIGIT,RandomUtil.randomInt(6, 10));

    }
}
