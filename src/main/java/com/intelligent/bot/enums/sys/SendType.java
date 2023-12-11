package com.intelligent.bot.enums.sys;

public enum SendType {

    GPT(1,"gpt"),
    GPT_OFFICIAL(2,"gpt-official"),
    SD(3,"sd"),

    FS(4,"fs"),

    MJ(5,"mj"),
    BING(6,"bing"),
    STABLE_STUDIO(7,"stableStudio"),
    GPT_4(8,"gpt-4"),
    SPARK_V2(9,"spark-v2"),
    SPARK_V3(10,"spark-v3");


    private Integer type;

    private String remark;

    SendType(Integer type,String remark){
        this.type = type;
        this.remark = remark;
    }

    public Integer getType(){
        return this.type;
    }

    public String getRemark(){
        return this.remark;
    }

}
