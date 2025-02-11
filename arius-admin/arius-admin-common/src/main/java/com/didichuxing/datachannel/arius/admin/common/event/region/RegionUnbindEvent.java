package com.didichuxing.datachannel.arius.admin.common.event.region;

import com.didichuxing.datachannel.arius.admin.common.bean.entity.region.ClusterRegion;
import org.springframework.context.ApplicationEvent;

/**
 * @Author: lanxinzheng
 * @Date: 2021/1/7
 * @Comment: region解除绑定事件
 */
public class RegionUnbindEvent extends ApplicationEvent {

    private ClusterRegion clusterRegion;

    private String        operator;

    public RegionUnbindEvent(Object source, ClusterRegion clusterRegion, String operator) {
        super(source);
        this.clusterRegion = clusterRegion;
        this.operator = operator;
    }

    public ClusterRegion getClusterRegion() {
        return clusterRegion;
    }

    public String getOperator() {
        return operator;
    }
}
