package com.intelligent.bot.model.req.stablestudio;

import lombok.Data;

import javax.validation.constraints.Max;
import java.util.List;

@Data
public class TextToImageReq {
    /**
     * 高度默认512
     */
    private Integer height = 512;

    /**
     * 宽度默认512
     */
    private Integer width = 512;

    /**
     * 描述数组
     */
    List<TextPrompt> textPrompts;

    /**
     * clg比例
     * 数字( CfgScale ) [ 0 .. 35 ]
     * 默认值： 7
     * 扩散过程如何严格遵守提示文本（较高的值使您的图像更接近您的提示）
     */
    private Double cfgScale = 7d;

    /**
     * 剪辑_指导_重置
     * FAST_BLUEb 快速蓝色
     * FAST_GREEN 快速绿色
     * NONE 无
     * SIMPLE 简单的
     * SLOW 缓慢
     * SLOWER 更慢的
     * SLOWEST 最慢的
     * 默认：无
     */
    private String clipGuidancePreset = "NONE";

    /**
     * 采样器
     * DDIM
     * DDPM
     * K_DPMPP_2M
     * K_DPMPP_2S_ANCESTRAL
     * K_DPM_2
     * K_DPM_2_ANCESTRAL
     * K_EULER K_EULER_ANCESTRAL
     * K_HEUN
     * K_LMS
     */
    private String sampler;

    /**
     * 样本
     * 0-10
     * 默认值：1
     */
    private Integer samples =1;

    /**
     * 种子
     * 0-4294967295
     * 默认值：0
     */
    @Max(4294967295L)
    private Long seed = 0L;

    /**
     * 步数
     * 10 -150
     * 默认值：50
     */
    @Max(150L)
    private Integer steps = 50;

    /**
     * 风格预设
     * 3d-model 3d 模型
     * analog-film 模拟电影
     * anime 动漫
     * cinematic  电影
     * comic-book 漫画书
     * digital-art 数字艺术
     * enhance 增强
     * fantasy-art 幻想艺术
     * isometric 等距
     * line-art 线艺术
     * low-poly 低多边形
     * modeling-compound 建模复合
     * neon-punk 霓虹朋克
     * origami 折纸
     * photographic 摄影
     * pixel-art 像素艺术
     * tile-texture 平铺纹理
     */
    private String stylePreset;

    /**
     * 引擎id
     */
    private String engineId;

}

@Data
class TextPrompt{

    /**
     * 描述
     */
    private String text;

    /**
     *权重
     */
    private Double weight;
}
