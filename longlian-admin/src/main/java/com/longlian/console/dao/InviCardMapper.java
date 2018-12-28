package com.longlian.console.dao;

import com.longlian.model.InviCard;

import java.util.List;

/**
 * Created by admin on 2017/2/16.
 */
public interface InviCardMapper {

    List<InviCard> getAllInviCard();

    void insertInviCard(InviCard inviCard);

    InviCard findInviCardById(long inviCard);

    void updateInviCard(InviCard inviCard);

    void delInviCardById(long id);

}
