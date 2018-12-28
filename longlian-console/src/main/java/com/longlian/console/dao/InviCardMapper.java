package com.longlian.console.dao;

import com.longlian.model.InviCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
