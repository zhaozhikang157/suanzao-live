package com.longlian.live.newdao;
import com.huaxin.util.DataGridPage;
import com.huaxin.util.page.DatagridRequestModel;
import com.longlian.model.DataChargeLevel;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import tk.mybatis.mapper.common.Mapper;
@org.apache.ibatis.annotations.Mapper
    public interface DataChargeLevelMapper extends Mapper<DataChargeLevel> {
        List<Map> getDataChargeLevelListPage(@Param(value = "page") DatagridRequestModel page,@Param(value="map") Map map);
        List<DataChargeLevel> getDataChargeLevelList();
        void deleteByIds(@Param(value = "item")String ids);
    }