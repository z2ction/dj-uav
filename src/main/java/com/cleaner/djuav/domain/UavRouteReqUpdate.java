package com.cleaner.djuav.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Author:Cleaner
 * Date: 2024/12/22 10:46
 **/
@Data
@Schema(description = "无人机航线请求参数")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class UavRouteReqUpdate extends UavRouteReq implements Serializable {
    @Schema(description = "工作空间ID")
    String workspaceId;
    @Schema(description = "航线ID")
    String waylineId;
}
