package com.cleaner.djuav.controller;

import com.cleaner.djuav.domain.KmzInfoVO;
import com.cleaner.djuav.domain.UavRouteReq;
import com.cleaner.djuav.service.UavRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Tag(name = "航线生成")
public class UavRouteController {

    @Resource
    private UavRouteService routeService;

    /**
     * 编辑kmz文件
     */
    @PostMapping("/updateKmz")
    @Operation(summary = "编辑kmz文件")
    public void updateKmz(@RequestBody UavRouteReq uavRouteReq) {
        this.routeService.updateKmz(uavRouteReq);
    }

    /**
     * 生成kmz文件
     */
    @PostMapping("/buildKmz")
    @Operation(summary = "生成kmz文件")
    public void buildKmz(@RequestBody UavRouteReq uavRouteReq) {
        this.routeService.buildKmz(uavRouteReq);
    }

    /**
     * 解析kmz文件
     *
     * @param fileUrl
     */
    @PostMapping("/parseKmz")
    @Operation(summary = "解析kmz文件")
    public KmzInfoVO parseKmz(@RequestParam("fileUrl") String fileUrl) throws IOException {
        return this.routeService.parseKmz(fileUrl);
    }
}
