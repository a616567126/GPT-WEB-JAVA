package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class FlagStudioImgCreateReq {

    @ApiModelProperty("调用/v1/getUploadLink后，返回的filename字段值")
    @NotNull(message = "图片名不能为空")
    private String filename;

    @ApiModelProperty("文字描述")
    private String prompt;

    @ApiModelProperty("通过设置“Guidance_scale>1”启用制导比例。较高的引导比例鼓励生成与文本“提示”密切相关的图像，通常以较低的图像质量为代价。\n" +
            "默认值：7.5 范围：float32[1，20]，步长0.5.")
    private Float guidanceScale;

    @ApiModelProperty("生成图片的高度，可选，默认512。宽*高不能超过768*768=589824")
    @Max(value = 768)
    private Integer height = 512;

    @ApiModelProperty("否定提示词")
    private String negativePrompts;

    @ApiModelProperty("支持的采样器有: ddim, lmsd, pndm, euler_d, euler_a_d, dpm.")
    private String sampler = "ddim";

    @ApiModelProperty("生成图片的宽度，可选，默认512。宽*高不能超过768*768=589824")
    @Max(value = 768)
    private Integer width = 512;

    @ApiModelProperty("随机种子的值，范围从 0 到4294967295")
    private Long seed = -0L;

    @ApiModelProperty("去噪的步骤数。更多的steps会获得更高质量的图像。默认值: 50 取值范围：[10, 100]")
    @NotNull(message = "步数不能为空")
    private Integer steps = 50;

    @ApiModelProperty("生成图片的风格配置。支持的风格有：\n" +
            "[国画, 写实主义, 虚幻引擎, 黑白插画, 版绘, 低聚, 工业霓虹, 电影艺术, 史诗大片, 暗黑, 涂鸦, 漫画场景, 特写, 儿童画, 油画, 水彩画, 素描, 卡通画, 浮世绘, 赛博朋克, 吉卜力, 哑光, 现代中式, 相机, CG渲染, 动漫, 霓虹游戏, 蒸汽波, 宝可梦, 火影忍者, 圣诞老人, 个人特效, 通用漫画, Momoko, MJ风格, 剪纸, 齐白石, 张大千, 丰子恺, 毕加索, 梵高, 塞尚, 莫奈, 马克·夏加尔, 丢勒, 米开朗基罗, 高更, 爱德华·蒙克, 托马斯·科尔, 安迪·霍尔, 新海诚, 倪传婧, 村上隆, 黄光剑, 吴冠中, 林风眠, 木内达朗, 萨雷尔, 杜拉克, 比利宾, 布拉德利, 普罗旺森, 莫比乌斯, 格里斯利, 比普, 卡尔·西松, 玛丽·布莱尔, 埃里克·卡尔, 扎哈·哈迪德, 包豪斯, 英格尔斯, RHADS, 阿泰·盖兰, 俊西, 坎皮恩, 德尚鲍尔, 库沙特, 雷诺阿]\n" +
            "默认值: null")
    private String style = null;

    @ApiModelProperty("默认值为2，设置1代表禁用")
    private Integer upsample = 2;

    @ApiModelProperty("使用controlnet渲染图片，可取的值有：depth, depth-from-image, canny, canny-from-image, hed, hed-from-image, mlsd, mlsd-from-image, normal, normal-from-image, pose, pose-from-image, scribble, scribble-from-image, seg, seg-from-image")
    private Integer controlnetTask = 2;



}
