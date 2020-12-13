package com.example.projectdemo.controller;

import com.example.projectdemo.Base.FileReaderUtil;
import com.example.projectdemo.model.Trade;
import com.example.projectdemo.model.Transaction;
import com.example.projectdemo.service.StockTradeService;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StockTradeController {
    private static final Logger logger = LoggerFactory.getLogger(StockTradeController.class);

    @Autowired
    private StockTradeService stockTradeService;
    
    @PostMapping(value = "/check/account.htm" , consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkIfAccountAttached (HttpServletRequest request , @RequestBody HashMap<String, String> stockSymbol)
    {
        logger.info("checkIfAccountAttached::");
        String email = (String) request.getSession().getAttribute("user");
        User user;

        boolean result = this.stockTradeService.checkIfAccountAttached(null);

        if (!result){
            return  ResponseEntity.badRequest().body("No user bank details found for user");
        }
        else {
            Map<String , String> res = new HashMap<String, String>();
            res.put("result", "Success");
            return ResponseEntity.ok(res);
        }

    }

    @PostMapping(value = "/trade/watchlist.htm")
    public ModelAndView addToTrade(HttpServletRequest request) {
        logger.info("addToTrade::");
        String email = (String) request.getSession().getAttribute("user");
        User user = (User) this.stockTradeService.getProfileAttributes(email);

        String[] stockSymbols = request.getParameterValues("checkedRows");

        Map<String, List<? extends Object>> stockList = this.stockTradeService.retrieveBuyList(user, stockSymbols);
        ModelAndView mv = new ModelAndView();
        mv.addObject("stockList", stockList);
        mv.setViewName("user-trade");

        request.getSession().setAttribute("stockList", stockList);

        return mv;
    }

    @PostMapping(value = "/trade/transaction.htm")
    public ModelAndView showTradeTransaction(HttpServletRequest request) {
        logger.info("showTradeTransaction::");
        String email = (String) request.getSession().getAttribute("user");
        User user = (User) this.stockTradeService.getProfileAttributes(email);

        String[] quantities = request.getParameterValues("quantity");

        String[] selling = request.getParameterValues("checkedRows");

        Map<String, List<? extends Object>> stockList = (Map<String, List<? extends Object>>) request.getSession()
                .getAttribute("stockList");

        List<? extends Object> stockBuying = stockList.get("Buy");

        ModelAndView mv = new ModelAndView();
        mv.setViewName("user-transaction");

        mv.addObject("transactionBuyId", new Random().nextInt(1000000));
        mv.addObject("transactionSellId", new Random().nextInt(1000000));    


        Object currentBuyTrades = null;
		mv.addObject("tradeBuyList", currentBuyTrades);
        Object currentSellTrades = null;
		mv.addObject("tradeSellList", currentSellTrades);

        return mv;
    }

    @GetMapping(value = "/statement.htm")
    public ModelAndView home(HttpServletRequest request) 
    {
            logger.info("Please login to access this page");
            ModelAndView mv = new ModelAndView("login-form");
            mv.addObject("user");
            mv.addObject("errorMessage", "Please login to access this page");
            return mv;
    }

  
}