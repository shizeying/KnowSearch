package com.didi.arius.gateway.util;

import com.didi.arius.gateway.common.consts.QueryConsts;
import com.didi.arius.gateway.common.metadata.*;
import com.didi.arius.gateway.elasticsearch.client.ESClient;
import com.didi.arius.gateway.remote.response.*;

import java.util.*;

/**
 * @author wuxuan
 * @Date 2022/6/6
 */
public class CustomDataSource {
    public static final String PHY_CLUSTER_NAME = "gateway_test_1";
    public static final String CLUSTER_NAME = "dc-es02";
    public static final String ip = "127.0.0.0" ;
    public static final String INDEX_NAME = "cn_record.arius.template.value";
    public static final String INDEX_NAME2 = "cn_record.arius.template.value_2021-05";
    public static final int appid = 1;

    public static AppDetail appDetailFactory() {
        AppDetail appDetail = new AppDetail();
        appDetail.setId(appid);
        appDetail.setCluster(PHY_CLUSTER_NAME);
        List<String> ips = new ArrayList<String>();
        ips.add(ip);
        appDetail.setIp(ips);
        return appDetail;
    }

    public static DSLTemplateResponse dslTemplateResponseFactory(){
        final DSLTemplateResponse dslTemplateResponse = new DSLTemplateResponse();
        dslTemplateResponse.setKey("key");
        dslTemplateResponse.setAriusCreateTime("ariusCreateTime");
        dslTemplateResponse.setAriusModifyTime("ariusModifyTime");
        dslTemplateResponse.setResponseLenAvg(1.0);
        dslTemplateResponse.setRequestType("requestType");
        dslTemplateResponse.setSearchType("searchType");
        dslTemplateResponse.setEsCostAvg(1.0);
        dslTemplateResponse.setTotalHitsAvg(1.0);
        dslTemplateResponse.setTotalShardsAvg(1.0);
        dslTemplateResponse.setQueryLimit(1.0);
        dslTemplateResponse.setEnable(false);
        dslTemplateResponse.setCheckMode("checkMode");
        return dslTemplateResponse;
    }

    public static DynamicConfigResponse dynamicConfigResponseFactory(){
        DynamicConfigResponse dynamicConfigResponse = new DynamicConfigResponse();
        dynamicConfigResponse.setCreateTime(0L);
        dynamicConfigResponse.setDimension(0L);
        dynamicConfigResponse.setEdit(0L);
        dynamicConfigResponse.setId(0L);
        dynamicConfigResponse.setMemo("memo");
        dynamicConfigResponse.setStatus(0L);
        dynamicConfigResponse.setUpdateTime(0L);
        dynamicConfigResponse.setValue("{\n" +
                "    \"appids\":[\n" +
                "        1,\n" +
                "        2,\n" +
                "        3,\n" +
                "        4\n" +
                "    ],\n" +
                "    \"didi-123deMacBook-Pro.local\":true\n" +
                "}");
        dynamicConfigResponse.setValueGroup("valueGroup");
        dynamicConfigResponse.setValueName(QueryConsts.DETAIL_LOG_FLAG);
        return dynamicConfigResponse;
    }

    public static DynamicConfigListResponse dynamicConfigListResponseFactory() {
        DynamicConfigListResponse dynamicConfigListResponse = new DynamicConfigListResponse();
        dynamicConfigListResponse.setCode(0);
        List<DynamicConfigResponse> data = new ArrayList<>();
        DynamicConfigResponse dynamicConfigResponse = CustomDataSource.dynamicConfigResponseFactory();
        data.add(dynamicConfigResponse);
        DynamicConfigResponse dynamicConfigResponseOne = CustomDataSource.dynamicConfigResponseFactory();
        dynamicConfigResponseOne.setValueName(null);
        data.add(dynamicConfigResponseOne);
        DynamicConfigResponse dynamicConfigResponseTwo = CustomDataSource.dynamicConfigResponseFactory();
        dynamicConfigResponseTwo.setValueName(QueryConsts.MAPPING_INDEXNAME_WHITE_APPIDS);
        data.add(dynamicConfigResponseTwo);
        DynamicConfigResponse dynamicConfigResponseThree = CustomDataSource.dynamicConfigResponseFactory();
        dynamicConfigResponseThree.setValueName(QueryConsts.FORBIDDEN_SETTING_PATH);
        data.add(dynamicConfigResponseThree);
        DynamicConfigResponse dynamicConfigResponseFour = CustomDataSource.dynamicConfigResponseFactory();
        dynamicConfigResponseFour.setValue("value");
        data.add(dynamicConfigResponseFour);
        DynamicConfigResponse dynamicConfigResponseFive = CustomDataSource.dynamicConfigResponseFactory();
        dynamicConfigResponseFive.setValue("{\n" +
                "    \"appids\":[\n" +
                "\n" +
                "    ]\n" +
                "}");
        data.add(dynamicConfigResponseFive);
        dynamicConfigListResponse.setData(data);
        return dynamicConfigListResponse;
    }

