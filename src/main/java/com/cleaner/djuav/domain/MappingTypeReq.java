package com.cleaner.djuav.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
/**
 * Author:Cleaner
 * Date: 2024/12/22 10:46
 **/
@Data
@Schema(description = """
        建图参数
        
        collectionMethod字段可选值:
        - "ortho": 正射采集
        - "inclined": 倾斜采集
        
        lensType字段可选值:
        - "lidar": 激光
        - "camera": 可见光
        
        shootType字段可选值:
        - "time": 等时间拍照
        - "distance": 等间隔拍照
        """)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class MappingTypeReq implements Serializable {

    /**
     * 采集方式
     *
     * 可选值:
     * - "ortho": 正射采集
     * - "inclined": 倾斜采集
     */
    @Schema(description = """
            采集方式
            
            可选值:
            - "ortho": 正射采集
            - "inclined": 倾斜采集
            """, required = true)
    private String collectionMethod;

    /**
     * 镜头类型
     *
     * 可选值:
     * - "lidar": 激光
     * - "camera": 可见光
     */
    @Schema(description = """
            镜头类型
            
            可选值:
            - "lidar": 激光
            - "camera": 可见光
            """, required = true)
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
     *
     * 可选值:
     * - "time": 等时间拍照
     * - "distance": 等间隔拍照
     */
    @Schema(description = """
            拍照模式
            
            可选值:
            - "time": 等时间拍照
            - "distance": 等间隔拍照
            """)
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
     *
     * CoordinatePointReq包含:
     * - longitude: 经度
     * - latitude: 纬度
     * - height: 高度
     */
    @Schema(description = """
            测区多边形坐标
            
            CoordinatePointReq包含:
            - longitude: 经度
            - latitude: 纬度
            - height: 高度
            """, required = true)
    private List<CoordinatePointReq> coordinates;


}