package com.cleaner.djuav.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "动作组参数")
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
     */
    @Schema(description = "动作触发类型")
    private String actionTriggerType;

    /**
     * 动作触发参数
     */
    @Schema(description = "动作触发参数")
    private Double actionTriggerParam;

    /**
     * 动作列表
     */
    @Schema(description = "动作列表")
    private List<PointActionReq> actions;

}