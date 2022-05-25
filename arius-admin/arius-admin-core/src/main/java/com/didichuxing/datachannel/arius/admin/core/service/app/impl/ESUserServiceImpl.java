package com.didichuxing.datachannel.arius.admin.core.service.app.impl;

import static com.didichuxing.datachannel.arius.admin.common.constant.AdminConstant.yesOrNo;
import static com.didichuxing.datachannel.arius.admin.common.constant.operaterecord.OperationEnum.ADD;
import static com.didichuxing.datachannel.arius.admin.common.constant.operaterecord.OperationEnum.EDIT;
import static com.didichuxing.datachannel.arius.admin.common.util.ConvertUtil.obj2Obj;

import com.didichuxing.datachannel.arius.admin.common.Tuple;
import com.didichuxing.datachannel.arius.admin.common.bean.common.Result;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.app.ESUserConfigDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.app.ESUserDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.app.ESUser;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.app.ESUserConfig;
import com.didichuxing.datachannel.arius.admin.common.bean.po.app.ESUserConfigPO;
import com.didichuxing.datachannel.arius.admin.common.bean.po.app.ESUserPO;
import com.didichuxing.datachannel.arius.admin.common.constant.AdminConstant;
import com.didichuxing.datachannel.arius.admin.common.constant.app.AppSearchTypeEnum;
import com.didichuxing.datachannel.arius.admin.common.constant.operaterecord.OperationEnum;
import com.didichuxing.datachannel.arius.admin.common.util.AriusObjUtils;
import com.didichuxing.datachannel.arius.admin.common.util.ConvertUtil;
import com.didichuxing.datachannel.arius.admin.common.util.EnvUtil;
import com.didichuxing.datachannel.arius.admin.common.util.VerifyCodeFactory;
import com.didichuxing.datachannel.arius.admin.core.service.app.ESUserService;
import com.didichuxing.datachannel.arius.admin.persistence.mysql.app.ESUserConfigDAO;
import com.didichuxing.datachannel.arius.admin.persistence.mysql.app.ESUserDAO;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linyunan
 * @date 2021-04-28
 */
@Service
public class ESUserServiceImpl implements ESUserService {

    private static final ILog          LOGGER                      = LogFactory.getLog(ESUserServiceImpl.class);

    private static final Integer       VERIFY_CODE_LENGTH          = 15;

    private static final Integer       APP_QUERY_THRESHOLD_DEFAULT = 100;

    private static final String APP_NOT_EXIST = "es user 不存在";

    @Autowired
    private ESUserDAO esUserDAO;
    @Autowired
    private ESUserConfigDAO esUserConfigDAO;

   

    private final Cache<String, List<?>>   appListCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(100).build();


    /**
     * 查询app详细信息
     *
     * @return 返回app列表
     */
    @Override
    public List<ESUser> listESUsers(List<Integer> projectIds) {
        return         ConvertUtil.list2List(esUserDAO.listByESUsers(projectIds), ESUser.class);
    }

    @Override
    public List<ESUser> listESUserWithCache(List<Integer> projectIds) {
        try {
            return (List<ESUser>) appListCache.get("listESUsers", () -> listESUsers(projectIds));
        } catch (ExecutionException e) {
            return listESUsers(projectIds);
        }
    }



    /**
     * 新建APP
     *
     * @param esUserDTO dto
     * @param operator  操作人 邮箱前缀
     * @return 成功 true  失败 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tuple</*创建的es user*/Result<Integer>,/*创建的es user po*/ ESUserPO> registerESUser(ESUserDTO esUserDTO,
                                                                                  String operator) {
       
        Result<Void> checkResult = validateESUser(esUserDTO, ADD);
        
        if (checkResult.failed()) {
            LOGGER.warn("class=ESUserManagerImpl||method=addApp||fail msg={}", checkResult.getMessage());
            return new Tuple<>(Result.buildFrom(checkResult),null);
        }
        initParam(esUserDTO);
        ESUserPO param = obj2Obj(esUserDTO, ESUserPO.class);
        final int countByProjectId = esUserDAO.countByProjectId(esUserDTO.getProjectId());
        //如果项目中已经存在es user，那么setDefaultDisplay为false
        if (countByProjectId == 0) {
            //新创建的项目会默认创建一个es user ，作为当前项目的默认es user
            param.setDefaultDisplay(true);
        } else {
            param.setDefaultDisplay(false);
        }
        
        boolean succ = (esUserDAO.insert(param) == 1);
        if (succ) {
            // 默认配置
            if (initConfig(param.getId()).failed()) {
                LOGGER.warn("class=ESUserServiceImpl||method=registerESUser||esUser={}||projectId={}||msg=initConfig "
                            + "fail",
                        param.getId(),esUserDTO.getProjectId());
            }
        
        }

        return new Tuple<>(Result.build(succ, param.getId()),param);
    }



