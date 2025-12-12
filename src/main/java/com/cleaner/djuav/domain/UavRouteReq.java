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
@Schema(description = "无人机航线请求参数")
public class UavRouteReq implements Serializable {

    /**
     * 航线类型
     * 对应枚举: TemplateTypeEnums
     * 可选值:
     * - "waypoint" 航点飞行
     * - "mapping2d" 建图航拍
     * - "mapping3d" 倾斜摄影
     * - "mappingStrip" 航带飞行
     */
    @Schema(description = "航线类型: waypoint-航点飞行, mapping2d-建图航拍, mapping3d-倾斜摄影, mappingStrip-航带飞行", required = true)
    private String templateType;

    /**
     * 无人机类型
     * 对应枚举: DroneEnumValueEnums
     * 可选值:
     * - 89 M350 RTK
     * - 60 M300 RTK
     * - 67 M30/M30T
     * - 77 M3E/M3T/M3M
     * - 91 M3D/M3TD
     * - 100 M4D/M4TD
     */
    @Schema(description = "无人机类型: 89-M350 RTK, 60-M300 RTK, 67-M30/M30T, 77-M3E/M3T/M3M, 91-M3D/M3TD, 100-M4D/M4TD", required = true)
    private Integer droneType;

    /**
     * 无人机子类型
     * 根据droneType的不同而不同
     */
    @Schema(description = "无人机子类型，根据droneType的不同而不同")
    private Integer subDroneType;

    /**
     * 负载类型
     * 对应枚举: PayloadTypeEnums
     * 可选值:
     * - 42 H20
     * - 43 H20T
     * - 52 M30双光相机
     * - 53 M30T三光相机
     * - 61 H20N
     * - 66 Mavic 3E 相机
     * - 67 Mavic 3T 相机
     * - 68 Mavic 3M 相机
     * - 80 Matrice 3D 相机
     * - 81 Matrice 3TD 相机
     * - 65534 PSDK 负载
     * - 82 H30
     * - 83 H30T
     * - 99 M4TD 相机
     */
    @Schema(description = "负载类型: 42-H20, 43-H20T, 52-M30双光相机, 53-M30T三光相机, 61-H20N, 66-Mavic 3E 相机, 67-Mavic 3T 相机, 68-Mavic 3M 相机, 80-Matrice 3D 相机, 81-Matrice 3TD 相机, 65534-PSDK 负载, 82-H30, 83-H30T ,99 M4TD相机", required = true)
    private Integer payloadType;

    /**
     * 负载挂载位置
     * 对应枚举: PayloadPositionIndexEnums
     * 可选值:
     * - 0 飞行器1号挂载位置。M300 RTK，M350 RTK机型，对应机身左前方。其它机型，对应主云台
     * - 1 飞行器2号挂载位置。M300 RTK，M350 RTK机型，对应机身右前方
     * - 2 飞行器3号挂载位置。M300 RTK，M350 RTK机型，对应机身上方
     */
    @Schema(description = "负载挂载位置: 0-1号挂载位置(左前方), 1-2号挂载位置(右前方), 2-3号挂载位置(上方)", required = true)
    private Integer payloadPosition;

    /**
     * 负载图片存储类型
     * 对应枚举: ImageFormatEnums
     * 可选值:
     * - "wide" 存储广角镜头照片
     * - "zoom" 存储变焦镜头照片
     * - "ir" 存储红外镜头照片
     * - "narrow_band" 存储窄带镜头拍摄照片
     * - "visable" 可见光照片
     */
    @Schema(description = "负载图片存储类型: wide-广角镜头照片, zoom-变焦镜头照片, ir-红外镜头照片, narrow_band-窄带镜头照片, visable-可见光照片", required = true)
    private String imageFormat;

    /**
     * 航线结束动作
     * 对应枚举: FinishActionEnums
     * 可选值:
     * - "goHome" 飞行器完成航线任务后，退出航线模式并返航
     * - "noAction" 飞行器完成航线任务后，退出航线模式
     * - "autoLand" 飞行器完成航线任务后，退出航线模式并原地降落
     * - "gotoFirstWaypoint" 飞行器完成航线任务后，立即飞向航线起始点，到达后退出航线模式
     */
    @Schema(description = "航线结束动作: goHome-返航, noAction-无动作, autoLand-自动降落, gotoFirstWaypoint-飞向第一个航点")
    private String finishAction;

    /**
     * 失控动作
     * 对应枚举: ExecuteRCLostActionEnums
     * 可选值:
     * - "goBack" 返航
     * - "landing" 降落
     * - "hover" 悬停
     */
    @Schema(description = "失控动作: goBack-返航, landing-降落, hover-悬停")
    private String exitOnRcLostAction;

    /**
     * 全局航线高度
     * 单位: 米
     */
    @Schema(description = "全局航线高度(米)", required = true)
    private Double globalHeight;

    /**
     * 全局航线飞行速度
     * 单位: m/s
     */
    @Schema(description = "全局航线飞行速度(m/s)", required = true)
    private Double autoFlightSpeed;

