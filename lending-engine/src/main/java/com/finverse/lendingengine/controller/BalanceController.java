package com.finverse.lendingengine.controller;

import com.finverse.lendingengine.model.Money;
import com.finverse.lendingengine.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PostMapping("/topup")
    public ResponseEntity<String> topUp(final @RequestBody Money amount, @RequestHeader("X-User-ID") UUID userId){
        try {
            balanceService.topUpBalance(amount,userId);
            return new ResponseEntity<>("Top up successful!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error:"+e,HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(final @RequestBody Money amount, @RequestHeader("X-User-ID") UUID userId){
        try {
            balanceService.withdrawBalance(amount,userId);
            return new ResponseEntity<>("Withdrawal successful!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error:"+e,HttpStatus.BAD_REQUEST);
        }

    }
}
