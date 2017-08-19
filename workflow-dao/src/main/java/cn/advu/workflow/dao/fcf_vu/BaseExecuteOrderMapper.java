package cn.advu.workflow.dao.fcf_vu;

import cn.advu.workflow.dao.base.BaseDAO;
import cn.advu.workflow.domain.fcf_vu.BaseExecuteOrder;
import cn.advu.workflow.domain.fcf_vu.datareport.BaseExecuteOrderReportVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseExecuteOrderMapper extends BaseDAO<BaseExecuteOrder> {

    // 以下为自定义SQL
    List<BaseExecuteOrder> queryAll(BaseExecuteOrder baseExecuteOrder);
    List<BaseExecuteOrder> queryAllForContract(BaseExecuteOrder baseExecuteOrder);
    List<BaseExecuteOrder> findAllUnFinished();
    List<BaseExecuteOrderReportVO> queryFinalReport(@Param("likeSearch") String likeSearch);
}