package com.cleaner.djuav.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Author:Cleaner
 * Date: 2024/12/22 13:05
 **/
@Data
@Schema(description = """
        航点偏航角设置
        
        waypointHeadingMode字段可选值:
        - "followWayline": 沿航线方向。飞行器机头沿着航线方向飞至下一航点
        - "manually": 手动控制。飞行器在飞至下一航点的过程中，用户可以手动控制飞行器机头朝向
        - "fixed": 锁定当前偏航角。飞行器机头保持执行完航点动作后的飞行器偏航角飞至下一航点
        - "smoothTransition": 自定义。通过waypointHeadingAngle给定某航点的目标偏航角，并在航段飞行过程中均匀过渡至下一航点的目标偏航角
        - "towardPOI": 朝向兴趣点
        """)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class WaypointHeadingReq implements Serializable {

    /**
     * 偏航角模式
     *
     * 可选值:
     * - "followWayline": 沿航线方向。飞行器机头沿着航线方向飞至下一航点
     * - "manually": 手动控制。飞行器在飞至下一航点的过程中，用户可以手动控制飞行器机头朝向
     * - "fixed": 锁定当前偏航角。飞行器机头保持执行完航点动作后的飞行器偏航角飞至下一航点
     * - "smoothTransition": 自定义。通过waypointHeadingAngle给定某航点的目标偏航角，并在航段飞行过程中均匀过渡至下一航点的目标偏航角
     * - "towardPOI": 朝向兴趣点
     */
    @Schema(description = """
            偏航角模式
            
            可选值:
            - "followWayline": 沿航线方向。飞行器机头沿着航线方向飞至下一航点
            - "manually": 手动控制。飞行器在飞至下一航点的过程中，用户可以手动控制飞行器机头朝向
            - "fixed": 锁定当前偏航角。飞行器机头保持执行完航点动作后的飞行器偏航角飞至下一航点
            - "smoothTransition": 自定义。通过waypointHeadingAngle给定某航点的目标偏航角，并在航段飞行过程中均匀过渡至下一航点的目标偏航角
            - "towardPOI": 朝向兴趣点
            """, required = true)
    private String waypointHeadingMode;

    /**
     * 偏航角度
     *
     * 说明: 当waypointHeadingMode设置为"smoothTransition"时使用，指定航点的目标偏航角
     */
    @Schema(description = """
            偏航角度
            
            说明: 当waypointHeadingMode设置为"smoothTransition"时使用，指定航点的目标偏航角
            """)
    private Double waypointHeadingAngle;

    /**
     * 兴趣点
     *
     * 说明: 当waypointHeadingMode设置为"towardPOI"时使用，指定兴趣点的坐标位置
     */
    @Schema(description = """
            兴趣点
            
            说明: 当waypointHeadingMode设置为"towardPOI"时使用，指定兴趣点的坐标位置
            """)
    private String waypointPoiPoint;
}
