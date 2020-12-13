package com.example.projectdemo.service;

import com.example.projectdemo.dao.*;
import com.example.projectdemo.model.Stock;
import com.example.projectdemo.model.Trade;
import com.example.projectdemo.model.Transaction;
import com.example.projectdemo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockTradeService
{
    @Autowired
    private StockTradeDAO stockTradeDAO;

    private static final Logger logger = LoggerFactory.getLogger(StockTradeService.class);

    public User getProfileAttributes(String email) {
        return this.stockTradeDAO.getProfileAttributes(email);
    }

    public Map<String , List<? extends Object>> retrieveBuyList ( org.apache.catalina.User user, String[] stockSymbols)
    {
        return this.stockTradeDAO.retrieveBuyList((User) user, stockSymbols);
    }

    public Transaction completeTransaction (org.apache.catalina.User user, String[] quantities , List<? extends Object> stockBuying ,String[] selling )
    {
        Transaction currenTransaction = this.stockTradeDAO.createTransaction((User) user);
        if (selling !=null)
        {
            Set<Trade> sellTradesSet = new HashSet<Trade>();
            for (int i = 0; i < selling.length; i++)
            {
                String temp[] = selling[i].split(Pattern.quote("!"));
                Stock s = this.stockTradeDAO.checkIfStockExists(temp[0]);
                       
            }          
        }


        Set<Trade> buyTradesSet = new HashSet<Trade>();
        for (int i = 0; i < stockBuying.size(); i++)
        {
                Stock s = (Stock) stockBuying.get(i);
                Trade buyTrade = this.stockTradeDAO.createBuyTrade (user , s , quantities[i] , currenTransaction );
                buyTradesSet.add(buyTrade);
        }
        
        return currenTransaction;
    }

    public List<Trade> getBuyTradesUsingCurrentTransaction (User user , Transaction transaction)
    {
        Transaction h = this.stockTradeDAO.getLatestTransaction(user.getEmail());
        logger.info("Fetching latest transaction from user::"+h.getId());
        List<Trade> buyTrades = this.stockTradeDAO.getTradesUsingTransaction (h.getId()  , "Buy");
        return buyTrades;

    }

    public List<Trade> getSellTradesUsingCurrentTransaction (User user , Transaction transaction)
    {
        Transaction h = this.stockTradeDAO.getLatestTransaction(user.getEmail());
        logger.info("Fetching latest transaction from user::"+h.getId());
        List<Trade> sellTrades = this.stockTradeDAO.getTradesUsingTransaction (h.getId()  , "Sell");
        return sellTrades;

    }


    public double getTotalPrice (List<Trade> trades)
    {
        double result =0.0;
        for (Trade tr: trades)
        {
            result = result + (tr.getIndividualPrice() * tr.getQuantity());
        }
        return result;
    }


    public List<Transaction> getAllTransactions (String email)
    {
        return this.stockTradeDAO.getAllTransactions (email);
    }

	public boolean checkIfAccountAttached(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	}
