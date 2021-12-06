package com.didichuxing.datachannel.arius.admin.biz.template.impl;

import static com.didichuxing.datachannel.arius.admin.client.constant.app.AppTemplateAuthEnum.OWN;
import static com.didichuxing.datachannel.arius.admin.client.constant.app.AppTemplateAuthEnum.isTemplateAuthExitByCode;
import static com.didichuxing.datachannel.arius.admin.client.constant.operaterecord.ModuleEnum.TEMPLATE;
import static com.didichuxing.datachannel.arius.admin.client.constant.operaterecord.OperationEnum.ADD;
import static com.didichuxing.datachannel.arius.admin.common.constant.PageSearchHandleTypeEnum.TEMPLATE_LOGIC;
import static com.didichuxing.datachannel.arius.admin.common.constant.TemplateConstant.*;
import static com.didichuxing.datachannel.arius.admin.common.constant.cache.CacheGlobalNamesContent.CACHE_GLOBAL_NAME;
import static com.didichuxing.datachannel.arius.admin.common.constant.template.TemplateServiceEnum.TEMPLATE_MAPPING;
import static com.didichuxing.datachannel.arius.admin.core.service.template.physic.impl.TemplatePhyServiceImpl.NOT_CHECK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.didichuxing.datachannel.arius.admin.biz.cluster.ClusterContextManager;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.cluster.ClusterLogicContext;
import com.didichuxing.datachannel.arius.admin.common.util.ListUtils;
import com.didichuxing.datachannel.arius.admin.core.service.cluster.region.RegionRackService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.arius.admin.biz.page.TemplateLogicPageSearchHandle;
import com.didichuxing.datachannel.arius.admin.biz.template.TemplateLogicManager;
import com.didichuxing.datachannel.arius.admin.biz.template.TemplatePhyManager;
import com.didichuxing.datachannel.arius.admin.biz.template.srv.cold.TemplateColdManager;
import com.didichuxing.datachannel.arius.admin.biz.template.srv.quota.TemplateQuotaManager;
import com.didichuxing.datachannel.arius.admin.client.bean.common.IndexTemplateValue;
import com.didichuxing.datachannel.arius.admin.client.bean.common.PaginationResult;
import com.didichuxing.datachannel.arius.admin.client.bean.common.QuotaUsage;
import com.didichuxing.datachannel.arius.admin.client.bean.common.Result;
import com.didichuxing.datachannel.arius.admin.client.bean.common.TemplateLabel;
import com.didichuxing.datachannel.arius.admin.client.bean.dto.template.IndexTemplateLogicDTO;
import com.didichuxing.datachannel.arius.admin.client.bean.dto.template.IndexTemplatePhysicalDTO;
import com.didichuxing.datachannel.arius.admin.client.bean.dto.template.TemplateConditionDTO;
import com.didichuxing.datachannel.arius.admin.client.bean.vo.template.ConsoleTemplateVO;
import com.didichuxing.datachannel.arius.admin.client.constant.app.AppTemplateAuthEnum;
import com.didichuxing.datachannel.arius.admin.client.constant.result.ResultType;
import com.didichuxing.datachannel.arius.admin.client.constant.template.TemplateDeployRoleEnum;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.app.App;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.app.AppTemplateAuth;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.cluster.ClusterLogic;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.cluster.ClusterPhy;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.quota.ESTemplateQuotaUsage;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.template.IndexTemplateConfig;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.template.IndexTemplateLogic;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.template.IndexTemplateLogicAggregate;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.template.IndexTemplateLogicWithCluster;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.template.IndexTemplateLogicWithLabels;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.template.IndexTemplatePhy;
import com.didichuxing.datachannel.arius.admin.common.component.BaseHandle;
import com.didichuxing.datachannel.arius.admin.common.component.HandleFactory;
import com.didichuxing.datachannel.arius.admin.common.component.SpringTool;
import com.didichuxing.datachannel.arius.admin.common.constant.AdminConstant;
import com.didichuxing.datachannel.arius.admin.common.event.template.LogicTemplateAddEvent;
import com.didichuxing.datachannel.arius.admin.common.exception.AdminOperateException;
import com.didichuxing.datachannel.arius.admin.common.exception.AmsRemoteException;
import com.didichuxing.datachannel.arius.admin.common.util.AriusObjUtils;
import com.didichuxing.datachannel.arius.admin.common.util.ConvertUtil;
import com.didichuxing.datachannel.arius.admin.core.component.ResponsibleConvertTool;
import com.didichuxing.datachannel.arius.admin.core.notify.NotifyTaskTypeEnum;
import com.didichuxing.datachannel.arius.admin.core.notify.info.template.TemplateLogicMetaErrorNotifyInfo;
import com.didichuxing.datachannel.arius.admin.core.notify.service.NotifyService;
import com.didichuxing.datachannel.arius.admin.core.service.app.AppLogicTemplateAuthService;
import com.didichuxing.datachannel.arius.admin.core.service.app.AppService;
import com.didichuxing.datachannel.arius.admin.core.service.cluster.logic.ClusterLogicService;
import com.didichuxing.datachannel.arius.admin.core.service.cluster.physic.ClusterPhyService;
import com.didichuxing.datachannel.arius.admin.core.service.common.OperateRecordService;
import com.didichuxing.datachannel.arius.admin.core.service.template.logic.TemplateLogicService;
import com.didichuxing.datachannel.arius.admin.core.service.template.physic.TemplatePhyService;
import com.didichuxing.datachannel.arius.admin.metadata.service.TemplateLabelService;
import com.didichuxing.datachannel.arius.admin.metadata.service.TemplateSattisService;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class TemplateLogicManagerImpl implements TemplateLogicManager {

    private static final ILog           LOGGER = LogFactory.getLog(TemplateLogicManager.class);

    @Autowired
    private AppLogicTemplateAuthService appLogicTemplateAuthService;

    @Autowired
    private TemplateQuotaManager        templateQuotaManager;

    @Autowired
    private TemplateSattisService       templateSattisService;

    @Autowired
    private TemplateLabelService        templateLabelService;

    @Autowired
    private TemplateColdManager         templateColdManager;

    @Autowired
    private TemplateLogicService        templateLogicService;

    @Autowired
    private TemplatePhyService          templatePhyService;

    @Autowired
    private ClusterPhyService           clusterPhyService;

    @Autowired
    private OperateRecordService        operateRecordService;

    @Autowired
    private AppService                  appService;

    @Autowired
    private ResponsibleConvertTool      responsibleConvertTool;

    @Autowired
    private ClusterLogicService         clusterLogicService;

    @Autowired
    private TemplatePhyManager          templatePhyManager;

    @Autowired
    private NotifyService               notifyService;

    @Autowired
    private HandleFactory               handleFactory;

    @Autowired
    private ClusterContextManager clusterContextManager;

    /**
     * 校验所有逻辑模板元数据信息
     *
     * @return
     */
    @Override
    public boolean checkAllLogicTemplatesMeta() {
        Map<Integer, App> appId2AppMap = ConvertUtil.list2Map(appService.listApps(), App::getId);
        List<IndexTemplateLogic> logicTemplates = templateLogicService.getAllLogicTemplates();
        for (IndexTemplateLogic templateLogic : logicTemplates) {
            try {
                Result<Void> result = checkLogicTemplateMeta(templateLogic, appId2AppMap);
                if (result.success()) {
                    LOGGER.info("class=TemplateLogicManagerImpl||method=metaCheck||msg=succeed||logicId={}", templateLogic.getId());
                } else {
                    LOGGER.warn("class=TemplateLogicManagerImpl||method=metaCheck||msg=fail||logicId={}||failMsg={}", templateLogic.getId(),
                            result.getMessage());
                    notifyService.send( NotifyTaskTypeEnum.TEMPLATE_LOGICAL_META_ERROR,
                            new TemplateLogicMetaErrorNotifyInfo(templateLogic, result.getMessage()), Arrays.asList());
                }
            } catch (Exception e) {
                LOGGER.error("class=TemplateLogicServiceImpl||method=metaCheck||errMsg={}||logicId={}||",
                        e.getMessage(), templateLogic.getId(), e);
            }
        }

        return true;
    }

    /**
     * 获取模板信息
     * @param excludeLabelIds 排除的Label ID列表
     * @param includeLabelIds 包含的Label ID列表
     * @return list
     */
    @Override
    public List<IndexTemplateLogicWithLabels> getByLabelIds(String includeLabelIds, String excludeLabelIds) {

        List<IndexTemplateLogicWithLabels> indexTemplateLogicWithLabels = Lists.newArrayList();

        Map<Integer, IndexTemplateLogic> logicTemplatesMappings = templateLogicService.getAllLogicTemplatesMap();

        List<TemplateLabel> templateLabels = fetchLabels(includeLabelIds, excludeLabelIds);
        templateLabels.stream().forEach(templateLabel -> {
            Integer templateId = templateLabel.getIndexTemplateId();

            IndexTemplateLogic indexTemplateLogic = logicTemplatesMappings.get(templateId);
            IndexTemplateLogicWithLabels logicWithLabel = responsibleConvertTool.obj2Obj(indexTemplateLogic,
                    IndexTemplateLogicWithLabels.class);
            if (logicWithLabel != null) {
                logicWithLabel.setLabels(templateLabel.getLabels());
                indexTemplateLogicWithLabels.add(logicWithLabel);
            }

        });

        return indexTemplateLogicWithLabels;
    }

    /**
     * 获取最近访问该模板的APP
     *
     * @param logicId logicId
     * @return result
     */
    @Override
    public List<App> getLogicTemplateAppAccess(Integer logicId) {
        Result<Map<Integer, Long>> result = templateSattisService.getTemplateAccessAppIds(logicId, 7);
        if (result.failed()) {
            throw new AmsRemoteException("获取访问模板的APP列表失败");
        }

        if (null == result.getData() || 0 == result.getData().size()) {
            return Lists.newArrayList();
        }

        List<App> apps = appService.listApps();
        Map<Integer, App> id2AppMap = ConvertUtil.list2Map(apps, App::getId);

        return result.getData().keySet().stream().map(id2AppMap::get).collect( Collectors.toList());
    }

    /**
     * 获取模板的标签信息
     * @param logicId 模板id
     * @return label
     */
    @Override
    public IndexTemplateLogicWithLabels getLabelByLogicId(Integer logicId) {
        IndexTemplateLogicWithLabels indexTemplateLogicWithLabels = ConvertUtil
                .obj2Obj(templateLogicService.getLogicTemplateById(logicId), IndexTemplateLogicWithLabels.class);

        if (indexTemplateLogicWithLabels != null) {
            indexTemplateLogicWithLabels.setLabels(templateLabelService.listTemplateLabel(logicId));
        }

        return indexTemplateLogicWithLabels;
    }

    /**
     * 新建逻辑模板 无参数校验
     *
     * @param param    模板信息
     * @param operator 操作人
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> addTemplateWithoutCheck(IndexTemplateLogicDTO param,
                                                   String operator) throws AdminOperateException {
        initLogicParam(param);

        // 初始化pipelineID
        param.setIngestPipeline(param.getName());

        // 保存数据库
        Result<Void> result = templateLogicService.addTemplateWithoutCheck(param);
        if (result.success()) {
            Result<Void> addPhysicalResult = templatePhyManager.addTemplatesWithoutCheck(param.getId(),
                    param.getPhysicalInfos());

            if (addPhysicalResult.failed()) {
                throw new AdminOperateException("新建物理模板失败");
            }

            IndexTemplateConfig defaultTemplateConfig = getDefaultTemplateConfig(param.getId());
            if (param.getDisableSourceFlags() != null) {
                defaultTemplateConfig.setDisableSourceFlags(param.getDisableSourceFlags());
            }

            if (param.getPreCreateFlags() != null) {
                defaultTemplateConfig.setPreCreateFlags(param.getPreCreateFlags());
            }

            if (param.getShardNum() != null) {
                defaultTemplateConfig.setShardNum(param.getShardNum());
            }

            // 生成配置记录
            insertTemplateConfig(defaultTemplateConfig);

            // 记录操作记录
            operateRecordService.save(TEMPLATE, ADD, param.getId(), "", operator);

            SpringTool.publish(new LogicTemplateAddEvent(this, templateLogicService.getLogicTemplateById(param.getId())));
        }

        return Result.build(result.success(), param.getId());
    }

    /**
     * 新建逻辑模板
     * @param param 模板信息
     * @param operator 操作人
     * @return result
     * @throws AdminOperateException 操作es失败 或者保存物理模板信息失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Result<Integer> createLogicTemplate(IndexTemplateLogicDTO param,
                                               String operator) throws AdminOperateException {
        Result<Void> checkResult = templateLogicService.validateTemplate(param, ADD);
        if (checkResult.failed()) {
            LOGGER.warn("class=TemplateLogicServiceImpl||method=addTemplate||msg={}", checkResult.getMessage());
            return Result.buildFrom(checkResult);
        }

        LOGGER.info("class=TemplateLogicServiceImpl||method=addTemplate||id={}||hotTime={}||name={}||physicalInfos={}",
                param.getId(), param.getHotTime(), param.getName(), JSON.toJSONString(param.getPhysicalInfos()));

        if (param.getPhysicalInfos() != null) {
            setIndexTemplateLogicHotTime(param);
        }

        checkResult = templatePhyManager.validateTemplates(param.getPhysicalInfos(), ADD);
        if (checkResult.failed()) {
            LOGGER.warn("class=TemplateLogicServiceImpl||method=addTemplate||msg={}", checkResult.getMessage());
            return Result.buildFrom(checkResult);
        }

        // 校验资源与模板的数据中心是否匹配
        List<String> clusters = param.getPhysicalInfos().stream().map(IndexTemplatePhysicalDTO::getCluster)
                .collect(Collectors.toList());
        for (String cluster : clusters) {
            ClusterPhy clusterPhy = clusterPhyService.getClusterByName(cluster);
            if (!clusterPhy.getDataCenter().equals(param.getDataCenter())) {
                return Result.buildParamIllegal("物理集群数据中心不符");
            }
        }

        return addTemplateWithoutCheck(param, operator);
    }

    private void setIndexTemplateLogicHotTime(IndexTemplateLogicDTO param) {
        for (IndexTemplatePhysicalDTO physicalDTO : param.getPhysicalInfos()) {
            physicalDTO.setLogicId(NOT_CHECK);
            physicalDTO.setName(param.getName());
            physicalDTO.setExpression(param.getExpression());
            physicalDTO.setWriteRateLimit(param.getWriteRateLimit());

            if (param.getHotTime() == null || param.getHotTime() <= 0) {
                int clusterSettingHotDay = templateColdManager.fetchClusterDefaultHotDay(physicalDTO.getCluster());
                if (clusterSettingHotDay > 0) {
                    param.setHotTime(clusterSettingHotDay);
                }
            }
        }
    }

    /**
     * 获取所有逻辑模板聚合
     *
     * @param appId 当前App Id
     * @return
     */
    @Override
    public List<IndexTemplateLogicAggregate> getAllTemplatesAggregate(Integer appId) {
        List<IndexTemplateLogicAggregate> indexTemplateLogicAggregates = new ArrayList<>();
        List<IndexTemplateLogicWithCluster> logicTemplates = templateLogicService
                .getAllLogicTemplateWithClusters();

        if (CollectionUtils.isNotEmpty(logicTemplates)) {
            indexTemplateLogicAggregates = fetchLogicTemplatesAggregates(logicTemplates, appId);
        }

        return indexTemplateLogicAggregates;
    }

    /**
     * 获取逻辑集群所有逻辑模板聚合
     *
     * @param logicClusterId 逻辑集群ID
     * @param appId 操作的App Id
     * @return
     */
    @Override
    public List<IndexTemplateLogicAggregate> getLogicClusterTemplatesAggregate(Long logicClusterId, Integer appId) {

        if (logicClusterId == null) {
            return new ArrayList<>();
        }

        List<IndexTemplateLogicWithCluster> logicTemplates = templateLogicService
                .getLogicTemplateWithClustersByClusterId(logicClusterId);

        if (CollectionUtils.isEmpty(logicTemplates)) {
            return new ArrayList<>();
        }

        return fetchLogicTemplatesAggregates(logicTemplates, appId);
    }

    /**
     * 拼接集群名称
     * @param logicClusters 逻辑集群详情列表
     * @return
     */
    @Override
    public String jointCluster(List<ClusterLogic> logicClusters) {
        if (CollectionUtils.isNotEmpty(logicClusters)) {
            return String.join(",", logicClusters.stream().
                    map(ClusterLogic::getName).collect(
                    Collectors.toList()));
        }

        return StringUtils.EMPTY;
    }

    /**
     *
     * @param aggregates 聚合列表
     * @return
     */
    @Override
    public List<ConsoleTemplateVO> fetchConsoleTemplates(List<IndexTemplateLogicAggregate> aggregates) {
        List<ConsoleTemplateVO> consoleTemplates = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(aggregates)) {
            Map<Integer, String> appId2AppNameMap = Maps.newHashMap();

            for (IndexTemplateLogicAggregate aggregate : aggregates) {
                ConsoleTemplateVO consoleTemplateVO = fetchConsoleTemplate(aggregate);

                //获取项目名称
                Integer appId = consoleTemplateVO.getAppId();
                if (appId2AppNameMap.containsKey(appId)) {
                    consoleTemplateVO.setAppName(appId2AppNameMap.get(appId));
                } else {
                    String appName = appService.getAppName(appId);
                    if (!AriusObjUtils.isNull(appName)) {
                        consoleTemplateVO.setAppName(appName);
                        appId2AppNameMap.put(appId, appName);
                    }
                }

                consoleTemplates.add(consoleTemplateVO);
            }
        }

        Collections.sort(consoleTemplates);
        return consoleTemplates;
    }

    /**
     * 获取模板VO
     * @param aggregate 模板聚合
     * @return
     */
    @Override
    public ConsoleTemplateVO fetchConsoleTemplate(IndexTemplateLogicAggregate aggregate) {
        if (aggregate != null) {
            ConsoleTemplateVO templateLogic = ConvertUtil.obj2Obj(
                    aggregate.getIndexTemplateLogicWithCluster(),
                    ConsoleTemplateVO.class);
            try {
                templateLogic.setAuthType(AppTemplateAuthEnum.NO_PERMISSION.getCode());
                if (aggregate.getAppTemplateAuth() != null) {
                    templateLogic.setAuthType(aggregate.getAppTemplateAuth().getType());
                }

                templateLogic.setValue(DEFAULT_TEMPLATE_VALUE);
                if (aggregate.getIndexTemplateValue() != null) {

                    templateLogic.setValue(aggregate.getIndexTemplateValue().getValue());
                }

                if (aggregate.getEsTemplateQuotaUsage() != null) {
                    templateLogic.setQuotaUsage(ConvertUtil.obj2Obj(
                            aggregate.getEsTemplateQuotaUsage(), QuotaUsage.class));
                }
                templateLogic.setHasDCDR(templateLogic.getHasDCDR());
                
                //设置模板关联物理集群
                List<IndexTemplatePhy> templatePhyList = templatePhyService.getTemplateByLogicId(templateLogic.getId());
                if (CollectionUtils.isNotEmpty(templatePhyList)) {
                    templateLogic.setClusterPhies(
                            templatePhyList.stream().map(IndexTemplatePhy::getCluster).collect(Collectors.toList()));
                }
            } catch (Exception e) {
                LOGGER.warn("class=TemplateLogicManager||method=fetchConsoleTemplate||aggregate={}",
                        aggregate, e);
            }

            return templateLogic;
        }

        return null;
    }

    @Override
    @Cacheable(cacheNames = CACHE_GLOBAL_NAME, key = "#appId + '@' + #clusterLogicId +'@' + 'getConsoleTemplatesVOS'")
    public List<ConsoleTemplateVO> getConsoleTemplateVOSForClusterLogic(Long clusterLogicId, Integer appId) {
        if (AriusObjUtils.isNull(clusterLogicId)) {
            return Lists.newArrayList();
        }

        List<IndexTemplateLogic> logicClusterTemplates = templateLogicService.getLogicClusterTemplates(clusterLogicId);

        Set<Integer> templateLogicIds = logicClusterTemplates.stream().map(IndexTemplateLogic::getId)
                .collect(Collectors.toSet());

        return getConsoleTemplatesVOS(appId)
                .stream()
                .filter(r -> templateLogicIds.contains(r.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = CACHE_GLOBAL_NAME, key = "#appId + '@' + 'getConsoleTemplatesVOS'")
    public List<ConsoleTemplateVO> getConsoleTemplatesVOS(Integer appId) {
        return fetchConsoleTemplates(getAllTemplatesAggregate(appId));
    }

    @Override
    public List<IndexTemplateLogic> getTemplatesByAppIdAndAuthType(Integer appId, Integer authType) {
        if (!appService.isAppExists(appId)) {
            return Lists.newArrayList();
        }

        //超级用户对所有模板都是管理权限
        if (appService.isSuperApp(appId) && !OWN.getCode().equals(authType)) {
            return Lists.newArrayList();
        }

        if (!isTemplateAuthExitByCode(authType)) {
            return Lists.newArrayList();
        }

        switch (AppTemplateAuthEnum.valueOf(authType)) {
            case OWN:
                if (appService.isSuperApp(appId)) {
                    return templateLogicService.getAllLogicTemplates();
                }else {
                    return templateLogicService.getAppLogicTemplatesByAppId(appId);
                }

            case RW:
                List<AppTemplateAuth> appActiveTemplateRWAuths = appLogicTemplateAuthService
                    .getAppActiveTemplateRWAuths(appId);
                return appActiveTemplateRWAuths
                        .stream()
                        .map(r -> templateLogicService.getLogicTemplateById(r.getTemplateId()))
                        .collect(Collectors.toList());

            case R:
                List<AppTemplateAuth> appActiveTemplateRAuths = appLogicTemplateAuthService
                    .getAppActiveTemplateRAuths(appId);
                return appActiveTemplateRAuths
                        .stream()
                        .map(r -> templateLogicService.getLogicTemplateById(r.getTemplateId()))
                        .collect(Collectors.toList());

            case NO_PERMISSION:
                List<IndexTemplateLogic> allLogicTemplates = templateLogicService.getAllLogicTemplates();
                List<Integer> appRAndRwAuthTemplateIdList = appLogicTemplateAuthService
                        .getAppTemplateRWAndRAuthsWithoutCodecResponsible(appId)
                        .stream()
                        .map(AppTemplateAuth::getTemplateId)
                        .collect(Collectors.toList());

                List<IndexTemplateLogic> notAuthIndexTemplateLogicList = allLogicTemplates
                        .stream()
                        .filter(r -> !appId.equals(r.getAppId()) && !appRAndRwAuthTemplateIdList.contains(r.getId()))
                        .collect(Collectors.toList());
                return notAuthIndexTemplateLogicList;

            default:
                return Lists.newArrayList();

        }
    }

    @Override
    public List<String> getTemplateLogicNames(Integer appId) {
        List<IndexTemplateLogic> templateLogics = templateLogicService.getAppLogicTemplatesByAppId(appId);

        return templateLogics.stream().map(IndexTemplateLogic::getName).collect(Collectors.toList());
    }

    @Override
    public Result<Void> editTemplate(IndexTemplateLogicDTO param, String operator)
            throws AdminOperateException {
        return templateLogicService.editTemplate(param, operator);
    }

    @Override
    public Result<Void> delTemplate(Integer logicTemplateId, String operator)
            throws AdminOperateException {
        return templateLogicService.delTemplate(logicTemplateId, operator);
    }

    @Override
    public PaginationResult<ConsoleTemplateVO> pageGetConsoleTemplateVOS(TemplateConditionDTO condition, Integer appId) {
        BaseHandle baseHandle     = handleFactory.getByHandlerNamePer(TEMPLATE_LOGIC.getPageSearchType());
        if (baseHandle instanceof TemplateLogicPageSearchHandle) {
            TemplateLogicPageSearchHandle handle = (TemplateLogicPageSearchHandle) baseHandle;
            return handle.doPageHandle(condition, condition.getAuthType(), appId);
        }

        LOGGER.warn("class=TemplateLogicManagerImpl||method=pageGetConsoleClusterVOS||msg=failed to get the TemplateLogicPageSearchHandle");

        return PaginationResult.buildFail("获取模板分页信息失败");
    }

    @Override
    public Result<Void> checkTemplateValidForCreate(String templateName) {
        if (AriusObjUtils.isNull(templateName)) {
            return Result.buildParamIllegal("名字为空");
        }

        if (templateName.length() < TEMPLATE_NAME_SIZE_MIN || templateName.length() > TEMPLATE_NAME_SIZE_MAX) {
            return Result.buildParamIllegal("名称长度非法, 1-128");
        }

        for (Character c : templateName.toCharArray()) {
            if (!TEMPLATE_NAME_CHAR_SET.contains(c)) {
                return Result.buildParamIllegal("名称包含非法字符, 只能包含小写字母、数字、-、_和.");
            }
        }

        if (CollectionUtils.isNotEmpty(templateLogicService.getLogicTemplateByName(templateName))) {
            return Result.buildDuplicate("模板名称已经存在");
        }

        return Result.buildSucc();
    }

    @Override
    public Result<Void> checkTemplateDataSizeValidForCreate(IndexTemplateLogicDTO param) {

        return Result.buildSucc();
    }

    @Override
    public Result<Boolean> checkTemplateEditMapping(Integer templateId) {
        IndexTemplateLogic indexTemplateLogic = templateLogicService.getLogicTemplateById(templateId);
        if (null == indexTemplateLogic) {
            LOGGER.error(
                "class=TemplateLogicManagerImpl||method=checkTemplateEditMapping||templateId={}||msg=indexTemplateLogic is empty",
                templateId);
            return Result.buildFail("模板不存在");
        }

        List<IndexTemplatePhy> templatePhyList = templatePhyService.getTemplateByLogicId(indexTemplateLogic.getId());
        if (CollectionUtils.isEmpty(templatePhyList)) {
            return Result.buildSucc(false);
        }

        List<String> clusterPhyNameList = templatePhyList.stream().map(IndexTemplatePhy::getCluster).distinct().collect(Collectors.toList());
        for (String clusterPhyName : clusterPhyNameList) {
            ClusterPhy clusterPhy = clusterPhyService.getClusterByName(clusterPhyName);
            if (null == clusterPhy) {
                return Result.buildFail(String.format("模板归属集群[%s]不存在", clusterPhyName));
            }
            
            List<String> templateSrvList = ListUtils.string2StrList(clusterPhy.getTemplateSrvs());
            if (!templateSrvList.contains(TEMPLATE_MAPPING.getCode().toString())){
                return Result.buildFail("该模板归属集群未开启mapping修改服务");
            }
        }

        return Result.buildSucc(true);
    }

    /**************************************** private method ***************************************************/
    /**
     * 校验逻辑模板Master ROLE物理模板是否存在
     * @param templateLogic 逻辑模板
     * @param appId2AppMap APP映射
     * @return
     */
    private Result<Void> checkLogicTemplateMeta(IndexTemplateLogic templateLogic, Map<Integer, App> appId2AppMap) {
        List<String> errMsg = Lists.newArrayList();

        if (!appId2AppMap.containsKey(templateLogic.getAppId())) {
            errMsg.add("所属APP ID不存在：" + templateLogic.getAppId());
        }

        List<IndexTemplatePhy> templatePhysicals = templatePhyService.getTemplateByLogicId(templateLogic.getId());

        if (CollectionUtils.isNotEmpty(templatePhysicals)) {
            List<IndexTemplatePhy> templatePhysicalsMaster = templatePhysicals.stream()
                    .filter(templatePhysical -> templatePhysical.getRole().equals( TemplateDeployRoleEnum.MASTER.getCode()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(templatePhysicalsMaster)) {
                errMsg.add("没有部署master：" + templateLogic.getName() + "(" + templateLogic.getId() + ")");
            }
        }

        if (CollectionUtils.isEmpty(errMsg)) {
            return Result.buildSucc();
        }

        return Result.build( ResultType.ADMIN_META_ERROR.getCode(), String.join(",", errMsg));
    }

    /**
     * 获取逻辑模板标签列表
     * @param includeLabelIds 包含的标签ID列表
     * @param excludeLabelIds 排除的标签ID列表
     * @return
     */
    private List<TemplateLabel> fetchLabels(String includeLabelIds, String excludeLabelIds) {
        Result<List<TemplateLabel>> result = templateLabelService.listByLabelIds(includeLabelIds,
                excludeLabelIds);
        if (result.failed()) {
            throw new AmsRemoteException("获取模板标签失败");
        }

        return result.getData();
    }

    /**
     * 获取逻辑模板详情
     *
     * @param indexTemplateLogicWithCluster 逻辑集群
     * @param appTemplateAuths                 App模板权限
     * @param templateQuotaUsages              逻辑模板Quota使用率
     * @param logicTemplateValues              逻辑模板健康分
     */
    private IndexTemplateLogicAggregate fetchTemplateAggregate(IndexTemplateLogicWithCluster indexTemplateLogicWithCluster,
                                                               Map<Integer, AppTemplateAuth> appTemplateAuths,
                                                               Map<Integer, ESTemplateQuotaUsage> templateQuotaUsages,
                                                               Map<Integer, IndexTemplateValue> logicTemplateValues,
                                                               List<Integer> hasDCDRLogicIds) {

        IndexTemplateLogicAggregate indexTemplateLogicAggregate = new IndexTemplateLogicAggregate();

        indexTemplateLogicAggregate.setIndexTemplateLogicWithCluster(indexTemplateLogicWithCluster);
        indexTemplateLogicAggregate.setAppTemplateAuth(appTemplateAuths.get(indexTemplateLogicWithCluster.getId()));
        indexTemplateLogicAggregate.setEsTemplateQuotaUsage(templateQuotaUsages.get(indexTemplateLogicWithCluster.getId()));
        indexTemplateLogicAggregate.setIndexTemplateValue(logicTemplateValues.get(indexTemplateLogicWithCluster.getId()));
        indexTemplateLogicAggregate.setHasDCDR(hasDCDRLogicIds.contains(indexTemplateLogicWithCluster.getId()));

        return indexTemplateLogicAggregate;
    }

    /**
     * 获取模板价值分
     *
     * @return
     */
    private List<IndexTemplateValue> fetchTemplateValues() {
        List<IndexTemplateValue> templateValues = Lists.newArrayList();
        Result<List<IndexTemplateValue>> listTemplateValueResult = templateSattisService.listTemplateValue();
        if (listTemplateValueResult.success()) {
            templateValues.addAll(listTemplateValueResult.getData());
        }

        return templateValues;
    }

    /**
     * 获取逻辑模板聚合信息
     * @param logicTemplates 逻辑模板列表
     * @param appId App Id
     * @return
     */
    private List<IndexTemplateLogicAggregate> fetchLogicTemplatesAggregates(List<IndexTemplateLogicWithCluster> logicTemplates,
                                                                            Integer appId) {
        List<IndexTemplateLogicAggregate> indexTemplateLogicAggregates = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(logicTemplates)) {

            // 模板权限
            Map<Integer, AppTemplateAuth> appTemplateAuths = ConvertUtil
                    .list2Map(appLogicTemplateAuthService.getTemplateAuthsByAppId(appId), AppTemplateAuth::getTemplateId);

            // quota
            Map<Integer, ESTemplateQuotaUsage> templateQuotaUsages = ConvertUtil
                    .list2Map( templateQuotaManager.listAllTemplateQuotaUsageWithCache(), ESTemplateQuotaUsage::getLogicId);

            // 模板
            Map<Integer, IndexTemplateValue> logicTemplateValues = ConvertUtil.list2Map(fetchTemplateValues(),
                    IndexTemplateValue::getLogicTemplateId);

            // 具备DCDR的模版id
            List<Integer> hasDCDRLogicIds = getHaveDCDRLogicIds();

            for (IndexTemplateLogicWithCluster combineLogicCluster : logicTemplates) {
                try {
                    indexTemplateLogicAggregates.add(fetchTemplateAggregate(combineLogicCluster, appTemplateAuths,
                            templateQuotaUsages, logicTemplateValues, hasDCDRLogicIds));
                } catch (Exception e) {
                    LOGGER.warn(
                            "class=LogicTemplateManager||method=fetchLogicTemplatesAggregates||" + "combineLogicCluster={}",
                            combineLogicCluster, e);
                }
            }
        }

        return indexTemplateLogicAggregates;
    }

    private List<Integer> getHaveDCDRLogicIds() {
        Result<List<TemplateLabel>> result = templateLabelService.listHaveDcdrTemplates();
        if (result.failed() || CollectionUtils.isEmpty(result.getData())) {
            return Lists.newArrayList();
        }

        return result.getData().stream().map(TemplateLabel::getIndexTemplateId).collect(Collectors.toList());
    }

    private void initLogicParam(IndexTemplateLogicDTO param) {
        if (param.getDateFormat() == null) {
            param.setDateFormat("");
        } else {
            param.setDateFormat(param.getDateFormat().replace("Y", "y"));
        }

        if (param.getDateField() == null) {
            param.setDateField("");
        }

        if (param.getDateFieldFormat() == null) {
            param.setDateFieldFormat("");
        }

        if (param.getIdField() == null) {
            param.setIdField("");
        }

        if (param.getRoutingField() == null) {
            param.setRoutingField("");
        }

        if (param.getDesc() == null) {
            param.setDesc("");
        }

        if (param.getLibraDepartment() == null) {
            param.setLibraDepartment("");
        }

        if (param.getLibraDepartmentId() == null) {
            param.setLibraDepartmentId("");
        }

        if (param.getHotTime() == null) {
            param.setHotTime(-1);
        }

        if (param.getDisableSourceFlags() == null) {
            param.setDisableSourceFlags(false);
        }

        if (param.getPreCreateFlags() == null) {
            param.setPreCreateFlags(true);
        }

        if (param.getShardNum() == null) {
            param.setShardNum(-1);
        }
    }

    private IndexTemplateConfig getDefaultTemplateConfig(Integer logicId) {
        IndexTemplateConfig indexTemplateConfig = new IndexTemplateConfig();
        indexTemplateConfig.setLogicId(logicId);
        indexTemplateConfig.setAdjustRackTpsFactor(1.0);
        indexTemplateConfig.setAdjustRackShardFactor(1.0);
        indexTemplateConfig.setDynamicLimitEnable( AdminConstant.YES);
        indexTemplateConfig.setMappingImproveEnable(AdminConstant.NO);
        indexTemplateConfig.setIsSourceSeparated(AdminConstant.NO);
        indexTemplateConfig.setDisableSourceFlags(false);
        indexTemplateConfig.setPreCreateFlags(true);
        indexTemplateConfig.setShardNum(1);
        return indexTemplateConfig;
    }

    /**
     * 记录模板配置
     *
     * @param indexTemplateConfig 索引模板配置
     */
    private boolean insertTemplateConfig(IndexTemplateConfig indexTemplateConfig) {
        return templateLogicService.insertTemplateConfig(indexTemplateConfig).success();
    }
}