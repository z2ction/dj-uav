package com.cleaner.djuav.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cleaner.djuav.constant.FileTypeConstants;
import com.cleaner.djuav.domain.*;
import com.cleaner.djuav.domain.kml.*;
import com.cleaner.djuav.enums.kml.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 航线文件操作工具类
 *
 * 该类主要负责处理无人机航线文件的生成和解析工作，包括：
 * 1. 将航线参数对象转换为符合DJI规范的KML/WPML XML格式
 * 2. 将XML数据打包成KMZ文件格式
 * 3. 解析KMZ文件中的航线信息
 *
 * 主要功能：
 * - buildKmz: 生成完整的KMZ航线文件
 * - parseKml: 解析KML/WPML文件内容
 * - buildKmlDocument: 构建KML文档结构
 * - buildKmlMissionConfig: 构建航线任务配置
 * - buildKmlFolder: 构建航线文件夹信息
 */
@Component
public class RouteFileUtils {

    /**
     * XML 头部
     */
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    /**
     * 生成的本地 kmz 文件存储路径
     */
    @Value("${wayline-file-save-path}")
    public String LOCAL_KMZ_FILE_PATH;


    /**
     * kml文件解析
     *
     * @param inputStream KML文件输入流
     * @return KmlInfo 解析后的KML信息对象
     */
    public static KmlInfo parseKml(InputStream inputStream) {
        // 创建XStream对象，用于XML与Java对象间的序列化和反序列化
        XStream xStream = new XStream();

        // 设置允许解析的类型，防止安全漏洞，只允许特定的类被解析
        xStream.allowTypes(new Class[]{KmlInfo.class, KmlAction.class, KmlWayLineCoordinateSysParam.class, KmlPoint.class, KmlActionGroup.class});

        // 为"kml"标签设置别名，对应KmlInfo类
        xStream.alias("kml", KmlInfo.class);

        // 处理KmlInfo类上的注解（如XStreamAlias等）
        xStream.processAnnotations(KmlInfo.class);

        // 启用自动检测注解功能
        xStream.autodetectAnnotations(true);

        // 忽略未知元素，提高解析容错性
        xStream.ignoreUnknownElements();

        // 为KmlActionGroup类的"action"属性添加隐式集合支持
        xStream.addImplicitCollection(KmlActionGroup.class, "action");

        // 为KmlPlacemark类的"actionGroup"属性添加隐式集合支持
        xStream.addImplicitCollection(KmlPlacemark.class, "actionGroup");

        // 从输入流中解析XML并转换为KmlInfo对象
        KmlInfo kmlInfo = (KmlInfo) xStream.fromXML(inputStream);

        // 返回解析后的KmlInfo对象
        return kmlInfo;
    }

    /**
     * 生成航线 KMZ 文件 (主入口方法)
     *
     * @param fileName  文件名
     * @param kmlParams 参数对象，包含航线的所有配置信息
     * @return 本地文件路径，指向生成的KMZ文件
     */
    public String buildKmz(String fileName, KmlParams kmlParams) {
        KmlInfo kmlInfo = buildKml(kmlParams);
        KmlInfo wpmlInfo = buildWpml(kmlParams);
        return buildKmz(fileName, kmlInfo, wpmlInfo);
    }

