package com.cleaner.djuav.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "坐标点参数")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CoordinatePointReq implements Serializable {

    /**
     * 经度
     *
     * 说明: WGS84坐标系下的经度值，单位为度
     */
    @Schema(description = """
            经度
            
            说明: WGS84坐标系下的经度值，单位为度
            """, required = true)
    private Double longitude;

    /**
     * 纬度
     *
     * 说明: WGS84坐标系下的纬度值，单位为度
     */
    @Schema(description = """
            纬度
            
            说明: WGS84坐标系下的纬度值，单位为度
            """, required = true)
    private Double latitude;

    /**
     * 高度
     *
     * 说明: 相对于地面的高度值，单位为米
     */
    @Schema(description = """
            高度
            
            说明: 相对于地面的高度值，单位为米
            """, required = true)
    private Double height;

}