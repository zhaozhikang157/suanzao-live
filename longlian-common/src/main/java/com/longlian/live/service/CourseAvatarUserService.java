package com.longlian.live.service;

/**
 * Created by liuhan on 2017-06-28.
 */
public interface CourseAvatarUserService {

    public void batchInsertUsers(Long courseId , String[] userId);

    public void insertUser(Long courseId , Long  userId);

    public void batchRemoveUsers(long id, String[] userId);

    void deleteAll(long id);
}
