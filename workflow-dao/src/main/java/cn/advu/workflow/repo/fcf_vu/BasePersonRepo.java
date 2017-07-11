package cn.advu.workflow.repo.fcf_vu;


import cn.advu.workflow.domain.fcf_vu.BaseDept;
import cn.advu.workflow.domain.fcf_vu.BasePerson;
import cn.advu.workflow.domain.fcf_vu.BasePersonExtend;
import cn.advu.workflow.repo.base.IRepo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasePersonRepo extends IRepo<BasePerson> {

    List<BasePerson> findAll();

    List<BasePersonExtend> findDeptPerson(Integer areaId, Integer deptId);
}