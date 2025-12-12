package com.cleaner.djuav.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = """
        动作组参数
        
        actionTriggerType字段可选值:
        - "reachPoint": 到达航点时执行
        - "betweenAdjacentPoints": 航段触发，均匀转云台
        - "multipleTiming": 等时触发
        - "multipleDistance": 等距触发
        """)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class ActionGroupReq implements Serializable {

    /**
     * 动作组ID
     */
    @Schema(description = "动作组ID")
    private Integer actionGroupId;

    /**
     * 动作组起始索引
     */
    @Schema(description = "动作组起始索引")
    private Integer actionGroupStartIndex;

    /**
     * 动作组结束索引
     */
    @Schema(description = "动作组结束索引")
    private Integer actionGroupEndIndex;

    /**
     * 动作触发类型
     *
     * 可选值:
     * - "reachPoint": 到达航点时执行
     * - "betweenAdjacentPoints": 航段触发，均匀转云台
     * - "multipleTiming": 等时触发
     * - "multipleDistance": 等距触发
     */
    @Schema(description = """
            动作触发类型
            
            可选值:
            - "reachPoint": 到达航点时执行
            - "betweenAdjacentPoints": 航段触发，均匀转云台
            - "multipleTiming": 等时触发
            - "multipleDistance": 等距触发
            """)
    private String actionTriggerType;

    /**
     * 动作触发参数
     */
    @Schema(description = "动作触发参数")
    private Double actionTriggerParam;

    /**
     * 动作列表
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
            动作列表
            
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
            """ ,requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PointActionReq> actions;

}