package cn.advu.workflow.repo.fcf_vu;


import cn.advu.workflow.domain.fcf_vu.BaseIndustry;
import cn.advu.workflow.domain.fcf_vu.BaseMedia;
import cn.advu.workflow.repo.base.IRepo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseIndustryRepo extends IRepo<BaseIndustry> {

    List<BaseIndustry> findAll();

    List<BaseIndustry> findEnabledAll();

    BaseIndustry findByIdAndName(Integer id, String name);
}
