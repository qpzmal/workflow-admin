package cn.advu.workflow.web.manager;

import cn.advu.workflow.domain.fcf_vu.BaseExecuteOrder;
import cn.advu.workflow.domain.fcf_vu.datareport.BaseExecuteOrderReportVO;
import cn.advu.workflow.repo.fcf_vu.BaseAdtypeRepo;
import cn.advu.workflow.repo.fcf_vu.BaseExecuteOrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangry on 17/8/19.
 */
@Component
public class ExecuteOrderManager {

    @Autowired
    private BaseExecuteOrderRepo baseExecuteOrderRepo;

    public List<BaseExecuteOrderReportVO> findFinalReport(String likeSearch) {
        return baseExecuteOrderRepo.finalReport(likeSearch);
    }
}
