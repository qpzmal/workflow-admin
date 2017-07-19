package cn.advu.workflow.repo.fcf_vu;


import cn.advu.workflow.domain.fcf_vu.BaseAdtype;
import cn.advu.workflow.repo.base.IRepo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseAdtypeRepo extends IRepo<BaseAdtype> {

    List<BaseAdtype> findAll();

    List<BaseAdtype> findAllActive();
}
