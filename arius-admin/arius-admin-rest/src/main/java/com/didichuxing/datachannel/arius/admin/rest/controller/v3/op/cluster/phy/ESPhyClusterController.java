package com.didichuxing.datachannel.arius.admin.rest.controller.v3.op.cluster.phy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.didichuxing.datachannel.arius.admin.biz.cluster.ClusterPhyManager;
import com.didichuxing.datachannel.arius.admin.common.Tuple;
import com.didichuxing.datachannel.arius.admin.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.arius.admin.common.bean.common.Result;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.cluster.ClusterJoinDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.cluster.ClusterPhyConditionDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.cluster.ClusterPhyDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.cluster.ecm.ClusterRoleInfo;
import com.didichuxing.datachannel.arius.admin.common.bean.vo.cluster.ClusterPhyVO;
import com.didichuxing.datachannel.arius.admin.common.bean.vo.cluster.ConsoleClusterPhyVO;
import com.didichuxing.datachannel.arius.admin.common.bean.vo.cluster.ESClusterRoleHostVO;
import com.didichuxing.datachannel.arius.admin.common.bean.vo.cluster.ESClusterRoleVO;
import com.didichuxing.datachannel.arius.admin.common.constant.result.ResultType;
import com.didichuxing.datachannel.arius.admin.common.util.AriusObjUtils;
import com.didichuxing.datachannel.arius.admin.common.util.ConvertUtil;
import com.didichuxing.datachannel.arius.admin.common.util.HttpRequestUtils;
import com.didichuxing.datachannel.arius.admin.core.service.cluster.ecm.ESPackageService;
import com.didichuxing.datachannel.arius.admin.core.service.cluster.ecm.ESPluginService;
import com.didichuxing.datachannel.arius.admin.core.service.cluster.physic.ClusterPhyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.didichuxing.datachannel.arius.admin.common.constant.ApiVersion.V3;
import static com.didichuxing.datachannel.arius.admin.common.constant.ApiVersion.V3_OP;

/**
 * 物理集群接口
 *
 * @author ohushenglin_v
 * @date 2022-05-20
 */
@RestController("esPhyClusterControllerV3")
@RequestMapping({V3_OP + "/phy/cluster",V3+"/cluster/phy"})
@Api(tags = "ES物理集群集群接口(REST)")
public class ESPhyClusterController {

    @Autowired
    private ClusterPhyService esClusterPhyService;

    @Autowired
    private ClusterPhyManager clusterPhyManager;

    @Autowired
    private ESPluginService   esPluginService;

    @Autowired
    private ESPackageService  packageService;

    @Value("${zeus.server}")
    private String              zeusServerUrl;

    /**
     * 根据物理集群ID获取全部角色
     */
    @GetMapping("/{clusterId}/roles")
    @ResponseBody
    @ApiOperation(value = "根据物理集群ID获取全部角色列表", notes = "")
    public Result<List<ESClusterRoleVO>> roleList(@PathVariable Integer clusterId) {
        List<ClusterRoleInfo> clusterRoleInfos = esClusterPhyService.listPhysicClusterRoles(clusterId);

        if (AriusObjUtils.isNull(clusterRoleInfos)) {
            return Result.buildFail(ResultType.NOT_EXIST.getMessage());
        }
        return Result.buildSucc(ConvertUtil.list2List(clusterRoleInfos, ESClusterRoleVO.class));
    }

    @DeleteMapping("/plugin/{id}")
    @ResponseBody
    @ApiOperation(value = "删除插件接口", notes = "")
    @Deprecated
    public Result<Long> pluginDelete(HttpServletRequest request, @PathVariable Long id) {
        return esPluginService.deletePluginById(id, HttpRequestUtils.getOperator(request));
    }

    @DeleteMapping("/package/{id}")
    @ResponseBody
    @ApiOperation(value = "删除程序包接口", notes = "")
    public Result<Long> packageDelete(HttpServletRequest request, @PathVariable Long id) {
        return packageService.deleteESPackage(id, HttpRequestUtils.getOperator(request));
    }

    @PostMapping("/join")
    @ResponseBody
    @ApiOperation(value = "接入集群", notes = "支持多类型集群加入")
    public Result<ClusterPhyVO> joinCluster(HttpServletRequest request, @RequestBody ClusterJoinDTO param) {
        return clusterPhyManager.joinCluster(param, HttpRequestUtils.getOperator(request));
    }

    @PostMapping("/join/{templateSrvId}/checkTemplateService")
    @ResponseBody
    @ApiOperation(value = "集群接入的时候校验是否可以开启指定索引服务")
    @Deprecated
    public Result<Boolean> addTemplateSrvId(HttpServletRequest request,
                                            @RequestBody ClusterJoinDTO clusterJoinDTO,
                                            @PathVariable("templateSrvId") String templateSrvId) {
        return clusterPhyManager.checkTemplateServiceWhenJoin(clusterJoinDTO, templateSrvId, HttpRequestUtils.getOperator(request));
    }

    @GetMapping("/{clusterId}/regions")
    @ResponseBody
    @ApiOperation(value = "获取节点划分列表")
    public Result<List<ESClusterRoleHostVO>> getClusterPhyRegionInfos(@PathVariable Integer clusterId) {
        return clusterPhyManager.getClusterPhyRegionInfos(clusterId);
    }