    /**
     * 全局偏航角模式
     * 对应类: WaypointHeadingReq
     * waypointHeadingMode字段对应枚举: WaypointHeadingModeEnums
     * 可选值:
     * - "followWayline" 沿航线方向。飞行器机头沿着航线方向飞至下一航点
     * - "manually" 手动控制。飞行器在飞至下一航点的过程中，用户可以手动控制飞行器机头朝向
     * - "fixed" 锁定当前偏航角。飞行器机头保持执行完航点动作后的飞行器偏航角飞至下一航点
     * - "smoothTransition" 自定义。通过waypointHeadingAngle给定某航点的目标偏航角，并在航段飞行过程中均匀过渡至下一航点的目标偏航角
     * - "towardPOI" 朝向兴趣点
     */
    @Schema(description = "全局偏航角模式", required = true)
    private WaypointHeadingReq waypointHeadingReq;

    /**
     * 全局航点转弯模式
     * 对应类: WaypointTurnReq
     * waypointTurnMode字段对应枚举: GlobalWaypointTurnModeEnums
     * 可选值:
     * - "coordinateTurn" 协调转弯，不过点，提前转弯
     * - "toPointAndStopWithDiscontinuityCurvature" 直线飞行，飞行器到点停
     * - "toPointAndStopWithContinuityCurvature" 曲线飞行，飞行器到点停
     * - "toPointAndPassWithContinuityCurvature" 曲线飞行，飞行器过点不停
     */
    @Schema(description = "全局航点转弯模式", required = true)
    private WaypointTurnReq waypointTurnReq;

    /**
     * 云台俯仰角控制模式
     * 对应枚举: GimbalPitchModeEnums
     * 可选值:
     * - "manual" 手动控制
     * - "usePointSetting" 依照每个航点设置
     */
    @Schema(description = "云台俯仰角控制模式: manual-手动控制, usePointSetting-依照每个航点设置", required = true)
    private String gimbalPitchMode;

    /**
     * 参考起飞点
     * 格式: 经度,纬度,高度 (如: "22.544556,113.943477,0")
     */
    @Schema(description = "参考起飞点，格式: 经度,纬度,高度 (如: \"22.544556,113.943477,0\")")
    private String takeOffRefPoint;

    /**
     * 航点列表
     * 对应类: RoutePointReq
     * 包含每个航点的详细信息:
     * - routePointIndex: 航点编号，从0开始的整数
     * - longitude: 经度，例如113.943477
     * - latitude: 纬度，例如22.544556
     * - height: 高度(米)
     * - speed: 飞行速度(m/s)
     * - waypointHeadingReq: 航点偏航角设置，参考WaypointHeadingReq类
     * - waypointTurnReq: 航点转弯模式设置，参考WaypointTurnReq类
     * - gimbalPitchAngle: 云台俯仰角(-90~0度)
     * - actionGroupList: 航点动作组列表
     */
    @Schema(description = "航点列表", required = true)
    private List<RoutePointReq> routePointList;

    /**
     * 建图航拍、倾斜摄影、航带飞行模板参数
     * 对应类: MappingTypeReq
     * 包含建图参数:
     * - collectionMethod: 采集方式，对应枚举CollectionMethodEnums
     *   可选值:
     *   - "ortho" 正射采集
     *   - "inclined" 倾斜采集
     * - lensType: 镜头类型，对应枚举LensTypeEnums
     *   可选值:
     *   - "lidar" 激光
     *   - "camera" 可见光
     * - overlapH: 航向重叠率(整数，如70表示70%)
     * - overlapW: 旁向重叠率(整数，如60表示60%)
     * - elevationOptimizeEnable: 是否开启高程优化，0关闭，1开启
     * - shootType: 拍照模式，对应枚举ShootTypeEnums
     *   可选值:
     *   - "time" 等时间拍照
     *   - "distance" 等间隔拍照
     * - direction: 航线方向(角度，如"90"表示东西向)
     * - margin: 测区外扩距离(米)
     * - coordinates: 测区多边形坐标点列表，包含经度、纬度、高度
     */
    @Schema(description = "建图航拍、倾斜摄影、航带飞行模板参数")
    private MappingTypeReq mappingTypeReq;

    /**
     * 航线初始动作列表
     * 对应类: PointActionReq
     * 包含起飞前执行的动作:
     * - actionIndex: 动作编号
     * - hoverTime: 悬停等待时间(秒)
     * - aircraftHeading: 飞行器目标偏航角(角度)
     * - takePhotoType: 拍照类型，0普通拍照，1全景拍照
     * - useGlobalImageFormat: 是否使用全局拍照模式，0不使用，1使用
     * - imageFormat: 拍照模式(如"wide","zoom","ir"等)
     * - gimbalYawRotateAngle: 云台偏航角(-180~180度)
     * - gimbalPitchRotateAngle: 云台俯仰角(-90~0度)
     * - zoom: 变焦焦距(mm)
     * - startRecord: 开始录像，布尔值
     * - stopRecord: 停止录像，布尔值
     *
     * 在这些动作参数中，每次只能设置一种动作类型:
     * 1. 悬停等待(hoverTime)
     * 2. 飞行器偏航(aircraftHeading)
     * 3. 拍照(takePhotoType=0)
     * 4. 云台旋转(gimbalYawRotateAngle或gimbalPitchRotateAngle)
     * 5. 变焦(zoom)
     * 6. 全景拍照(takePhotoType=1)
     * 7. 开始录像(startRecord=true)
     * 8. 停止录像(stopRecord=true)
     */
    @Schema(description = "航线初始动作列表")
    private List<PointActionReq> startActionList;

}