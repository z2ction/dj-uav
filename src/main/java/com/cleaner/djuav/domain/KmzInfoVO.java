package com.cleaner.djuav.domain;

import com.cleaner.djuav.domain.kml.KmlInfo;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;

/**
 * Author:Cleaner
 * Date: 2024/12/22 10:46
 **/
@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class KmzInfoVO implements Serializable {

    /**
     * 航线kml信息
     */
    private KmlInfo kmlInfo;

    /**
     * 航线wpml信息
     */
    private KmlInfo wpmlInfo;

}

