package cn.advu.workflow.web.service.pc;

import cn.advu.workflow.common.golbal.Page;

/**
 * Created by BBD on 2017/5/16.
 */
public interface PcConsumerDataService {
    Page getPcConsumerDatas(Page page, String startTime, String endTime, String[] cnids);
}