    /**
     * 生成航线 KMZ 文件 (底层实现方法)
     *
     * @param fileName 文件名
     * @param kmlInfo  kml 文件信息（基础配置）
     * @param wpmlInfo wpml 文件信息（详细航线）
     * @return 本地文件路径，指向生成的KMZ文件
     */
    public String buildKmz(String fileName, KmlInfo kmlInfo, KmlInfo wpmlInfo) {
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(KmlInfo.class);
        xStream.addImplicitCollection(KmlActionGroup.class, "action");
        xStream.addImplicitCollection(KmlPlacemark.class, "actionGroup");

        String kml = XML_HEADER + xStream.toXML(kmlInfo);
        String wpml = XML_HEADER + xStream.toXML(wpmlInfo);
        File file = new File(LOCAL_KMZ_FILE_PATH);
        if (!file.exists()) {
            File mkdir = FileUtil.mkdir(file);
            if (!mkdir.exists()) {
                throw new RuntimeException("创建目录失败");
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(LOCAL_KMZ_FILE_PATH + fileName + ".kmz");
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            zipOutputStream.setLevel(0); // 0 表示不压缩，存储方式

            // 创建 wpmz 目录中的 template.kml 文件条目
            buildZipFile("wpmz/template.kml", zipOutputStream, kml);

            // 创建 wpmz 目录中的 waylines.wpml 文件条目
            buildZipFile("wpmz/waylines.wpml", zipOutputStream, wpml);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return LOCAL_KMZ_FILE_PATH + fileName + ".kmz";
    }

    /**
     * 构建ZIP文件条目
     *
     * @param name 条目名称（在ZIP中的路径）
     * @param zipOutputStream ZIP输出流
     * @param content 文件内容
     * @throws IOException IO异常
     */
    private static void buildZipFile(String name, ZipOutputStream zipOutputStream, String content) throws IOException {
        ZipEntry kmlEntry = new ZipEntry(name);
        zipOutputStream.putNextEntry(kmlEntry);
        // 将内容写入 ZIP 条目
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) >= 0) {
                zipOutputStream.write(buffer, 0, length);
            }
        }
        zipOutputStream.closeEntry(); // 关闭条目
    }


    /**
     * 构建KML文档对象
     *
     * @param kmlParams 航线参数对象
     * @return KmlInfo KML信息对象
     */
    public static KmlInfo buildKml(KmlParams kmlParams) {
        KmlInfo kmlInfo = new KmlInfo();
        kmlInfo.setDocument(buildKmlDocument(FileTypeConstants.KML, kmlParams));
        return kmlInfo;
    }

    /**
     * 构建WPML文档对象
     *
     * @param kmlParams 航线参数对象
     * @return KmlInfo WPML信息对象
     */
    public static KmlInfo buildWpml(KmlParams kmlParams) {
        KmlInfo kmlInfo = new KmlInfo();
        kmlInfo.setDocument(buildKmlDocument(FileTypeConstants.WPML, kmlParams));
        return kmlInfo;
    }

    /**
     * 构建KML文档对象
     *
     * @param fileType 文件类型 (KML 或 WPML)
     * @param kmlParams 航线参数对象，包含所有航线配置信息
     * @return KmlDocument KML文档对象
     */
    public static KmlDocument buildKmlDocument(String fileType, KmlParams kmlParams) {
        // 创建一个新的KML文档对象
        KmlDocument kmlDocument = new KmlDocument();

        // 如果是KML文件类型，则设置文档元数据
        if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
            // 设置文档作者
            kmlDocument.setAuthor("Cleaner");
            // 设置创建时间（当前时间戳）
            kmlDocument.setCreateTime(String.valueOf(DateUtil.current()));
            // 设置更新时间（当前时间戳）
            kmlDocument.setUpdateTime(String.valueOf(DateUtil.current()));
        }

        // 构建并设置任务配置信息（包括无人机信息、负载信息等）
        kmlDocument.setKmlMissionConfig(buildKmlMissionConfig(kmlParams));

        // 构建并设置文件夹信息（包含航点、航线参数等具体配置）
        kmlDocument.setFolder(buildKmlFolder(fileType, kmlParams));

        // 返回构建好的KML文档对象
        return kmlDocument;
    }

    /**
     * 构建KML任务配置信息
     * 这部分包含了航线的核心配置，如无人机型号、负载信息、起飞参考点等
     *
     * @param kmlParams 航线参数对象
     * @return KmlMissionConfig 任务配置对象
     */
    public static KmlMissionConfig buildKmlMissionConfig(KmlParams kmlParams) {
        // 创建任务配置对象
        KmlMissionConfig kmlMissionConfig = new KmlMissionConfig();

        // 设置飞向航线模式为安全模式
        kmlMissionConfig.setFlyToWayLineMode(FlyToWaylineModeEnums.SAFELY.getValue());

        // 设置航线完成后的动作（如返航、降落等）
        kmlMissionConfig.setFinishAction(kmlParams.getFinishAction());

        // 设置失控后的处理动作
        if (StringUtils.isNotBlank(kmlParams.getExitOnRcLostAction())) {
            // 设置失控时执行丢失动作
            kmlMissionConfig.setExitOnRCLost(ExitOnRCLostEnums.EXECUTE_LOST_ACTION.getValue());
            // 设置具体的失控动作（返航、降落等）
            kmlMissionConfig.setExecuteRCLostAction(kmlParams.getExitOnRcLostAction());
        } else {
            // 默认设置为继续执行
            kmlMissionConfig.setExitOnRCLost(ExitOnRCLostEnums.GO_CONTINUE.getValue());
        }

        // 设置起飞安全高度（单位：米）
        kmlMissionConfig.setTakeOffSecurityHeight("20");

        // 设置全局过渡速度（单位：米/秒）
        kmlMissionConfig.setGlobalTransitionalSpeed("15");

        // 设置全局返航高度（单位：米）
        kmlMissionConfig.setGlobalRTHHeight("100");

        // 设置起飞参考点坐标（经纬度）
        kmlMissionConfig.setTakeOffRefPoint(kmlParams.getTakeOffRefPoint());

        // 构建并设置无人机信息（型号、子型号等）
        kmlMissionConfig.setDroneInfo(buildKmlDroneInfo(kmlParams.getDroneType(), kmlParams.getSubDroneType()));

        // 构建并设置负载信息（型号、安装位置等）
        kmlMissionConfig.setPayloadInfo(buildKmlPayloadInfo(kmlParams.getPayloadType(), kmlParams.getPayloadPosition(), kmlParams.getPayloadSubType()));

        // 返回构建好的任务配置对象
        return kmlMissionConfig;
    }

    /**
     * 构建无人机信息对象
     *
     * @param droneType 无人机类型枚举值
     * @param subDroneType 无人机子类型枚举值
     * @return KmlDroneInfo 无人机信息对象
     */
    public static KmlDroneInfo buildKmlDroneInfo(Integer droneType, Integer subDroneType) {
        KmlDroneInfo kmlDroneInfo = new KmlDroneInfo();
        kmlDroneInfo.setDroneEnumValue(String.valueOf(droneType));
        if (Objects.equals(droneType, DroneEnumValueEnums.M30_M30T.getValue()) ||
                Objects.equals(droneType, DroneEnumValueEnums.M3D_M3TD.getValue()) ||
                Objects.equals(droneType, DroneEnumValueEnums.M3E_M3T_M3M.getValue()) ||
                Objects.equals(droneType, DroneEnumValueEnums.M4D_M4TD.getValue())) {
            kmlDroneInfo.setDroneSubEnumValue(String.valueOf(subDroneType));
        }
        return kmlDroneInfo;
    }

    /**
     * 构建负载信息对象
     *
     * @param payloadType 负载类型枚举值
     * @param payloadPosition 负载安装位置索引
     * @param payloadSubType 负载子类型枚举值
     * @return KmlPayloadInfo 负载信息对象
     */
    public static KmlPayloadInfo buildKmlPayloadInfo(Integer payloadType, Integer payloadPosition, Integer payloadSubType) {
        KmlPayloadInfo kmlPayloadInfo = new KmlPayloadInfo();
        kmlPayloadInfo.setPayloadEnumValue(String.valueOf(payloadType));
        kmlPayloadInfo.setPayloadPositionIndex(String.valueOf(payloadPosition));
        if (payloadSubType != null) {
            kmlPayloadInfo.setPayloadSubEnumValue(String.valueOf(payloadSubType));
        }
        return kmlPayloadInfo;

    }


    /**
     * 构建KML文件夹对象
     *
     * 该方法根据文件类型(KML或WPML)和航线参数构建相应的文件夹结构，
     * 包括模板类型、坐标系参数、负载参数、航点信息等
     *
     * @param fileType 文件类型 (KML 或 WPML)
     * @param kmlParams 航线参数对象，包含所有航线配置信息
     * @return KmlFolder KML文件夹对象
     */
    public static KmlFolder buildKmlFolder(String fileType, KmlParams kmlParams) {
        // 创建KML文件夹对象
        KmlFolder kmlFolder = new KmlFolder();

        // 设置模板ID，默认为"0"
        kmlFolder.setTemplateId("0");

        // 设置自动飞行速度，从参数中获取
        kmlFolder.setAutoFlightSpeed(String.valueOf(kmlParams.getAutoFlightSpeed()));

        // 根据文件类型设置不同的参数
        if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
            // KML文件类型配置
            // 设置模板类型
            kmlFolder.setTemplateType(kmlParams.getTemplateType());

            // 构建并设置航线坐标系参数
            kmlFolder.setWaylineCoordinateSysParam(buildKmlWayLineCoordinateSysParam(
                    kmlParams.getTemplateType(),
                    HeightModeEnums.RELATIVE_TO_START_POINT.getValue(),
                    String.valueOf(kmlParams.getGlobalHeight())));

            // 构建并设置负载参数
            kmlFolder.setPayloadParam(buildKmlPayloadParam(kmlParams));
        }

        if (StringUtils.equals(fileType, FileTypeConstants.WPML)) {
            // WPML文件类型配置
            // 设置航线ID，默认为"0"
            kmlFolder.setWaylineId("0");

            // 设置执行高度模式为相对于起点
            kmlFolder.setExecuteHeightMode(ExecuteHeightModeEnums.RELATIVE_TO_START_POINT.getValue());

            // 如果存在起始动作列表，则构建起始动作组
            if (CollectionUtil.isNotEmpty(kmlParams.getStartActionList())) {
                KmlActionGroup kmlActionGroup = new KmlActionGroup();
                // 获取并设置动作列表
                kmlActionGroup.setAction(getKmlActionList(kmlParams.getStartActionList(), kmlParams));
                kmlFolder.setStartActionGroup(kmlActionGroup);
            }
        }

        // 航点类型模板航线配置
        if (StringUtils.equals(kmlFolder.getTemplateType(), TemplateTypeEnums.WAYPOINT.getValue())) {
            // 航点飞行模板类型处理
            WaypointTurnReq waypointTurnReq = kmlParams.getWaypointTurnReq();

            // 设置全局航点转弯模式
            kmlFolder.setGlobalWaypointTurnMode(waypointTurnReq.getWaypointTurnMode());

            // 根据转弯模式设置是否使用直线飞行
            if (StringUtils.equals(waypointTurnReq.getWaypointTurnMode(), GlobalWaypointTurnModeEnums.TO_POINT_AND_STOP_WITH_CONTINUITY_CURVATURE.getValue()) ||
                    StringUtils.equals(waypointTurnReq.getWaypointTurnMode(), GlobalWaypointTurnModeEnums.TO_POINT_AND_PASS_WITH_CONTINUITY_CURVATURE.getValue())) {
                kmlFolder.setGlobalUseStraightLine("1");
            }

            // 设置云台俯仰角控制模式
            kmlFolder.setGimbalPitchMode(kmlParams.getGimbalPitchMode());

            // 设置全局高度
            kmlFolder.setGlobalHeight(String.valueOf(kmlParams.getGlobalHeight()));

            // 设置全局航点偏航角参数
            WaypointHeadingReq waypointHeadingReq = kmlParams.getWaypointHeadingReq();
            kmlFolder.setGlobalWaypointHeadingParam(buildKmlGlobalWaypointHeadingParam(
                    waypointHeadingReq.getWaypointHeadingMode(),
                    waypointHeadingReq.getWaypointHeadingAngle(),
                    waypointHeadingReq.getWaypointPoiPoint()));

            // 构建航点
            List<RoutePointInfo> routePointList = kmlParams.getRoutePointList();
            if (CollectionUtil.isNotEmpty(routePointList)) {
                // 标记第一个和最后一个航点为起始/结束点
                routePointList.stream()
                        .min(Comparator.comparing(RoutePointInfo::getRoutePointIndex))
                        .ifPresent(routePointInfo -> routePointInfo.setIsStartAndEndPoint(Boolean.TRUE));
                routePointList.stream()
                        .max(Comparator.comparing(RoutePointInfo::getRoutePointIndex))
                        .ifPresent(routePointInfo -> routePointInfo.setIsStartAndEndPoint(Boolean.TRUE));

                // 构建航点标记列表
                List<KmlPlacemark> kmlPlacemarkList = new ArrayList<>();
                for (RoutePointInfo routePointInfo : routePointList) {
                    kmlPlacemarkList.add(buildKmlPlacemark(routePointInfo, kmlParams, fileType));
                }
                kmlFolder.setPlacemarkList(kmlPlacemarkList);
            }
        } else {
            // 非航点飞行模板类型处理（如建图航拍、倾斜摄影等）
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                // KML文件类型处理
                List<KmlPlacemark> kmlPlacemarkList = new ArrayList<>();
                // 构建测绘类型的航点标记
                kmlPlacemarkList.add(buildMappingKmlPlacemark(kmlParams));
                kmlFolder.setPlacemarkList(kmlPlacemarkList);
            } else {
                // WPML文件类型处理
                // 构建航点
                List<RoutePointInfo> routePointList = kmlParams.getRoutePointList();
                if (CollectionUtil.isNotEmpty(routePointList)) {
                    // 构建航点标记列表
                    List<KmlPlacemark> kmlPlacemarkList = new ArrayList<>();
                    for (RoutePointInfo routePointInfo : routePointList) {
                        kmlPlacemarkList.add(buildKmlPlacemark(routePointInfo, kmlParams, fileType));
                    }
                    kmlFolder.setPlacemarkList(kmlPlacemarkList);
                }
            }
        }

        // 返回构建好的KML文件夹对象
        return kmlFolder;
    }

    /**
     * 构建航点动作
     *
     * @param actionList 航点动作列表
     * @param kmlParams 航线参数对象
     * @return KmlAction动作列表
     */
    private static List<KmlAction> getKmlActionList(List<PointActionReq> actionList, KmlParams kmlParams) {
        List<KmlAction> kmlActionList = new ArrayList<>();
        for (PointActionReq pointActionReq : actionList) {
            if (ObjectUtil.isNotNull(pointActionReq.getHoverTime())) {
                kmlActionList.add(buildKmlAction(String.valueOf(pointActionReq.getActionIndex()), ActionActuatorFuncEnums.HOVER.getValue(), pointActionReq, kmlParams));
            } else if (ObjectUtil.isNotNull(pointActionReq.getAircraftHeading())) {
                kmlActionList.add(buildKmlAction(String.valueOf(pointActionReq.getActionIndex()), ActionActuatorFuncEnums.ROTATE_YAW.getValue(), pointActionReq, kmlParams));
            } else if (ObjectUtil.isNotNull(pointActionReq.getTakePhotoType()) && ObjectUtil.equals(pointActionReq.getTakePhotoType(), 0)) {
                kmlActionList.add(buildKmlAction(String.valueOf(pointActionReq.getActionIndex()), ActionActuatorFuncEnums.TAKE_PHOTO.getValue(), pointActionReq, kmlParams));
            } else if ((ObjectUtil.isNotNull(pointActionReq.getGimbalYawRotateAngle())) || (ObjectUtil.isNotNull(pointActionReq.getGimbalPitchRotateAngle()))) {
                kmlActionList.add(buildKmlAction(String.valueOf(pointActionReq.getActionIndex()), ActionActuatorFuncEnums.GIMBAL_ROTATE.getValue(), pointActionReq, kmlParams));
            } else if (ObjectUtil.isNotNull(pointActionReq.getZoom())) {
                kmlActionList.add(buildKmlAction(String.valueOf(pointActionReq.getActionIndex()), ActionActuatorFuncEnums.ZOOM.getValue(), pointActionReq, kmlParams));
            } else if (ObjectUtil.isNotNull(pointActionReq.getTakePhotoType()) && ObjectUtil.equals(pointActionReq.getTakePhotoType(), 1)) {
                kmlActionList.add(buildKmlAction(String.valueOf(pointActionReq.getActionIndex()), ActionActuatorFuncEnums.PANO_SHOT.getValue(), pointActionReq, kmlParams));
            } else if (ObjectUtil.isNotNull(pointActionReq.getStartRecord()) && pointActionReq.getStartRecord()) {
                kmlActionList.add(buildKmlAction(String.valueOf(pointActionReq.getActionIndex()), ActionActuatorFuncEnums.START_RECORD.getValue(), pointActionReq, kmlParams));
            } else if (ObjectUtil.isNotNull(pointActionReq.getStopRecord()) && pointActionReq.getStopRecord()) {
                kmlActionList.add(buildKmlAction(String.valueOf(pointActionReq.getActionIndex()), ActionActuatorFuncEnums.STOP_RECORD.getValue(), pointActionReq, kmlParams));
            }
        }
        return kmlActionList;
    }

    /**
     * 构造坐标系参数信息
     *
     * @param templateType 航线模板类型
     * @param heightMode   高度模式
     * @param height       高度
     * @return 坐标系参数信息
     */
    public static KmlWayLineCoordinateSysParam buildKmlWayLineCoordinateSysParam(String templateType, String heightMode, String height) {
        KmlWayLineCoordinateSysParam kmlWayLineCoordinateSysParam = new KmlWayLineCoordinateSysParam();
        kmlWayLineCoordinateSysParam.setCoordinateMode("WGS84");
        kmlWayLineCoordinateSysParam.setHeightMode(heightMode);
        kmlWayLineCoordinateSysParam.setPositioningType(PositioningTypeEnums.GPS.getValue());
        if (StringUtils.equals(templateType, TemplateTypeEnums.MAPPING2D.getValue()) ||
                StringUtils.equals(templateType, TemplateTypeEnums.MAPPING3D.getValue()) ||
                StringUtils.equals(templateType, TemplateTypeEnums.MAPPING_STRIP.getValue())) {
            kmlWayLineCoordinateSysParam.setGlobalShootHeight(height);
            kmlWayLineCoordinateSysParam.setSurfaceFollowModeEnable("1");
            kmlWayLineCoordinateSysParam.setSurfaceRelativeHeight(height);
        }
        return kmlWayLineCoordinateSysParam;
    }

    /**
     * 构建负载参数对象
     *
     * @param kmlParams 航线参数对象
     * @return KmlPayloadParam 负载参数对象
     */
    public static KmlPayloadParam buildKmlPayloadParam(KmlParams kmlParams) {
        KmlPayloadParam kmlPayloadParam = new KmlPayloadParam();
        kmlPayloadParam.setPayloadPositionIndex(String.valueOf(kmlParams.getPayloadPosition()));
        kmlPayloadParam.setFocusMode(FocusModeEnums.FIRST_POINT.getValue());
        kmlPayloadParam.setMeteringMode(MeteringModeEnums.AVERAGE.getValue());
        // 0：不开启 1：开启
        kmlPayloadParam.setDewarpingEnable("1");
        kmlPayloadParam.setReturnMode(ReturnModeEnums.SINGLE_RETURN_STRONGEST.getValue());
        // 60000、80000、120000、160000、180000、240000
        kmlPayloadParam.setSamplingRate("240000");
        kmlPayloadParam.setScanningMode(ScanningModeEnums.REPETITIVE.getValue());
        // 0: 不上色 1: 真彩上色
        kmlPayloadParam.setModelColoringEnable("1");
        kmlPayloadParam.setImageFormat(kmlParams.getImageFormat());

        return kmlPayloadParam;
    }

    /**
     * 构建全局航点偏航角参数对象
     *
     * @param waypointHeadingMode 偏航角模式
     * @param waypointHeadingAngle 偏航角角度
     * @param waypointPoiPoint 兴趣点坐标
     * @return KmlGlobalWaypointHeadingParam 全局航点偏航角参数对象
     */
    public static KmlGlobalWaypointHeadingParam buildKmlGlobalWaypointHeadingParam(String waypointHeadingMode, Double waypointHeadingAngle, String waypointPoiPoint) {
        KmlGlobalWaypointHeadingParam kmlGlobalWaypointHeadingParam = new KmlGlobalWaypointHeadingParam();
        kmlGlobalWaypointHeadingParam.setWaypointHeadingMode(waypointHeadingMode);
        if (StringUtils.equals(waypointHeadingMode, WaypointHeadingModeEnums.SMOOTH_TRANSITION.getValue())) {
            kmlGlobalWaypointHeadingParam.setWaypointHeadingAngle(String.valueOf(waypointHeadingAngle));
        }
        if (StringUtils.equals(waypointHeadingMode, WaypointHeadingModeEnums.TOWARD_POI.getValue())) {
            kmlGlobalWaypointHeadingParam.setWaypointPoiPoint(waypointPoiPoint);
        }
        kmlGlobalWaypointHeadingParam.setWaypointHeadingPathMode(WaypointHeadingPathModeEnums.FOLLOW_BAD_ARC.getValue());
        return kmlGlobalWaypointHeadingParam;

    }

    /**
     * 构建航点标记对象
     *
     * @param routePointInfo 航点信息
     * @param kmlParams 航线参数对象
     * @param fileType 文件类型
     * @return KmlPlacemark 航点标记对象
     */
    public static KmlPlacemark buildKmlPlacemark(RoutePointInfo routePointInfo, KmlParams kmlParams, String fileType) {
        KmlPlacemark kmlPlacemark = new KmlPlacemark();
        kmlPlacemark.setIsRisky("0");
        kmlPlacemark.setKmlPoint(buildKmlPoint(String.valueOf(routePointInfo.getLongitude()), String.valueOf(routePointInfo.getLatitude())));
        kmlPlacemark.setIndex(String.valueOf(routePointInfo.getRoutePointIndex()));

        handleHeight(routePointInfo, kmlParams, fileType, kmlPlacemark);
        handleSpeed(routePointInfo, kmlParams, fileType, kmlPlacemark);
        handleWaypointHeadingParam(routePointInfo, kmlParams, fileType, kmlPlacemark);
        handleWaypointTurnParam(routePointInfo, kmlParams, fileType, kmlPlacemark);
        if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
            if (ObjectUtil.isNotEmpty(routePointInfo.getGimbalPitchAngle()) && StringUtils.equals(kmlParams.getGimbalPitchMode(), GimbalPitchModeEnums.USE_POINT_SETTING.getValue())) {
                kmlPlacemark.setGimbalPitchAngle(String.valueOf(routePointInfo.getGimbalPitchAngle()));
            }
        }
        if (CollectionUtil.isNotEmpty(routePointInfo.getActionGroupList())) {
            List<KmlActionGroup> kmlActionGroupList = new ArrayList<>();
            for (ActionGroupReq actionGroupReq : routePointInfo.getActionGroupList()) {
                kmlActionGroupList.add(buildKmlActionGroup(actionGroupReq, kmlParams));
            }
            kmlPlacemark.setActionGroup(kmlActionGroupList);
        }
        return kmlPlacemark;
    }

    /**
     * 构建测绘航点标记对象
     *
     * @param kmlParams 航线参数对象
     * @return KmlPlacemark 测绘航点标记对象
     */
    private static KmlPlacemark buildMappingKmlPlacemark(KmlParams kmlParams) {
        KmlPlacemark kmlPlacemark = new KmlPlacemark();
        MappingTypeReq mappingTypeReq = kmlParams.getMappingTypeReq();
        kmlPlacemark.setCaliFlightEnable("0");
        kmlPlacemark.setElevationOptimizeEnable(String.valueOf(mappingTypeReq.getElevationOptimizeEnable()));
        kmlPlacemark.setSmartObliqueEnable("0");
        kmlPlacemark.setShootType(mappingTypeReq.getShootType());
        kmlPlacemark.setDirection(mappingTypeReq.getDirection());
        kmlPlacemark.setMargin(mappingTypeReq.getMargin());
        kmlPlacemark.setOverlap(buildKmlOverlap(mappingTypeReq.getCollectionMethod(), mappingTypeReq.getLensType(), mappingTypeReq.getOverlapH(), mappingTypeReq.getOverlapW()));
        kmlPlacemark.setEllipsoidHeight(String.valueOf(kmlParams.getGlobalHeight()));
        kmlPlacemark.setHeight(String.valueOf(kmlParams.getGlobalHeight()));
        kmlPlacemark.setFacadeWaylineEnable("0");
        kmlPlacemark.setPolygon(buildKmlPolygon(mappingTypeReq.getCoordinates()));
        return kmlPlacemark;
    }

    /**
     * 构建重叠参数对象
     *
     * @param collectionMethod 采集方式
     * @param lensType 镜头类型
     * @param overlapH 航向重叠率
     * @param overlapW 旁向重叠率
     * @return KmlOverlap 重叠参数对象
     */
    private static KmlOverlap buildKmlOverlap(String collectionMethod, String lensType, Integer overlapH, Integer overlapW) {
        KmlOverlap overlap = new KmlOverlap();
        if (StringUtils.equals(collectionMethod, LensTypeEnums.CAMERA.getValue())) {
            overlap.setOrthoCameraOverlapH(String.valueOf(overlapH));
            overlap.setOrthoCameraOverlapW(String.valueOf(overlapW));
            overlap.setInclinedCameraOverlapW(String.valueOf(overlapW));
            overlap.setInclinedCameraOverlapH(String.valueOf(overlapH));
        } else {
            overlap.setOrthoLidarOverlapH(String.valueOf(overlapH));
            overlap.setOrthoLidarOverlapW(String.valueOf(overlapW));
            overlap.setInclinedLidarOverlapH(String.valueOf(overlapH));
            overlap.setInclinedLidarOverlapW(String.valueOf(overlapW));
        }
//        if (StringUtils.equals(collectionMethod, CollectionMethodEnums.ORTHO.getValue())) {
//            if (StringUtils.equals(lensType, LensTypeEnums.LIDAR.getValue())) {
//                overlap.setOrthoLidarOverlapH(String.valueOf(overlapH));
//                overlap.setOrthoLidarOverlapW(String.valueOf(overlapW));
//            } else {
//                overlap.setOrthoCameraOverlapH(String.valueOf(overlapH));
//                overlap.setOrthoCameraOverlapW(String.valueOf(overlapW));
//            }
//        } else {
//            if (StringUtils.equals(lensType, LensTypeEnums.LIDAR.getValue())) {
//                overlap.setInclinedLidarOverlapH(String.valueOf(overlapH));
//                overlap.setInclinedLidarOverlapW(String.valueOf(overlapW));
//            } else {
//                overlap.setInclinedCameraOverlapH(String.valueOf(overlapH));
//                overlap.setInclinedCameraOverlapW(String.valueOf(overlapW));
//            }
//        }
        return overlap;
    }

    /**
     * 构建多边形对象
     *
     * @param coordinatePointReqList 坐标点列表
     * @return KmlPolygon 多边形对象
     */
    private static KmlPolygon buildKmlPolygon(List<CoordinatePointReq> coordinatePointReqList) {
        KmlPolygon kmlPolygon = new KmlPolygon();
        KmlLinearRing kmlLinearRing = new KmlLinearRing();

        String coordinates = coordinatePointReqList.stream().map(point -> point.getLongitude() + "," + point.getLatitude() + "," + point.getHeight())
                .collect(Collectors.joining(", "));
        kmlLinearRing.setCoordinates(StringUtils.join(coordinates, " "));
        KmlOuterBoundaryIs kmlOuterBoundaryIs = new KmlOuterBoundaryIs();
        kmlOuterBoundaryIs.setLinearRing(kmlLinearRing);
        kmlPolygon.setOuterBoundaryIs(kmlOuterBoundaryIs);
        return kmlPolygon;
    }

    /**
     * 处理航点转弯参数
     *
     * @param routePointInfo 航点信息
     * @param kmlParams 航线参数对象
     * @param fileType 文件类型
     * @param kmlPlacemark 航点标记对象
     */
    private static void handleWaypointTurnParam(RoutePointInfo routePointInfo, KmlParams kmlParams, String fileType, KmlPlacemark kmlPlacemark) {
        WaypointTurnReq waypointTurnReq = routePointInfo.getWaypointTurnReq();
        // 使用全局航点转弯模式
        if (ObjectUtil.isNotEmpty(waypointTurnReq)) {
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                kmlPlacemark.setUseGlobalTurnParam("0");
            }
            kmlPlacemark.setWaypointTurnParam(buildKmlWaypointTurnParam(waypointTurnReq.getWaypointTurnMode(),
                    waypointTurnReq.getWaypointTurnDampingDist(), waypointTurnReq.getUseStraightLine(), routePointInfo.getIsStartAndEndPoint()));
            if (ObjectUtil.isNotEmpty(waypointTurnReq.getUseStraightLine())) {
                kmlPlacemark.setUseStraightLine(String.valueOf(waypointTurnReq.getUseStraightLine()));
            }
        } else {
            // 使用自定义航点转弯模式
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                kmlPlacemark.setUseGlobalTurnParam("1");
            } else if (StringUtils.equals(fileType, FileTypeConstants.WPML)) {
                WaypointTurnReq globalWaypoint = kmlParams.getWaypointTurnReq();
                kmlPlacemark.setWaypointTurnParam(buildKmlWaypointTurnParam(globalWaypoint.getWaypointTurnMode(),
                        globalWaypoint.getWaypointTurnDampingDist(), globalWaypoint.getUseStraightLine(), routePointInfo.getIsStartAndEndPoint()));
            }
        }
    }

    /**
     * 处理航点偏航角参数
     *
     * @param routePointInfo 航点信息
     * @param kmlParams 航线参数对象
     * @param fileType 文件类型
     * @param kmlPlacemark 航点标记对象
     */
    private static void handleWaypointHeadingParam(RoutePointInfo routePointInfo, KmlParams kmlParams, String fileType, KmlPlacemark kmlPlacemark) {
        WaypointHeadingReq waypointHeadingReq = routePointInfo.getWaypointHeadingReq();
        if (ObjectUtil.isNotEmpty(waypointHeadingReq)) {
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                kmlPlacemark.setUseGlobalHeadingParam("0");
            }
            kmlPlacemark.setWaypointHeadingParam(buildKmlWaypointHeadingParam(waypointHeadingReq.getWaypointHeadingMode(), waypointHeadingReq.getWaypointHeadingAngle(), waypointHeadingReq.getWaypointPoiPoint()));
        } else {
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                kmlPlacemark.setUseGlobalHeadingParam("1");
            } else if (StringUtils.equals(fileType, FileTypeConstants.WPML)) {
                WaypointHeadingReq globalWaypointHeading = kmlParams.getWaypointHeadingReq();
                kmlPlacemark.setWaypointHeadingParam(buildKmlWaypointHeadingParam(globalWaypointHeading.getWaypointHeadingMode(), globalWaypointHeading.getWaypointHeadingAngle(), globalWaypointHeading.getWaypointPoiPoint()));
            }
        }

    }


    /**
     * 处理航点速度参数
     *
     * @param routePointInfo 航点信息
     * @param kmlParams 航线参数对象
     * @param fileType 文件类型
     * @param kmlPlacemark 航点标记对象
     */
    private static void handleSpeed(RoutePointInfo routePointInfo, KmlParams kmlParams, String fileType, KmlPlacemark kmlPlacemark) {
        if (ObjectUtil.isNotEmpty(routePointInfo.getSpeed())) {
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                kmlPlacemark.setUseGlobalSpeed("0");
            }
            kmlPlacemark.setWaypointSpeed(String.valueOf(routePointInfo.getSpeed()));
        } else {
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                kmlPlacemark.setUseGlobalSpeed("1");
            } else if (StringUtils.equals(fileType, FileTypeConstants.WPML)) {
                kmlPlacemark.setWaypointSpeed(String.valueOf(kmlParams.getAutoFlightSpeed()));
            }
        }
    }

    /**
     * 处理航点高度参数
     *
     * @param routePointInfo 航点信息
     * @param kmlParams 航线参数对象
     * @param fileType 文件类型
     * @param kmlPlacemark 航点标记对象
     */
    private static void handleHeight(RoutePointInfo routePointInfo, KmlParams kmlParams, String fileType, KmlPlacemark kmlPlacemark) {
        if (ObjectUtil.isNotEmpty(routePointInfo.getHeight())) {
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                kmlPlacemark.setUseGlobalHeight("0");
                kmlPlacemark.setEllipsoidHeight(String.valueOf(routePointInfo.getHeight()));
                kmlPlacemark.setHeight(String.valueOf(routePointInfo.getHeight()));
            } else if (StringUtils.equals(fileType, FileTypeConstants.WPML)) {
                kmlPlacemark.setExecuteHeight(String.valueOf(routePointInfo.getHeight()));
            }
        } else {
            if (StringUtils.equals(fileType, FileTypeConstants.KML)) {
                kmlPlacemark.setUseGlobalHeight("1");
            } else if (StringUtils.equals(fileType, FileTypeConstants.WPML)) {
                kmlPlacemark.setExecuteHeight(String.valueOf(kmlParams.getGlobalHeight()));
            }
        }
    }

    /**
     * 构建航点坐标对象
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @return KmlPoint 航点坐标对象
     */
    public static KmlPoint buildKmlPoint(String longitude, String latitude) {
        KmlPoint kmlPoint = new KmlPoint();
        kmlPoint.setCoordinates(longitude + "," + latitude);
        return kmlPoint;
    }

    /**
     * 构建航点偏航角参数对象
     *
     * @param waypointHeadingMode 偏航角模式
     * @param waypointHeadingAngle 偏航角角度
     * @param waypointPoiPoint 兴趣点坐标
     * @return KmlWaypointHeadingParam 航点偏航角参数对象
     */
    public static KmlWaypointHeadingParam buildKmlWaypointHeadingParam(String waypointHeadingMode, Double waypointHeadingAngle, String waypointPoiPoint) {
        KmlWaypointHeadingParam kmlWaypointHeadingParam = new KmlWaypointHeadingParam();
        kmlWaypointHeadingParam.setWaypointHeadingMode(waypointHeadingMode);
        if (StringUtils.equals(waypointHeadingMode, WaypointHeadingModeEnums.SMOOTH_TRANSITION.getValue())) {
            kmlWaypointHeadingParam.setWaypointHeadingAngle(String.valueOf(waypointHeadingAngle));
        }
        if (StringUtils.equals(waypointHeadingMode, WaypointHeadingModeEnums.TOWARD_POI.getValue())) {
            kmlWaypointHeadingParam.setWaypointPoiPoint(waypointPoiPoint);
        }
        kmlWaypointHeadingParam.setWaypointHeadingPathMode(WaypointHeadingPathModeEnums.FOLLOW_BAD_ARC.getValue());
        return kmlWaypointHeadingParam;
    }

    /**
     * 构建航点转弯参数对象
     *
     * @param waypointTurnMode 转弯模式
     * @param waypointTurnDampingDist 转弯缓冲距离
     * @param useStraightLine 是否使用直线
     * @param startAndEndPoint 是否为首尾航点
     * @return KmlWaypointTurnParam 航点转弯参数对象
     */
    public static KmlWaypointTurnParam buildKmlWaypointTurnParam(String waypointTurnMode, Double waypointTurnDampingDist, Integer useStraightLine, Boolean startAndEndPoint) {
        KmlWaypointTurnParam kmlWaypointTurnParam = new KmlWaypointTurnParam();
        // 首尾航点不能是协调转弯类型
        if (startAndEndPoint && StringUtils.equals(waypointTurnMode, GlobalWaypointTurnModeEnums.COORDINATE_TURN.getValue())) {
            kmlWaypointTurnParam.setWaypointTurnMode(GlobalWaypointTurnModeEnums.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE.getValue());
        } else {
            kmlWaypointTurnParam.setWaypointTurnMode(waypointTurnMode);
        }
        if ((StringUtils.equals(waypointTurnMode, GlobalWaypointTurnModeEnums.COORDINATE_TURN.getValue()) ||
                StringUtils.equals(waypointTurnMode, GlobalWaypointTurnModeEnums.TO_POINT_AND_PASS_WITH_CONTINUITY_CURVATURE.getValue())) &&
                ObjectUtil.equals(useStraightLine, 1)) {
            kmlWaypointTurnParam.setWaypointTurnDampingDist(String.valueOf(waypointTurnDampingDist));
        }
        return kmlWaypointTurnParam;
    }

    /**
     * 构建航点动作组对象
     *
     * @param actionGroupReq 动作组请求参数
     * @param kmlParams 航线参数对象
     * @return KmlActionGroup 航点动作组对象
     */
    public static KmlActionGroup buildKmlActionGroup(ActionGroupReq actionGroupReq, KmlParams kmlParams) {
        KmlActionGroup kmlActionGroup = new KmlActionGroup();
        kmlActionGroup.setActionGroupId(String.valueOf(actionGroupReq.getActionGroupId()));
        kmlActionGroup.setActionGroupStartIndex(String.valueOf(actionGroupReq.getActionGroupStartIndex()));
        kmlActionGroup.setActionGroupEndIndex(String.valueOf(actionGroupReq.getActionGroupEndIndex()));
        kmlActionGroup.setActionGroupMode(ActionGroupModeEnums.SEQUENCE.getValue());

        kmlActionGroup.setActionTrigger(buildKmlActionTrigger(actionGroupReq.getActionTriggerType(), actionGroupReq.getActionTriggerParam()));
        kmlActionGroup.setAction(getKmlActionList(actionGroupReq.getActions(), kmlParams));
        return kmlActionGroup;
    }

    /**
     * 构建动作触发器对象
     *
     * @param actionTriggerType 动作触发类型
     * @param actionTriggerParam 动作触发参数
     * @return KmlActionTrigger 动作触发器对象
     */
    public static KmlActionTrigger buildKmlActionTrigger(String actionTriggerType, Double actionTriggerParam) {
        KmlActionTrigger kmlActionTrigger = new KmlActionTrigger();
        kmlActionTrigger.setActionTriggerType(actionTriggerType);
        if (StringUtils.equals(actionTriggerType, ActionTriggerTypeEnums.MULTIPLE_TIMING.getValue()) ||
                StringUtils.equals(actionTriggerType, ActionTriggerTypeEnums.MULTIPLE_DISTANCE.getValue())) {
            kmlActionTrigger.setActionTriggerParam(String.valueOf(actionTriggerParam));
        }
        return kmlActionTrigger;
    }

    /**
     * 构建航点动作对象
     *
     * @param actionId 动作ID
     * @param actionActuatorFunc 动作执行器功能
     * @param pointActionReq 点动作请求参数
     * @param kmlParams 航线参数对象
     * @return KmlAction 航点动作对象
     */
    public static KmlAction buildKmlAction(String actionId, String actionActuatorFunc, PointActionReq pointActionReq, KmlParams kmlParams) {
        KmlAction kmlAction = new KmlAction();
        kmlAction.setActionId(actionId);
        kmlAction.setActionActuatorFunc(actionActuatorFunc);
        kmlAction.setActionActuatorFuncParam(buildKmlActionActuatorFuncParam(actionActuatorFunc, pointActionReq, kmlParams));
        return kmlAction;
    }

    /**
     * 构建动作执行器功能参数对象
     *
     * @param actionActuatorFunc 动作执行器功能
     * @param pointActionReq 点动作请求参数
     * @param kmlParams 航线参数对象
     * @return KmlActionActuatorFuncParam 动作执行器功能参数对象
     */
    public static KmlActionActuatorFuncParam buildKmlActionActuatorFuncParam(String actionActuatorFunc, PointActionReq pointActionReq, KmlParams kmlParams) {
        KmlActionActuatorFuncParam kmlActionActuatorFuncParam = new KmlActionActuatorFuncParam();
        if (StringUtils.equals(actionActuatorFunc, ActionActuatorFuncEnums.TAKE_PHOTO.getValue()) ||
                StringUtils.equals(actionActuatorFunc, ActionActuatorFuncEnums.START_RECORD.getValue())) {
            kmlActionActuatorFuncParam.setPayloadPositionIndex(String.valueOf(kmlParams.getPayloadPosition()));
            kmlActionActuatorFuncParam.setFileSuffix("");
            kmlActionActuatorFuncParam.setUseGlobalPayloadLensIndex(String.valueOf(pointActionReq.getUseGlobalImageFormat()));
            if (ObjectUtil.equals(pointActionReq.getUseGlobalImageFormat(), 0)) {
                kmlActionActuatorFuncParam.setPayloadLensIndex(pointActionReq.getImageFormat());
            } else {
                kmlActionActuatorFuncParam.setPayloadLensIndex(kmlParams.getImageFormat());
            }
        } else if (StringUtils.equals(actionActuatorFunc, ActionActuatorFuncEnums.STOP_RECORD.getValue())) {
            kmlActionActuatorFuncParam.setPayloadPositionIndex(String.valueOf(kmlParams.getPayloadPosition()));
        } else if (StringUtils.equals(actionActuatorFunc, ActionActuatorFuncEnums.GIMBAL_ROTATE.getValue())) {
            kmlActionActuatorFuncParam.setPayloadPositionIndex(String.valueOf(kmlParams.getPayloadPosition()));
            kmlActionActuatorFuncParam.setGimbalHeadingYawBase("north");
            kmlActionActuatorFuncParam.setGimbalRotateMode("absoluteAngle");
            if (!Objects.isNull(pointActionReq.getGimbalPitchRotateAngle())) {
                kmlActionActuatorFuncParam.setGimbalPitchRotateEnable("1");
                kmlActionActuatorFuncParam.setGimbalPitchRotateAngle(String.valueOf(pointActionReq.getGimbalPitchRotateAngle()));
            } else {
                kmlActionActuatorFuncParam.setGimbalPitchRotateEnable("0");
                kmlActionActuatorFuncParam.setGimbalPitchRotateAngle("0");
            }
            kmlActionActuatorFuncParam.setGimbalRollRotateEnable("0");
            kmlActionActuatorFuncParam.setGimbalRollRotateAngle("0");
            if (!Objects.isNull(pointActionReq.getGimbalYawRotateAngle())) {
                kmlActionActuatorFuncParam.setGimbalYawRotateEnable("1");
                kmlActionActuatorFuncParam.setGimbalYawRotateAngle(String.valueOf(pointActionReq.getGimbalYawRotateAngle()));
            } else {
                kmlActionActuatorFuncParam.setGimbalYawRotateEnable("0");
                kmlActionActuatorFuncParam.setGimbalYawRotateAngle("0");
            }
            kmlActionActuatorFuncParam.setGimbalRotateTimeEnable("0");
            kmlActionActuatorFuncParam.setGimbalRotateTime("0");
        } else if (StringUtils.equals(actionActuatorFunc, ActionActuatorFuncEnums.ROTATE_YAW.getValue())) {
            kmlActionActuatorFuncParam.setAircraftHeading(String.valueOf(pointActionReq.getAircraftHeading()));
            kmlActionActuatorFuncParam.setAircraftPathMode(AircraftPathModeEnums.CLOCKWISE.getValue());
        } else if (StringUtils.equals(actionActuatorFunc, ActionActuatorFuncEnums.HOVER.getValue())) {
            kmlActionActuatorFuncParam.setHoverTime(String.valueOf(pointActionReq.getHoverTime()));
        } else if (StringUtils.equals(actionActuatorFunc, ActionActuatorFuncEnums.ZOOM.getValue())) {
            kmlActionActuatorFuncParam.setPayloadPositionIndex(String.valueOf(kmlParams.getPayloadPosition()));
            kmlActionActuatorFuncParam.setFocalLength(String.valueOf(pointActionReq.getZoom()));
        } else if (StringUtils.equals(actionActuatorFunc, ActionActuatorFuncEnums.PANO_SHOT.getValue())) {
            kmlActionActuatorFuncParam.setPayloadPositionIndex(String.valueOf(kmlParams.getPayloadPosition()));
            kmlActionActuatorFuncParam.setUseGlobalPayloadLensIndex(String.valueOf(pointActionReq.getUseGlobalImageFormat()));
            kmlActionActuatorFuncParam.setPanoShotSubMode("panoShot_360");
            if (ObjectUtil.equals(pointActionReq.getUseGlobalImageFormat(), 0)) {
                kmlActionActuatorFuncParam.setPayloadLensIndex(pointActionReq.getImageFormat());
            } else {
                kmlActionActuatorFuncParam.setPayloadLensIndex(kmlParams.getImageFormat());
            }
        }
        return kmlActionActuatorFuncParam;
    }
}