    /**
     * 编辑APP
     *
     * @param esUserDTO dto
     * @return 成功 true  失败 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tuple<Result<Void>/*更新的状态*/, ESUserPO/*更新之后的的ESUserPO*/> editUser(ESUserDTO esUserDTO) {
        
        
        final ESUserPO param = obj2Obj(esUserDTO, ESUserPO.class);
    
        return new Tuple<>(Result.build((esUserDAO.update(param) == 1)), param);
    
    }

    /**
     * 删除APP
     *
     * @param esUser APPID
     * @return 成功 true  失败 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tuple<Result<Void>, ESUserPO> deleteESUserById(int esUser) {
        
        ESUserPO oldPO = esUserDAO.getByESUser(esUser);
        boolean succ = esUserDAO.delete(esUser) == 1;
      

        return new Tuple<>(Result.build(succ),oldPO);
    }
     /**
      * @param projectId
      * @return
      */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Tuple<Result<Void>, List<ESUserPO>> deleteByESUsers(int projectId) {
        final List<ESUserPO> esUserPOS = esUserDAO.listByProjectId(projectId);
        final int deleteByProjectId = esUserDAO.deleteByProjectId(projectId);
        return new Tuple<>(Result.build(deleteByProjectId==esUserPOS.size()), esUserPOS);
    }
    
    /**
     * 获取项目下es user 个数
     *
     * @param projectId 项目id
     * @return int
     */
    @Override
    public int countByProjectId(int projectId) {
        return esUserDAO.countByProjectId(projectId);
    }
    
    /**
     * 初始化APP配置
     *
     * @param esUserName APPID
     * @return 成功 true  失败false
     */
    @Override
    public Result<Void> initConfig(Integer esUserName) {
        ESUserConfigPO param = new ESUserConfigPO();
        param.setEsUser(esUserName);
        param.setDslAnalyzeEnable(AdminConstant.YES);
        param.setIsSourceSeparated(AdminConstant.NO);
        param.setAggrAnalyzeEnable(AdminConstant.YES);
        param.setAnalyzeResponseEnable(AdminConstant.YES);

        return Result.build(esUserConfigDAO.update(param) == 1);
    }

    /**
     * 获取appid配置信息
     *
     * @param esUserName APPID
     * @return 配置信息
     */
    @Override
    public ESUserConfig getESUserConfig(int esUserName) {
        final ESUserPO oldEsUser = esUserDAO.getByESUser(esUserName);
        if (oldEsUser == null) {
            LOGGER.warn("class=ESUserServiceImpl||method=getESUserConfig||esUserName={}||msg=es username not exist!",
                    esUserName);
            return null;
        }
    
        ESUserConfigPO oldConfigPO = esUserConfigDAO.getByESUser(esUserName);
        if (oldConfigPO == null) {
            initConfig(esUserName);
            oldConfigPO = esUserConfigDAO.getByESUser(esUserName);
        
        }

        return obj2Obj(oldConfigPO, ESUserConfig.class);
    }

    /**
     * 获取所有应用的配置
     *
     * @return list
     */
    @Override
    public List<ESUserConfig> listConfig() {
        return ConvertUtil.list2List(esUserConfigDAO.listAll(), ESUserConfig.class);
    }

    @Override
    public List<ESUserConfig> listConfigWithCache() {
        try {
            return (List<ESUserConfig>) appListCache.get("listConfig", this::listConfig);
        } catch (ExecutionException e) {
            return listConfig();
        }
    }

