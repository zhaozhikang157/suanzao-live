package com.longlian.live.newdao;

import com.huaxin.util.DataGridPage;
import com.longlian.model.LiveConnect;
import com.longlian.model.LiveConnectRequest;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@org.apache.ibatis.annotations.Mapper
public interface LiveConnectMapper extends Mapper<LiveConnect> {
    LiveConnect getLiveConnectByRequest(@Param("req") LiveConnectRequest request);


    /**
     * WHA -  The student asked the teacher to connect to the network voice��Request list
     * @param page :Paging parameters
     * @param teacher : Teacher ID
     * @param student : Student ID
     * @param courseId :Course ID
     * @return
     */
    public List<Map> getEvenForWheatListPage(@Param("page")DataGridPage page,
                                             @Param("teacher")Long teacher,
                                             @Param("student")Long student,
                                             @Param("courseId")Long courseId);

    /**
     * WHA -  The student asked the teacher to connect to the network voice��Request list
     * @param student : Student ID
     * @param courseId :Course ID
     * @return
     */
    public Set<Long> getEvenForWheatList(@Param("student")Long student,
                                             @Param("courseId")Long courseId);

    /**
     * WHA -  The student asked the teacher to connect to the network voice��Request list
     * @param page :Paging parameters
     * @param teacher : Teacher ID
     * @param student : Student ID
     * @param courseId :Course ID
     * @return
     */
    public List<Map> getEvenForWheatEndListPage(@Param("page")DataGridPage page,
                                             @Param("teacher")Long teacher,
                                             @Param("student")Long student,
                                             @Param("courseId")Long courseId);

    Long findByCouseIdAndApplyUser(@Param("courseId")Long courseId,@Param("applyUser")Long applyUser);
}
