package com.xmx.credit_card.service;

import com.xmx.credit_card.command.BuyGoodsCommand;
import com.xmx.credit_card.constant.ApplicaplePeople;
import com.xmx.credit_card.constant.ProductType;
import com.xmx.credit_card.entity.Goods;

import java.util.List;

public interface GoodsService {

    Goods findGoodsById(Long id);

    List<Goods> getGoodsByCondition(ProductType productType, ApplicaplePeople applicaplePeople,String lowPrice,String highPrice,String orderBy,String order);

    void buyGoods(BuyGoodsCommand buyGoodsCommand);

    void updateGoodsCount(Long id);

    List<Goods> getHotGoods(String account);

    void insert(Goods goods);
}