    /**
     * 修改APP配置
     *
     * @param configDTO 配置信息
     * @param operator  操作人
     * @return 成功 true  失败  false
     * <p>
     * NotExistException APP不存在 IllegalArgumentException 参数不合理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tuple<Result<Void>, ESUserConfigPO> updateESUserConfig(ESUserConfigDTO configDTO, String operator) {
        Result<Void> checkResult = checkConfigParam(configDTO);
        if (checkResult.failed()) {
            LOGGER.warn("class=ESUserServiceImpl||method=updateESUserConfig||msg={}||msg=check fail!", checkResult.getMessage());
            return new Tuple<>(checkResult, null);
        }

         ESUserPO oldESUser = esUserDAO.getByESUser(configDTO.getEsUser());
        if (oldESUser == null) {
            return new Tuple<>(Result.buildNotExist(APP_NOT_EXIST), null);
        }

        ESUserConfigPO oldConfigPO = esUserConfigDAO.getByESUser(configDTO.getEsUser());

        boolean succ = (1 == esUserConfigDAO.update(obj2Obj(configDTO, ESUserConfigPO.class)));
        

        return new Tuple<>(Result.build(succ),oldConfigPO);
    }

    /**
     * 校验appid是否存在
     * @param esUserName 应用id
     * @return true/false
     */
    @Override
    public boolean isESUserExists(Integer esUserName) {
        return esUserDAO.getByESUser(esUserName) != null;
    }

    @Override
    public boolean isESUserExists(ESUser esUser) {
        return esUser != null;
    }

    @Override
    public boolean isSuperESUser(Integer esUserName) {
        ESUser appById = getEsUserById(esUserName);
        if (AriusObjUtils.isNull(appById)) {
            return false;
        }

        return appById.getIsRoot() == 1;
    }


    /**
     * 指定id查询
     *
     * @param esUser appID
     * @return app  如果不存在返回null
     */
    @Override
    public ESUser getEsUserById(Integer esUser) {
        return obj2Obj(esUserDAO.getByESUser(esUser), ESUser.class);
    }

  


    /**
     * 校验验证码
     *
     * @param esUserName     app
     * @param verifyCode 验证码
     * @return result
     */
    @Override
    public Result<Void> verifyAppCode(Integer esUserName, String verifyCode) {
        final ESUserPO esUser = esUserDAO.getByESUser(esUserName);
    
        if (esUser == null) {
            return Result.buildNotExist(APP_NOT_EXIST);
        }

        if (StringUtils.isBlank(verifyCode) || !esUser.getVerifyCode().equals(verifyCode)) {
            return Result.buildParamIllegal("校验码错误");
        }

        return Result.buildSucc();
    }



    
    
    /**
     * 验证APP参数是否合法
     *
     * @param appDTO    dto
     * @param operation 是否校验null参数;  新建的时候需要校验,编辑的时候不需要校验
     * @return 参数合法返回
     */
    @Override
    public Result<Void> validateESUser(ESUserDTO appDTO, OperationEnum operation) {
        if (AriusObjUtils.isNull(appDTO)) {
            return Result.buildParamIllegal("应用信息为空");
        }
        Result<Void> validateAppFieldIsNullResult = validateAppFieldIsNull(appDTO);
        if (validateAppFieldIsNullResult.failed()) {
            return validateAppFieldIsNullResult;
        }
        //todo 暂定校验超级用户 user name
        if (!appDTO.getResponsible().equals(AdminConstant.SUPER_USER_NAME)) {
            return Result.buildParamIllegal(String.format("当前操作[%s] 不能创建es user", appDTO.getResponsible()));
        }
        ESUserPO oldESUser = esUserDAO.getByESUser(appDTO.getId());
        if (Objects.nonNull(oldESUser)){
            if (Objects.isNull(appDTO.getProjectId())) {
                return Result.buildFail("项目id为空");
            }
        }
        if (ADD.equals(operation)) {
            if (Objects.nonNull(oldESUser)) {
                return Result.buildParamIllegal(String.format("es user [%s] is exists", appDTO.getId()));
            }
        } else if (EDIT.equals(operation)) {
            if (AriusObjUtils.isNull(appDTO.getId())) {
                return Result.buildParamIllegal(String.format("es user [%s] not exists", appDTO.getId()));
            }
            if (AriusObjUtils.isNull(oldESUser)) {
                return Result.buildNotExist(APP_NOT_EXIST);
            }
            //判断当前es user 在同一个项目中
            if (Objects.nonNull(oldESUser) && !Objects.equals(oldESUser.getProjectId(), appDTO.getProjectId())) {
                return Result.buildFail(
                        String.format("es user 已经存在在项目[%s],不能为项目[%s]创建,请重新提交es user", oldESUser.getProjectId(),
                                appDTO.getProjectId()));
            }
        }
    
        if (appDTO.getIsRoot() == null || !AdminConstant.yesOrNo(appDTO.getIsRoot())) {
            return Result.buildParamIllegal("超管标记非法");
        }
    
        AppSearchTypeEnum searchTypeEnum = AppSearchTypeEnum.valueOf(appDTO.getSearchType());
        if (searchTypeEnum.equals(AppSearchTypeEnum.UNKNOWN)) {
            return Result.buildParamIllegal("查询模式非法");
        }
        if (StringUtils.isBlank(appDTO.getVerifyCode())) {
            return Result.buildParamIllegal("校验码不能为空");
        }
        return Result.buildSucc();
    }
    
