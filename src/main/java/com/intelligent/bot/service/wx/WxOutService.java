package com.intelligent.bot.service.wx;

import me.chanjar.weixin.common.error.WxErrorException;

public interface WxOutService {

    /**
     * 绑定
     * @param content
     * @return
     */
    String bind (String content,String fromUser);

    /**
     * 开通
     * @param content
     * @return
     */
    String opened (String content,String fromUser);

    /**
     * 查询
     * @param content
     * @return
     */
    String query (String content,String fromUser);

    /**
     * 菜单
     * @param content
     * @return
     */
    String menu (String content,String fromUser);

    /**
     * 兑换
     * @param content
     * @return
     */
    String exchange (String content,String fromUser);

    /**
     * 画画
     * @param content
     * @return
     */
    String draw (String content,String fromUser);

    /**
     * 咒语解析
     * @param content
     * @param fromUser
     * @return
     */
    String spellAnalysis (String content,String fromUser);

    /**
     * 画画变换
     * @param content
     * @return
     */
    String paintingChanges (String content,String fromUser);

    /**
     * 画画查询进度
     * @param content
     * @return
     */
    String queryProgress (String content,String fromUser) throws WxErrorException;

    /**
     * 迁移
     * @param content
     * @return
     */
    String migrate (String content,String fromUser);

    /**
     * 修改密码
     * @param content
     * @return
     */
    String changePassword (String content,String fromUser);

    /**
     * 重置密码
     * @param content
     * @return
     */
    String resetPassword (String content,String fromUser);

    /**
     * 功能
     * @param content
     * @return
     */
    String function (String content,String fromUser);

    /**
     * 卡密
     * @param
     * @return
     */
    void cardPin (String fromUser) throws WxErrorException;

    /**
     * 充值
     * @param content
     * @return
     */
    String recharge (String content,String fromUser) throws Exception;


}
