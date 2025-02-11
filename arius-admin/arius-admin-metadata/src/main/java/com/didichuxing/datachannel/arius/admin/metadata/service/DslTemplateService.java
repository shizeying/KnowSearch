package com.didichuxing.datachannel.arius.admin.metadata.service;

import com.didichuxing.datachannel.arius.admin.common.Tuple;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.dsl.DslQueryLimitDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.dto.dsl.template.DslTemplateConditionDTO;
import com.didichuxing.datachannel.arius.admin.common.bean.entity.dsl.DslQueryLimit;
import com.didichuxing.datachannel.arius.admin.common.bean.po.dsl.DslTemplatePO;
import com.didichuxing.datachannel.arius.admin.common.exception.ESOperateException;
import com.didichuxing.datachannel.arius.admin.common.util.ConvertUtil;
import com.didichuxing.datachannel.arius.admin.common.util.DateTimeUtil;
import com.didichuxing.datachannel.arius.admin.persistence.es.index.dao.dsl.DslTemplateESDAO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cjm
 */
@Service
public class DslTemplateService {

    @Autowired
    private DslTemplateESDAO dslTemplateESDAO;

    public Boolean updateDslTemplateQueryLimit(List<DslQueryLimitDTO> dslQueryLimitDTOList) {

        List<DslQueryLimit> dslQueryLimitList = new ArrayList<>();
        for (DslQueryLimitDTO dslQueryLimitDTO : dslQueryLimitDTOList) {
            // 排除无效的 dslTemplateMd5
            if (dslTemplateESDAO.getDslTemplateByKey(dslQueryLimitDTO.getProjectIdDslTemplateMd5()) != null) {
                dslQueryLimitList.add(ConvertUtil.obj2Obj(dslQueryLimitDTO, DslQueryLimit.class));
            }
        }
        return dslTemplateESDAO.updateQueryLimitByProjectIdDslTemplate(dslQueryLimitList);
    }

    public Boolean updateDslTemplateStatus(Integer projectId,String dslTemplateMd5) {
        DslTemplatePO dslTemplatePO = dslTemplateESDAO.getDslTemplateByKey(projectId, dslTemplateMd5);
        if (dslTemplatePO == null) {
            // 如果没有该 dslTemplateMd5
            return false;
        }
        // getEnable() 如果为 null，表示当前是启用状态，反转模版启用状态
        if (dslTemplatePO.getEnable() == null) {
            dslTemplatePO.setEnable(false);
        } else {
            dslTemplatePO.setEnable(!dslTemplatePO.getEnable());
        }
        String ariusModifyTime =DateTimeUtil.getCurrentFormatDateTime();
        dslTemplatePO.setAriusModifyTime(ariusModifyTime);
        List<DslTemplatePO> dslTemplatePOList = new ArrayList<>();
        dslTemplatePOList.add(dslTemplatePO);
        return dslTemplateESDAO.updateTemplates(dslTemplatePOList);
    }

    public DslTemplatePO getDslTemplateDetail(Integer projectId, String dslTemplateMd5) {
        return dslTemplateESDAO.getDslTemplateByKey(projectId, dslTemplateMd5);
    }

    public Tuple<Long, List<DslTemplatePO>> getDslTemplatePage(Integer projectId, DslTemplateConditionDTO queryDTO)
            throws ESOperateException {
        return dslTemplateESDAO.getDslTemplatePage(projectId, queryDTO);
    }
}