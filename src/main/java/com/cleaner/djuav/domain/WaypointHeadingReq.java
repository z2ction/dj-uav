package com.cleaner.djuav.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Author:Cleaner
 * Date: 2024/12/22 13:05
 **/
@Data
@Schema(description = "航点偏航角设置")
public class WaypointHeadingReq implements Serializable {

    /**
     * 偏航角模式
     */
    @Schema(description = "偏航角模式", required = true)
    private String waypointHeadingMode;

    /**
     * 偏航角度
     */
    @Schema(description = "偏航角度")
    private Double waypointHeadingAngle;

    /**
     * 兴趣点
     */
    @Schema(description = "兴趣点")
    private String waypointPoiPoint;
}