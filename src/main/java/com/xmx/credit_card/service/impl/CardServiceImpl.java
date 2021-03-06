package com.xmx.credit_card.service.impl;

import com.xmx.credit_card.command.CreateCardCommand;
import com.xmx.credit_card.constant.CardStatus;
import com.xmx.credit_card.constant.CardType;
import com.xmx.credit_card.constant.DataSimulator;
import com.xmx.credit_card.dao.CardMapper;
import com.xmx.credit_card.entity.Card;
import com.xmx.credit_card.entity.CardExample;
import com.xmx.credit_card.service.CardService;
import com.xmx.credit_card.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private UserService userService;

    @Override
    public void repayment(String cardNumber, BigDecimal amount) {
        BigDecimal previousCardAmount=getAmountByCardNumber(cardNumber);
        if(previousCardAmount==null)
            throw new RuntimeException("No such card");
        updateCardByCardNumber(cardNumber,previousCardAmount.add(amount),null);
        String account=getCardByCardNumber(cardNumber).getAcct();
        BigDecimal previousUserAmount=userService.getAmountByAccount(account);
        if(previousUserAmount!=null)
            userService.updateAmount(account,previousUserAmount.add(amount));
    }

    @Override
    public List<Card> getCardListByCondition(String account, CardType cardType,CardStatus cardStatus) {
        CardExample example=new CardExample();
        CardExample.Criteria criteria=example.createCriteria();
        if(!StringUtils.isEmpty(account))
            criteria.andAcctEqualTo(account);
        if(cardType!=null)
            criteria.andCardTypeEqualTo(cardType.name());
        if(cardStatus!=null)
            criteria.andStatusEqualTo(cardStatus.toString());
        return cardMapper.selectByExample(example);
    }

    @Override
    public void approveCardApply(String cardNumber, boolean approve) {
        Card card=new Card();
        card.setCardNumber(cardNumber);
        if(approve)
            card.setStatus(CardStatus.ACTIVE);
        else
            card.setStatus(CardStatus.REJECTED);
        cardMapper.updateByPrimaryKeySelective(card);
    }

    @Override
    public Card getCardByCardNumber(String cardNumber) {
        return cardMapper.selectByPrimaryKey(cardNumber);
    }

    @Override
    public void eventRepay(String cardNumber, Integer point, BigDecimal amount){
        cardMapper.eventRepay(cardNumber,point,amount);
    }

    @Override
    public void play(String cardNumber, Integer point){
        cardMapper.play(cardNumber,point);
    }

    @Override
    public void updateCardByCardNumber(String cardNumber, BigDecimal amount,Integer point) {
        Card card=new Card();
        card.setCardNumber(cardNumber);
        if(amount!=null)
            card.setCardAmount(amount);
        if(point!=null)
            card.setCardPoint(point);
        cardMapper.updateByPrimaryKeySelective(card);
    }

    @Override
    public BigDecimal getAmountByCardNumber(String cardNumber) {
        return cardMapper.selectByPrimaryKey(cardNumber).getCardAmount();
    }

    @Override
    public void deleteCard(String cardNumber) {
        Card card=new Card();
        card.setCardNumber(cardNumber);
        card.setStatus(CardStatus.DELETE);
        cardMapper.updateByPrimaryKeySelective(card);

    }

    @Override
    public List<Integer> countCardGroupByCardType() {
        return cardMapper.countCardGroupByCardType();
    }

    @Override
    public List<Integer> sumCardPointByCard(String account) {
        return cardMapper.sumCardPointByCard(account);
    }

    @Override
    public List<Integer> countCardGroupByAcct() {
        return cardMapper.countCardGroupByAcct();
    }
    @Override
    public boolean applyCard(CreateCardCommand command) {
        if(!CollectionUtils.isEmpty(getCardListByCondition(command.getAccount(),command.getCardType(),CardStatus.ACTIVE)))
            throw new RuntimeException("You have already had the same card!");
        if(!CollectionUtils.isEmpty(getCardListByCondition(command.getAccount(),command.getCardType(),CardStatus.INACTIVE)))
            throw new RuntimeException("You have already applied the card,please wait for approving!");
        Card card =new Card();
        card.setAcct(command.getAccount());
        card.setCardType(command.getCardType());
        String cardNumber=DataSimulator.randomCardNumber(command.getCardType().name());
        while(!StringUtils.isEmpty(cardMapper.selectByPrimaryKey(cardNumber)))
            cardNumber=DataSimulator.randomCardNumber(command.getCardType().name());
        card.setCardNumber(cardNumber);
        card.setInqpwd(command.getPassword());
        card.setStatus(CardStatus.INACTIVE);
        card.setCreateTime(LocalDateTime.now());
        switch (command.getCardType().name()){
            case "BABY_GROWUP":
                card.setCardAmount(new BigDecimal(2000));
                break;
            case "COMMERICAL":
                card.setCardAmount(new BigDecimal(3000));
                break;
            case "GLOBAL_TRAVEL":
                card.setCardAmount(new BigDecimal(4000));
                break;
            case "CHANGLONG":
                card.setCardAmount(new BigDecimal(5000));
                break;
            default:
                card.setCardAmount(new BigDecimal(2000));
        }
        cardMapper.insert(card);
        return true;
    }
}
