package cn.advu.workflow.web.manager;

import cn.advu.workflow.domain.base.IEntity;
import cn.advu.workflow.domain.fcf_vu.SysLog;
import cn.advu.workflow.domain.fcf_vu.SysLogWithBLOBs;
import cn.advu.workflow.repo.fcf_vu.SysLogRepo;
import cn.advu.workflow.web.common.loginContext.UserThreadLocalContext;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangry on 17/7/8.
 */
@Component
public class BizLogManager {

    @Autowired
    SysLogRepo sysLogRepo;

    public List<SysLogWithBLOBs> findAllLog(String startDate, String endDate, SysLog sysLog) {
        return sysLogRepo.findAllLog(startDate, endDate, sysLog);
    }


    public Integer count(String startDate, String endDate, SysLog sysLog) {
        return sysLogRepo.count(startDate, endDate, sysLog);
    }

    public void addBizLog(String content) {
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setContent(content);
        sysLogRepo.addSelective(sysLogWithBLOBs);
    }

    public void addBizLog(String content, Integer type) {
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setContent(content);
        sysLogWithBLOBs.setType(type);
        sysLogRepo.addSelective(sysLogWithBLOBs);
    }

    public void addBizLog(IEntity entity, String operation, Integer type) {
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setContent(JSONObject.toJSONString(entity));
        sysLogWithBLOBs.setOperation(operation);
        sysLogWithBLOBs.setOperator(UserThreadLocalContext.getCurrentUser().getRealName());
        sysLogWithBLOBs.setType(type);
        sysLogRepo.addSelective(sysLogWithBLOBs);
    }

    public void addBizLog(IEntity entity, Integer type) {
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setContent(JSONObject.toJSONString(entity));
        sysLogWithBLOBs.setOperation("");
        sysLogWithBLOBs.setOperator(UserThreadLocalContext.getCurrentUser().getUserId());
        sysLogWithBLOBs.setType(type);
        sysLogRepo.addSelective(sysLogWithBLOBs);
    }

    public void addBizLog(List<IEntity> entitys, Integer type) {

        if (entitys == null || entitys.isEmpty()) {
            return;
        }

        for (IEntity entity : entitys) {
            SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
            sysLogWithBLOBs.setContent(JSONObject.toJSONString(entity));
            sysLogWithBLOBs.setType(type);
            sysLogRepo.addSelective(sysLogWithBLOBs);
        }
    }

    public void addDeleteTypeBizLog(List<? extends  IEntity> entitys) {

        if (entitys == null || entitys.isEmpty()) {
            return;
        }

        for (IEntity entity : entitys) {
            SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
            sysLogWithBLOBs.setContent(JSONObject.toJSONString(entity));
            sysLogWithBLOBs.setType(3);
            sysLogRepo.addSelective(sysLogWithBLOBs);
        }
    }

    public <T extends IEntity > void addSaveTypeBizLog(T entity) {

        if (entity == null) {
            return;
        }
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setContent(JSONObject.toJSONString(entity));
        sysLogWithBLOBs.setType(1);
        sysLogRepo.addSelective(sysLogWithBLOBs);
    }
}
