package com.cleaner.djuav.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Author:Cleaner
 * Date: 2024/12/22 10:46
 **/
@Data
@Schema(description = "建图参数")
public class MappingTypeReq implements Serializable {

    /**
     * 采集方式
     */
    @Schema(description = "采集方式", required = true)
    private String collectionMethod;

    /**
     * 镜头类型
     */
    @Schema(description = "镜头类型", required = true)
    private String lensType;

    /**
     * 航向重叠率
     */
    @Schema(description = "航向重叠率", required = true)
    private Integer overlapH;

    /**
     * 旁向重叠率
     */
    @Schema(description = "旁向重叠率", required = true)
    private Integer overlapW;

    /**
     * 是否开启高程优化
     */
    @Schema(description = "是否开启高程优化")
    private Integer elevationOptimizeEnable;

    /**
     * 拍照模式
     */
    @Schema(description = "拍照模式")
    private String shootType;

    /**
     * 航线方向
     */
    @Schema(description = "航线方向")
    private String direction;

    /**
     * 测区外扩距离
     */
    @Schema(description = "测区外扩距离")
    private String margin;

    /**
     * 测区多边形坐标  经度,纬度,高度
     */
    @Schema(description = "测区多边形坐标", required = true)
    private List<CoordinatePointReq> coordinates;


}