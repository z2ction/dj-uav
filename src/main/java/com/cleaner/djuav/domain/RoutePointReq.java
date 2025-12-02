package com.cleaner.djuav.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "航点信息")
public class RoutePointReq implements Serializable {

    /**
     * 航点编号
     */
    @Schema(description = "航点编号", required = true)
    private Integer routePointIndex;

    /**
         * 经度
     */
    @Schema(description = "经度", required = true)
    private Double longitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度", required = true)
    private Double latitude;

    /**
     * 高度
     */
    @Schema(description = "高度(米)", required = true)
    private Double height;

    /**
     * 飞行速度
     */
    @Schema(description = "飞行速度(m/s)")
    private Double speed;

    /**
     * 航点偏航角
     */
    @Schema(description = "航点偏航角")
    private WaypointHeadingReq waypointHeadingReq;

    /**
     * 航点转弯模式
     */
    @Schema(description = "航点转弯模式")
    private WaypointTurnReq waypointTurnReq;

    /**
     * 航点云台俯仰角
     */
    @Schema(description = "航点云台俯仰角(-90~0度)")
    private Double gimbalPitchAngle;

    /**
     * 航点动作列表
     */
    @Schema(description = "航点动作列表")
    private List<ActionGroupReq> actionGroupList;

}