    @GetMapping("/{clusterLogicType}/{clusterLogicId}/list")
    @ResponseBody
    @ApiOperation(value = "获取逻辑集群可关联region的物理集群名称列表")
    public Result<List<String>> listCanBeAssociatedRegionOfClustersPhys(@PathVariable Integer clusterLogicType,
                                                                        @PathVariable Long clusterLogicId) {
        return clusterPhyManager.listCanBeAssociatedRegionOfClustersPhys(clusterLogicType, clusterLogicId);
    }

    @GetMapping("/{clusterLogicType}/list")
    @ResponseBody
    @ApiOperation(value = "获取逻辑集群可进行关联的物理集群名称")
    public Result<List<String>> listCanBeAssociatedClustersPhys(@PathVariable Integer clusterLogicType) {
        return clusterPhyManager.listCanBeAssociatedClustersPhys(clusterLogicType);
    }

    @GetMapping("/names")
    @ResponseBody
    @ApiOperation(value = "根据AppId获取逻辑集群下的物理集群名称")
    public Result<List<String>> getClusterPhyNames(HttpServletRequest request) {
        return Result.buildSucc(clusterPhyManager.getAppClusterPhyNames(HttpRequestUtils.getAppId(request)));
    }

    @GetMapping("/{templateId}/sameversion/clusternames")
    @ResponseBody
    @ApiOperation(value = "根据模板所在集群，获取与该集群相同版本号的集群名称列表")
    public Result<List<String>> getTemplateSameVersionClusterNamesByTemplateId(HttpServletRequest request, @PathVariable Integer templateId) {
        return clusterPhyManager.getTemplateSameVersionClusterNamesByTemplateId(HttpRequestUtils.getAppId(request), templateId);
    }

    @GetMapping("/{clusterPhyName}/nodes")
    @ResponseBody
    @ApiOperation(value = "根据AppId获取物理集群下的节点名称")
    public Result<List<String>> getAppClusterPhyNodeNames(@PathVariable String clusterPhyName) {
        return Result.buildSucc(clusterPhyManager.getAppClusterPhyNodeNames(clusterPhyName));
    }

    @GetMapping("/node/names")
    @ResponseBody
    @ApiOperation(value = "根据AppId获取物理集群下的节点名称")
    public Result<List<String>> getAppNodeNames(HttpServletRequest request) {
        return Result.buildSucc(clusterPhyManager.getAppNodeNames(HttpRequestUtils.getAppId(request)));
    }

    @PostMapping("/page")
    @ResponseBody
    @ApiOperation(value = "按条件分页获取物理集群列表")
    public PaginationResult<ClusterPhyVO> pageGetClusterPhys(HttpServletRequest request,
                                                                         @RequestBody ClusterPhyConditionDTO condition) {
        return clusterPhyManager.pageGetClusterPhys(condition, HttpRequestUtils.getAppId(request));
    }

    @GetMapping("/{clusterPhyId}/overview")
    @ResponseBody
    @ApiOperation(value = "获取物理集群概览信息接口")
    @ApiImplicitParam(type = "Integer", name = "clusterPhyId", value = "物理集群ID", required = true)
    public Result<ConsoleClusterPhyVO> overview(@PathVariable("clusterPhyId") Integer clusterId, HttpServletRequest request) {
        return Result.buildSucc(clusterPhyManager.getConsoleClusterPhy(clusterId, HttpRequestUtils.getAppId(request)));
    }

    @GetMapping("/{clusterLogicType}/{clusterName}/version/list")
    @ResponseBody
    @ApiOperation(value = "根据逻辑集群类型和物理集群名称获取相同版本的可关联的物理名称列表")
    public Result<List<String>> getPhyClusterNameWithSameEsVersion(@PathVariable("clusterLogicType") Integer clusterLogicType, @PathVariable(name = "clusterName", required = false) String clusterName) {
        return clusterPhyManager.getPhyClusterNameWithSameEsVersion(clusterLogicType, clusterName);
    }

    @GetMapping("/{clusterLogicId}/bind/version/list")
    @ResponseBody
    @ApiOperation(value = "新建的逻辑集群绑定region的时候进行物理集群版本的校验")
    public Result<List<String>> getPhyClusterNameWithSameEsVersionAfterBuildLogic(@PathVariable("clusterLogicId") Long clusterLogicId) {
        return clusterPhyManager.getPhyClusterNameWithSameEsVersionAfterBuildLogic(clusterLogicId);
    }

    @GetMapping("/{clusterPhy}/{clusterLogic}/{templateSize}/bindRack")
    @ResponseBody
    @ApiOperation(value = "根据物理集群名称和当前模板审批的工单获取可以绑定的rack列表")
    @Deprecated
    public Result<Set<String>> getValidRacksListByDiskSize(@PathVariable("clusterPhy") String clusterPhy,
                                                @PathVariable("clusterLogic") String clusterLogic,
                                                @PathVariable("templateSize") String templateSize) {
        return clusterPhyManager.getValidRacksListByTemplateSize(clusterPhy, clusterLogic, templateSize);
    }

    @GetMapping("/zeus-url")
    @ResponseBody
    @ApiOperation(value = "获取zeus管控平台跳转接口")
    public Result<String> zeusUrl() {
        return Result.buildSucc(zeusServerUrl);
    }

    @PutMapping("/gateway")
    @ResponseBody
    @ApiOperation(value = "更新物理集群的gateway" )
    public Result<ClusterPhyVO> updateClusterGateway(HttpServletRequest request, @RequestBody ClusterPhyDTO param) {
        return clusterPhyManager.updateClusterGateway(param, HttpRequestUtils.getOperator(request),HttpRequestUtils.getAppId(request));
    }
}
