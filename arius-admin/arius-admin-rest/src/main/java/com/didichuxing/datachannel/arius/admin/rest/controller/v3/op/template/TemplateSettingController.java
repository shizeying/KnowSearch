package com.didichuxing.datachannel.arius.admin.rest.controller.v3.op.template;

import static com.didichuxing.datachannel.arius.admin.common.constant.ApiVersion.V3;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.admin.biz.template.srv.setting.TemplateLogicSettingsManager;
import com.didichuxing.datachannel.arius.admin.common.bean.common.Result;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.template.IndexTemplatePhySettings;
import com.didichuxing.datachannel.arius.admin.common.exception.AdminOperateException;
import com.didichuxing.datachannel.arius.admin.rest.controller.v2.console.template.BaseConsoleTemplateController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(V3 + "/template/setting")
@Api(tags = "Console-用户侧索引模板setting接口(REST)")
public class TemplateSettingController extends BaseConsoleTemplateController {

    @Autowired
    private TemplateLogicSettingsManager templateLogicSettingsManager;
    @PutMapping("/{logicId}")
    @ResponseBody
    @ApiOperation(value = "更新索引Setting接口" )
    public Result<Void> modifySetting(@PathVariable("logicId") Integer logicId,
                                      @RequestBody String settingDTO) {
        IndexTemplatePhySettings settings = new IndexTemplatePhySettings(JSONObject.parseObject(settingDTO));
        return templateLogicSettingsManager.updateSettings(logicId, settings);
    }
    
    @GetMapping("")
    @ResponseBody
    @ApiOperation(value = "获取索引Setting接口" )
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "logicId", value = "索引ID", required = true) })
    public Result<IndexTemplatePhySettings> getTemplateSettings(@RequestParam("logicId") Integer logicId) throws
                                                                                                          AdminOperateException {
        return templateLogicSettingsManager.getSettings(logicId);
    }
    
   
}