package cn.advu.workflow.dao.fcf_vu;

import cn.advu.workflow.dao.base.BaseDAO;
import cn.advu.workflow.domain.fcf_vu.BasePerson;
import cn.advu.workflow.domain.fcf_vu.BasePersonExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasePersonMapper extends BaseDAO<BasePerson> {
    // 以下为自定义SQL
    List<BasePerson> queryAll(@Param("status") Integer status);
    // 以下为自定义SQL
    List<BasePersonExtend> queryByDept(@Param("areaId") Integer areaId, @Param("deptId") Integer deptId);

    List<BasePerson> queryChildListByDept(@Param("deptId") Integer deptId);

    List<BasePerson> queryPersonListByArea(@Param("areaId") Integer areaId);

    BasePerson queryPersonByName(@Param("name")String name);

    BasePerson queryByIdAndName(@Param("id")Integer id, @Param("name")String name);

    BasePerson queryByUid(@Param("uid")Integer uid);

    List<BasePerson> queryByParentId(@Param("parentId")Integer parentId);
}