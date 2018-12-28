package com.longlian.console.service.impl;

import com.longlian.console.dao.InviCardMapper;
import com.longlian.console.service.InviCardService;
import com.longlian.model.InviCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by admin on 2017/2/16.
 */
@Service("inviCardService")
public class InviCardServiceImpl implements InviCardService {
    private static Logger log = LoggerFactory.getLogger(InviCardServiceImpl.class);

    @Autowired
    InviCardMapper inviCardMapper;

    @Override
    public List<InviCard> getAllInviCard() {
        return inviCardMapper.getAllInviCard();
    }

    @Override
    public void insertInviCard(InviCard inviCard) {
        inviCardMapper.insertInviCard(inviCard);
    }

    @Override
    public InviCard findInviCardById(long id) {
        return inviCardMapper.findInviCardById(id);
    }

    @Override
    public void updateInviCard(InviCard inviCard) {
        inviCardMapper.updateInviCard(inviCard);
    }

    @Override
    public void delInviCardById(long id) {
        inviCardMapper.delInviCardById(id);
    }
}