    /**
     * 查询项目下可以免密登录的es user
     *
     * @param projectId projectId
     * @return appList
     */
    @Override
    public List<ESUser> getProjectWithoutCodeApps(Integer projectId) {
    
        return listESUsers(Collections.singletonList(projectId));
    
    }
    
    /**************************************** private method ****************************************************/
    

    private void initParam(ESUserDTO esUser) {
        // 默认不是root用户
        if (esUser.getIsRoot() == null) {
            esUser.setIsRoot(AdminConstant.NO);
        }

        if (StringUtils.isBlank(esUser.getDataCenter())) {
            esUser.setDataCenter(EnvUtil.getDC().getCode());
        }

        // 默认cluster=""
        if (esUser.getCluster() == null) {
            esUser.setCluster("");
        }
        
        // 默认索引模式
        if (esUser.getSearchType() == null) {
            esUser.setSearchType(AppSearchTypeEnum.TEMPLATE.getCode());
        }

        // 生成默认的校验码
        if (StringUtils.isBlank(esUser.getVerifyCode())) {
            esUser.setVerifyCode(VerifyCodeFactory.get(VERIFY_CODE_LENGTH));
        }

        // 设置默认查询限流值
        if (esUser.getQueryThreshold() == null) {
            esUser.setQueryThreshold(APP_QUERY_THRESHOLD_DEFAULT);
        }
    }





    private Result<Void> checkConfigParam(ESUserConfigDTO configDTO) {
        if (configDTO == null) {
            return Result.buildParamIllegal("配置信息为空");
        }
        if (configDTO.getEsUser() == null) {
            return Result.buildParamIllegal("应用ID为空");
        }
        if (configDTO.getAnalyzeResponseEnable() != null && !yesOrNo(configDTO.getAnalyzeResponseEnable())) {
            return Result.buildParamIllegal("解析响应结果开关非法");
        }
        if (configDTO.getDslAnalyzeEnable() != null && !yesOrNo(configDTO.getDslAnalyzeEnable())) {
            return Result.buildParamIllegal("DSL分析开关非法");
        }
        if (configDTO.getAggrAnalyzeEnable() != null && !yesOrNo(configDTO.getAggrAnalyzeEnable())) {
            return Result.buildParamIllegal("聚合分析开关非法");
        }
        if (configDTO.getIsSourceSeparated() != null && !yesOrNo(configDTO.getIsSourceSeparated())) {
            return Result.buildParamIllegal("索引存储分离开关非法");
        }

        return Result.buildSucc();
    }

  
    private Result<Void> validateAppFieldIsNull(ESUserDTO appDTO) {
        if (appDTO.getMemo() == null) {
            return Result.buildParamIllegal("备注为空");
        }
        return Result.buildSucc();
    }
}