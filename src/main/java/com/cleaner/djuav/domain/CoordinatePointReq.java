package com.cleaner.djuav.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "坐标点参数")
public class CoordinatePointReq implements Serializable {

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
    @Schema(description = "高度", required = true)
    private Double height;

}