    public static QueryContext queryContextFactory(){
        QueryContext queryContext = new QueryContext();
        queryContext.setClusterId("clusterId");
        queryContext.setAppid(0);
        queryContext.setClusterName(CLUSTER_NAME);
        queryContext.setRequest(null);
        queryContext.setChannel(null);
        queryContext.setResponse(null);
        queryContext.setIndices(Arrays.asList("value"));
        queryContext.setXUserName("xUserName");
        queryContext.setRestName("restName");
        queryContext.setClient(new ESClient(CLUSTER_NAME, "version"));
        queryContext.setClientVersion("clientVersion");
        queryContext.setFromKibana(false);
        queryContext.setNewKibana(false);
        queryContext.setPreQueryEsTime(0L);
        return queryContext;
    }

    public static IndexTemplate indexTemplateFactory(){
        IndexTemplate indexTemplate = new IndexTemplate();
        indexTemplate.setId(0);
        indexTemplate.setName(INDEX_NAME);
        indexTemplate.setDateField("dateField");
        indexTemplate.setDateFormat("dateFormat");
        indexTemplate.setExpireTime(0L);
        indexTemplate.setExpression("expression");
        indexTemplate.setIsDefaultRouting(false);
        indexTemplate.setVersion(0);
        indexTemplate.setDeployStatus(IndexTemplate.DeployStatus.MASTER_AND_SLAVE);
        TemplateClusterInfo masterInfo = new TemplateClusterInfo();
        masterInfo.setAccessApps(new HashSet<>(Arrays.asList(0)));
        masterInfo.setCluster(CLUSTER_NAME);
        indexTemplate.setMasterInfo(masterInfo);
        TemplateClusterInfo templateClusterInfo = new TemplateClusterInfo();
        templateClusterInfo.setAccessApps(new HashSet<>(Arrays.asList(0)));
        templateClusterInfo.setCluster(CLUSTER_NAME);
        indexTemplate.setSlaveInfos(Arrays.asList(templateClusterInfo));
        return indexTemplate;
    }

    public static DataCenterResponse dataCenterResponseFactory(){
        DataCenterResponse dataCenterResponse = new DataCenterResponse();
        dataCenterResponse.setId(0L);
        dataCenterResponse.setCluster(CLUSTER_NAME);
        dataCenterResponse.setReadAddress("readAddress");
        dataCenterResponse.setHttpAddress("httpAddress");
        dataCenterResponse.setWriteAddress("writeAddress");
        dataCenterResponse.setHttpWriteAddress("httpWriteAddress");
        dataCenterResponse.setDesc("desc");
        dataCenterResponse.setType(0);
        dataCenterResponse.setEsVersion("esVersion");
        dataCenterResponse.setPassword("password");
        dataCenterResponse.setRunMode(1);
        dataCenterResponse.setWriteAction("writeAction");
        return dataCenterResponse;
    }

    public static DataCenterListResponse dataCenterListResponseFactory(){
        DataCenterListResponse dataCenterListResponse = new DataCenterListResponse();
        List<DataCenterResponse> dataCenterResponses = new ArrayList<>();
        DataCenterResponse dataCenterResponse = dataCenterResponseFactory();
        dataCenterResponses.add(dataCenterResponse);
        DataCenterResponse dataCenterResponseOne = dataCenterResponseFactory();
        dataCenterResponseOne.setHttpAddress(null);
        dataCenterResponses.add(dataCenterResponseOne);
        DataCenterResponse dataCenterResponseTwo = dataCenterResponseFactory();
        dataCenterResponseTwo.setEsVersion(null);
        dataCenterResponses.add(dataCenterResponseTwo);
        DataCenterResponse dataCenterResponseThree = dataCenterResponseFactory();
        dataCenterResponseThree.setRunMode(0);
        dataCenterResponses.add(dataCenterResponseThree);
        dataCenterListResponse.setData(dataCenterResponses);
        return dataCenterListResponse;
    }

