package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("mj_task")
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 任务类型
     */
    @TableField("`action`")
    private TaskAction action;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 提示词-英文
     */
    private String promptEn;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 自定义参数
     */
    private String state;

    /**
     * 提交时间
     */
    private Long submitTime;

    /**
     * 开始执行时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long finishTime;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 任务状态
     */
    @TableField("`status`")
    private TaskStatus status = TaskStatus.NOT_START;


    /**
     * 任务进度
     */
    private String progress;

    /**
     * 失败原因
     */
    private String failReason;

    private String finalPrompt;

    /**
     * 回调地址
     */
    private String notifyHook;

    /**
     * 关联任务 id
     */
    private Long relatedTaskId;

    /**
     * 消息 id
     */
    private String messageId;

    /**
     * 消息 hash
     */
    private String messageHash;

    /**
     * 变换位置
     */
    @TableField("`index`")
    private Integer index;


    private String progressMessageId;

    private Integer flags;

    /**
     * 提交类型 1：web 2：公众号
     */
    private Integer subType;


    /**
     * 公开状态 0-公开、1-私有
     */
    private Integer publicStatus;

    private String nonce;


    @JsonIgnore
    private final transient Object lock = new Object();

    public void sleep() throws InterruptedException {
        synchronized (this.lock) {
            this.lock.wait();
        }
    }

    public void awake() {
        synchronized (this.lock) {
            this.lock.notifyAll();
        }
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.status = TaskStatus.SUBMITTED;
        this.progress = "0%";
    }

    public void success() {
        this.finishTime = System.currentTimeMillis();
        this.status = TaskStatus.SUCCESS;
        this.progress = "100%";
    }

    public void fail(String reason) {
        this.finishTime = System.currentTimeMillis();
        this.status = TaskStatus.FAILURE;
        this.failReason = reason;
        this.progress = "";
    }

}
