package com.cleaner.djuav.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "航点动作参数")
public class PointActionReq implements Serializable {

    /**
     * 动作编号
     */
    @Schema(description = "动作编号")
    private Integer actionIndex;

    /**
     * 飞行器悬停等待时间
     */
    @Schema(description = "飞行器悬停等待时间(秒)")
    private Double hoverTime;

    /**
     * 飞行器目标偏航角
     */
    @Schema(description = "飞行器目标偏航角")
    private Double aircraftHeading;

    /**
     * 普通拍照：0，全景拍照：1
     */
    @Schema(description = "拍照类型: 0-普通拍照, 1-全景拍照")
    private Integer takePhotoType;

    /**
     * 是否使用全局拍照模式 0：不使用 1：使用
     */
    @Schema(description = "是否使用全局拍照模式: 0-不使用, 1-使用")
    private Integer useGlobalImageFormat;

    /**
     * 拍照模式（字典）
     */
    @Schema(description = "拍照模式")
    private String imageFormat;


    /**
     * 云台偏航角
     */
    @Schema(description = "云台偏航角")
    private Double gimbalYawRotateAngle;

    /**
     * 云台俯仰角
     */
    @Schema(description = "云台俯仰角")
    private Double gimbalPitchRotateAngle;

    /**
     * 变焦焦距
     */
    @Schema(description = "变焦焦距")
    private Double zoom;

    /**
     * 开始录像
     */
    @Schema(description = "开始录像")
    private Boolean startRecord;

    /**
     * 停止录像
     */
    @Schema(description = "停止录像")
    private Boolean stopRecord;

}