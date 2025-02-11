package com.didichuxing.datachannel.arius.admin.common.bean.dto.metrics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "esClientNode节点查询")
public class ClientNodeDTO extends GatewayNodeDTO {

    @ApiModelProperty("clientNodeIp")
    private String clientNodeIp;

    @Override
    public String getGroup() {
        return "clientNode";
    }
}
