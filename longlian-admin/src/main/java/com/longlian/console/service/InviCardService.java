package com.longlian.console.service;

import com.longlian.model.InviCard;

import java.util.List;

/**
 * Created by admin on 2017/2/16.
 */
public interface InviCardService {

    List<InviCard> getAllInviCard();

    void insertInviCard(InviCard inviCard);

    InviCard findInviCardById(long inviCard);

    void updateInviCard(InviCard inviCard);

    void delInviCardById(long id);
}
