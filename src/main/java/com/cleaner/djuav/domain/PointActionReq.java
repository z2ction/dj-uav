package com.cleaner.djuav.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = """
        航点动作参数
        
        在这些动作参数中，每次只能设置一种动作类型:
        1. 悬停等待(hoverTime)
        2. 飞行器偏航(aircraftHeading)
        3. 拍照(takePhotoType=0)
        4. 云台旋转(gimbalYawRotateAngle或gimbalPitchRotateAngle)
        5. 变焦(zoom)
        6. 全景拍照(takePhotoType=1)
        7. 开始录像(startRecord=true)
        8. 停止录像(stopRecord=true)
        """)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class PointActionReq implements Serializable {

    /**
     * 动作编号
     */
    @Schema(description = "动作编号")
    private Integer actionIndex;

    /**
     * 飞行器悬停等待时间
     *
     * 动作类型: 悬停等待
     * 说明: 设置飞行器在当前位置悬停指定的时间(秒)
     */
    @Schema(description = """
            飞行器悬停等待时间(秒)
            
            动作类型: 悬停等待
            说明: 设置飞行器在当前位置悬停指定的时间(秒)
            """)
    private Double hoverTime;

    /**
     * 飞行器目标偏航角
     *
     * 动作类型: 飞行器偏航
     * 说明: 控制飞行器绕其垂直轴旋转到指定角度
     */
    @Schema(description = """
            飞行器目标偏航角
            
            动作类型: 飞行器偏航
            说明: 控制飞行器绕其垂直轴旋转到指定角度
            """)
    private Double aircraftHeading;

    /**
     * 普通拍照：0，全景拍照：1
     *
     * 动作类型: 拍照
     * 说明: 控制相机进行拍照
     * 可选值:
     * - 0: 普通拍照
     * - 1: 全景拍照
     */
    @Schema(description = """
            拍照类型: 0-普通拍照, 1-全景拍照
            
            动作类型: 拍照
            说明: 控制相机进行拍照
            可选值:
            - 0: 普通拍照
            - 1: 全景拍照
            """)
    private Integer takePhotoType;

    /**
     * 是否使用全局拍照模式 0：不使用 1：使用
     *
     * 说明: 控制是否使用全局设置的拍照模式
     * 可选值:
     * - 0: 不使用全局拍照模式
     * - 1: 使用全局拍照模式
     */
    @Schema(description = """
            是否使用全局拍照模式: 0-不使用, 1-使用
            
            说明: 控制是否使用全局设置的拍照模式
            可选值:
            - 0: 不使用全局拍照模式
            - 1: 使用全局拍照模式
            """)
    private Integer useGlobalImageFormat;

    /**
     * 拍照模式（字典）
     *
     * 说明: 指定具体的拍照模式，如"wide","zoom","ir"等
     */
    @Schema(description = """
            拍照模式
            
            说明: 指定具体的拍照模式，如"wide","zoom","ir"等
            """)
    private String imageFormat;


    /**
     * 云台偏航角
     *
     * 动作类型: 云台旋转
     * 说明: 控制云台绕其垂直轴旋转到指定角度(-180~180度)
     */
    @Schema(description = """
            云台偏航角
            
            动作类型: 云台旋转
            说明: 控制云台绕其垂直轴旋转到指定角度(-180~180度)
            """)
    private Double gimbalYawRotateAngle;

    /**
     * 云台俯仰角
     *
     * 动作类型: 云台旋转
     * 说明: 控制云台绕其水平轴旋转到指定角度(-90~0度)
     */
    @Schema(description = """
            云台俯仰角
            
            动作类型: 云台旋转
            说明: 控制云台绕其水平轴旋转到指定角度(-90~0度)
            """)
    private Double gimbalPitchRotateAngle;

    /**
     * 变焦焦距
     *
     * 动作类型: 变焦
     * 说明: 控制相机镜头变焦到指定焦距(mm)
     */
    @Schema(description = """
            变焦焦距
            
            动作类型: 变焦
            说明: 控制相机镜头变焦到指定焦距(mm)
            """)
    private Double zoom;

    /**
     * 开始录像
     *
     * 动作类型: 开始录像
     * 说明: 控制相机开始录像
     * 可选值:
     * - true: 开始录像
     */
    @Schema(description = """
            开始录像
            
            动作类型: 开始录像
            说明: 控制相机开始录像
            可选值:
            - true: 开始录像
            """)
    private Boolean startRecord;

    /**
     * 停止录像
     *
     * 动作类型: 停止录像
     * 说明: 控制相机停止录像
     * 可选值:
     * - true: 停止录像
     */
    @Schema(description = """
            停止录像
            
            动作类型: 停止录像
            说明: 控制相机停止录像
            可选值:
            - true: 停止录像
            """)
    private Boolean stopRecord;

}