    public static BaseInfoResponse baseInfoResponseFactory() {
        BaseInfoResponse baseInfoResponse = new BaseInfoResponse();
        baseInfoResponse.setExpression("expression");
        baseInfoResponse.setId(1);
        baseInfoResponse.setBlockRead(true);
        baseInfoResponse.setDateField("datafiled");
        baseInfoResponse.setBlockWrite(true);
        baseInfoResponse.setDateFormat("dataformat");
        baseInfoResponse.setVersion(1);
        return baseInfoResponse;
    }

    public static MasterInfoResponse masterInfoResponseFactory(){
        MasterInfoResponse masterInfoResponse = new MasterInfoResponse();
        masterInfoResponse.setCluster(CLUSTER_NAME);
        return masterInfoResponse;
    }

    public static List<SlaveInfoResponse> slaveInfosFactory() {
        List<SlaveInfoResponse> slaveInfos = new ArrayList<>();
        SlaveInfoResponse slaveInfoResponse = new SlaveInfoResponse();
        slaveInfoResponse.setCluster(CLUSTER_NAME);
        return slaveInfos;
    }

    public static IndexTemplateResponse indexTemplateResponseFactory(){
        IndexTemplateResponse indexTemplateResponse = new IndexTemplateResponse();
        indexTemplateResponse.setBaseInfo(baseInfoResponseFactory());
        indexTemplateResponse.setMasterInfo(masterInfoResponseFactory());
        indexTemplateResponse.setSlaveInfos(slaveInfosFactory());
        return indexTemplateResponse;
    }

    public static IndexTemplateListResponse indexTemplateListResponseFactory(){
        IndexTemplateListResponse indexTemplateListResponse = new IndexTemplateListResponse();
        indexTemplateListResponse.setMessage("message");
        Map<String, IndexTemplateResponse> data = new HashMap<>();
        IndexTemplateResponse indexTemplateResponse = indexTemplateResponseFactory();
        data.put("key",indexTemplateResponseFactory());
        indexTemplateListResponse.setData(data);
        return indexTemplateListResponse;
    }

    public static TemplateInfoResponse templateInfoResponseFactory(){
        TemplateInfoResponse templateInfoResponse = new TemplateInfoResponse();
        templateInfoResponse.setExpression("expression");
        templateInfoResponse.setId(1);
        templateInfoResponse.setVersion(1);
        templateInfoResponse.setAliases(aliasesFactory());
        return templateInfoResponse;
    }

    public static TemplateInfoListResponse templateInfoListResponseFactory(){
        TemplateInfoListResponse templateInfoListResponse = new TemplateInfoListResponse();
        templateInfoListResponse.setMessage("message");
        Map<String, TemplateInfoResponse> data = new HashMap<>();
        data.put("key",templateInfoResponseFactory());
        templateInfoListResponse.setData(data);
        return templateInfoListResponse;
    }

    public static List<AliasesInfoResponse> aliasesFactory() {
        List<AliasesInfoResponse> aliases = new ArrayList<>();
        AliasesInfoResponse aliasesInfoResponse = new AliasesInfoResponse();
        aliasesInfoResponse.setName(CLUSTER_NAME);
        aliases.add(aliasesInfoResponse);
        return aliases;
    }

    public static ESCluster esClusterFactory() {
        ESCluster esCluster = new ESCluster();
        esCluster.setCluster(CustomDataSource.CLUSTER_NAME);
        esCluster.setType(ESCluster.Type.INDEX);
        return esCluster;
    }

    public static DSLTemplateListResponse dslTemplateListResponseFactory() {
        DSLTemplateListResponse dslTemplateListResponse = new DSLTemplateListResponse();
        DSLTemplateWrapResponse data = new DSLTemplateWrapResponse();
        DSLTemplateResponse dslTemplateResponse = CustomDataSource.dslTemplateResponseFactory();
        DSLTemplateResponse dslTemplateResponseOne = CustomDataSource.dslTemplateResponseFactory();
        dslTemplateResponseOne.setKey("key——key");
        List<DSLTemplateResponse> dslTemplatePoList = new ArrayList<>();
        dslTemplatePoList.add(dslTemplateResponse);
        dslTemplatePoList.add(dslTemplateResponseOne);
        data.setDslTemplatePoList(dslTemplatePoList);
        data.setScrollId("scrollId");
        dslTemplateListResponse.setData(data);
        return dslTemplateListResponse;
    }
}