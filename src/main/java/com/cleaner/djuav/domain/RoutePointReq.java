package com.cleaner.djuav.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "航点信息")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
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
     * waypointHeadingMode字段可选值:
     * - "followWayline": 沿航线方向。飞行器机头沿着航线方向飞至下一航点
     * - "manually": 手动控制。飞行器在飞至下一航点的过程中，用户可以手动控制飞行器机头朝向
     * - "fixed": 锁定当前偏航角。飞行器机头保持执行完航点动作后的飞行器偏航角飞至下一航点
     * - "smoothTransition": 自定义。通过waypointHeadingAngle给定某航点的目标偏航角，并在航段飞行过程中均匀过渡至下一航点的目标偏航角
     * - "towardPOI": 朝向兴趣点
     */
    @Schema(description = "航点偏航角设置")
    private WaypointHeadingReq waypointHeadingReq;

    /**
     * 航点转弯模式
     * waypointTurnMode字段可选值:
     * - "coordinateTurn": 协调转弯，不过点，提前转弯
     * - "toPointAndStopWithDiscontinuityCurvature": 直线飞行，飞行器到点停
     * - "toPointAndStopWithContinuityCurvature": 曲线飞行，飞行器到点停
     * - "toPointAndPassWithContinuityCurvature": 曲线飞行，飞行器过点不停
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
     *
     * actionTriggerType字段可选值:
     * - "reachPoint": 到达航点时执行
     * - "betweenAdjacentPoints": 航段触发，均匀转云台
     * - "multipleTiming": 等时触发
     * - "multipleDistance": 等距触发
     *
     * PointActionReq动作参数说明:
     * 每次只能设置一种动作类型:
     * 1. 悬停等待(hoverTime)
     * 2. 飞行器偏航(aircraftHeading)
     * 3. 拍照(takePhotoType=0)
     * 4. 云台旋转(gimbalYawRotateAngle或gimbalPitchRotateAngle)
     * 5. 变焦(zoom)
     * 6. 全景拍照(takePhotoType=1)
     * 7. 开始录像(startRecord=true)
     * 8. 停止录像(stopRecord=true)
     */
    @Schema(description = """
            航点动作列表
            
            actionTriggerType字段可选值:
            - "reachPoint": 到达航点时执行
            - "betweenAdjacentPoints": 航段触发，均匀转云台
            - "multipleTiming": 等时触发
            - "multipleDistance": 等距触发
            
            PointActionReq动作参数说明:
            每次只能设置一种动作类型:
            1. 悬停等待(hoverTime)
            2. 飞行器偏航(aircraftHeading)
            3. 拍照(takePhotoType=0)
            4. 云台旋转(gimbalYawRotateAngle或gimbalPitchRotateAngle)
            5. 变焦(zoom)
            6. 全景拍照(takePhotoType=1)
            7. 开始录像(startRecord=true)
            8. 停止录像(stopRecord=true)
            """)
    private List<ActionGroupReq> actionGroupList;

}