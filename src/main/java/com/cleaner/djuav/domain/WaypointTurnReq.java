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
        航点转弯设置
        
        waypointTurnMode字段可选值:
        - "coordinateTurn": 协调转弯，不过点，提前转弯
        - "toPointAndStopWithDiscontinuityCurvature": 直线飞行，飞行器到点停
        - "toPointAndStopWithContinuityCurvature": 曲线飞行，飞行器到点停
        - "toPointAndPassWithContinuityCurvature": 曲线飞行，飞行器过点不停
        """)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class WaypointTurnReq implements Serializable {

    /**
     * 航点转弯模式
     *
     * 可选值:
     * - "coordinateTurn": 协调转弯，不过点，提前转弯
     * - "toPointAndStopWithDiscontinuityCurvature": 直线飞行，飞行器到点停
     * - "toPointAndStopWithContinuityCurvature": 曲线飞行，飞行器到点停
     * - "toPointAndPassWithContinuityCurvature": 曲线飞行，飞行器过点不停
     */
    @Schema(description = """
            航点转弯模式
            
            可选值:
            - "coordinateTurn": 协调转弯，不过点，提前转弯
            - "toPointAndStopWithDiscontinuityCurvature": 直线飞行，飞行器到点停
            - "toPointAndStopWithContinuityCurvature": 曲线飞行，飞行器到点停
            - "toPointAndPassWithContinuityCurvature": 曲线飞行，飞行器过点不停
            """, required = true)
    private String waypointTurnMode;

    /**
     * 航点转弯截距
     *
     * 说明: 当waypointTurnMode设置为"coordinateTurn"时使用，指定转弯的截距距离
     */
    @Schema(description = """
            航点转弯截距
            
            说明: 当waypointTurnMode设置为"coordinateTurn"时使用，指定转弯的截距距离
            """)
    private Double waypointTurnDampingDist;

    /**
     * 该航段是否贴合直线
     *
     * 说明: 控制航段是否贴合直线飞行
     * 可选值:
     * - 0: 不贴合直线
     * - 1: 贴合直线
     */
    @Schema(description = """
            该航段是否贴合直线
            
            说明: 控制航段是否贴合直线飞行
            可选值:
            - 0: 不贴合直线
            - 1: 贴合直线
            """)
    private Integer useStraightLine;


}
