package com.xmx.credit_card.service.impl;

import com.xmx.credit_card.dao.CardMapper;
import com.xmx.credit_card.entity.Card;
import com.xmx.credit_card.entity.CardExample;
import com.xmx.credit_card.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CardServiceImpl implements CardService {
    @Autowired
   private CardMapper cardMapper;
    @Override
    public List<Card> getCardListByCondition() {
        CardExample example=new CardExample();
        CardExample.Criteria criteria=example.createCriteria();
        criteria.andCardPointGreaterThan(2);
        return cardMapper.selectByExample(example);
    }
}