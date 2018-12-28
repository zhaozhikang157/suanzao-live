package com.longlian.dto;

import com.longlian.model.AppUser;

/**
 * Created by liuhan on 2017-06-29.
 */
public class AppUserDto extends AppUser {
    public String isInRoom;

    public String getIsInRoom() {
        return isInRoom;
    }

    public void setIsInRoom(String isInRoom) {
        this.isInRoom = isInRoom;
    }
}
