package com.didichuxing.datachannel.arius.admin.method.v3.op.cluster.phy;

import static com.didichuxing.datachannel.arius.admin.common.constant.ApiVersion.V3_OP;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didichuxing.datachannel.arius.admin.AriusClient;
import com.didichuxing.datachannel.arius.admin.common.Tuple;
import com.didichuxing.datachannel.arius.admin.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.arius.admin.common.bean.common.Result;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.cluster.ClusterJoinDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.cluster.ClusterPhyConditionDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.vo.cluster.ConsoleClusterPhyVO;
import com.didichuxing.datachannel.arius.admin.common.bean.vo.cluster.ESClusterRoleHostVO;
import com.didichuxing.datachannel.arius.admin.common.bean.vo.cluster.ESClusterRoleVO;

/**
 * @author cjm
 */
public class ESPhyClusterControllerMethod {

    public static final String PHY_CLUSTER = V3_OP + "/phy/cluster";

    public static Result<List<ESClusterRoleVO>> roleList(Long clusterId) throws IOException {
        String path = String.format("%s/%d/roles", PHY_CLUSTER, clusterId);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<List<ESClusterRoleVO>>>(){});
    }

    public static Result<Long> pluginDelete(Long pluginId) throws IOException {
        String path = String.format("%s/plugin/%d", PHY_CLUSTER, pluginId);
        return JSON.parseObject(AriusClient.delete(path), new TypeReference<Result<Long>>(){});
    }

    public static Result<Long> packageDelete(Long packageId) throws IOException {
        String path = String.format("%s/package/%d", PHY_CLUSTER, packageId);
        return JSON.parseObject(AriusClient.delete(path), new TypeReference<Result<Long>>(){});
    }

    public static Result<Tuple<Long, String>> clusterJoin(ClusterJoinDTO dto) throws IOException {
        String path = String.format("%s/join", PHY_CLUSTER);
        return JSON.parseObject(AriusClient.post(path, dto), new TypeReference<Result<Tuple<Long, String>>>(){});
    }

    public static Result<Boolean> addTemplateSrvId(ClusterJoinDTO clusterJoinDTO, String templateSrvId) throws IOException {
        String path = String.format("%s/join/%s/checkTemplateService", PHY_CLUSTER, templateSrvId);
        return JSON.parseObject(AriusClient.post(path, clusterJoinDTO), new TypeReference<Result<Boolean>>(){});
    }

    public static Result<List<ESClusterRoleHostVO>> getClusterPhyRegionInfos(Long clusterId) throws IOException {
        String path = String.format("%s/%d/regioninfo", PHY_CLUSTER, clusterId);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<List<ESClusterRoleHostVO>>>(){});
    }

    public static Result<List<String>> listCanBeAssociatedRegionOfClustersPhys(Integer clusterLogicType, Long clusterLogicId) throws IOException {
        String path = String.format("%s/%d/%d/list", PHY_CLUSTER, clusterLogicType, clusterLogicId);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<List<String>>>(){});
    }

    public static Result<List<String>> listCanBeAssociatedClustersPhys(Integer clusterLogicType) throws IOException {
        String path = String.format("%s/%d/list", PHY_CLUSTER, clusterLogicType);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<List<String>>>(){});
    }

    public static Result<List<String>> getClusterPhyNames() throws IOException {
        String path = String.format("%s/names", PHY_CLUSTER);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<List<String>>>(){});
    }

    public static Result<List<String>> getAppClusterPhyNodeNames(String clusterPhyName) throws IOException {
        String path = String.format("%s/%s/nodes", PHY_CLUSTER, clusterPhyName);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<List<String>>>(){});
    }

    public static Result<List<String>> getAppNodeNames() throws IOException {
        String path = String.format("%s/node/names", PHY_CLUSTER);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<List<String>>>(){});
    }

    public static PaginationResult<ConsoleClusterPhyVO> pageGetConsoleClusterPhyVOS(ClusterPhyConditionDTO condition) throws IOException {
        String path = String.format("%s/page", PHY_CLUSTER);
        return JSON.parseObject(AriusClient.post(path, condition), new TypeReference<PaginationResult<ConsoleClusterPhyVO>>(){});
    }

    public static Result<ConsoleClusterPhyVO> get(Long clusterId) throws IOException {
        String path = String.format("%s/%d/overView", PHY_CLUSTER, clusterId);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<ConsoleClusterPhyVO>>(){});
    }

    public static Result<List<String>> getPhyClusterNameWithSameEsVersion(Integer clusterLogicType, String clusterName) throws IOException {
        String path = String.format("%s/%d/%s/version/list", PHY_CLUSTER, clusterLogicType, clusterName);
        return JSON.parseObject(AriusClient.get(path), new TypeReference<Result<List<String>>>(){});
    }
}