package com.cleaner.djuav.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Author:Cleaner
 * Date: 2024/12/22 13:05
 **/
@Data
@Schema(description = "航点转弯设置")
public class WaypointTurnReq implements Serializable {

    /**
     * 航点转弯模式
     */
    @Schema(description = "航点转弯模式", required = true)
    private String waypointTurnMode;

    /**
     * 航点转弯截距
     */
    @Schema(description = "航点转弯截距")
    private Double waypointTurnDampingDist;

    /**
     * 该航段是否贴合直线
     */
    @Schema(description = "该航段是否贴合直线")
    private Integer useStraightLine;


}