package com.finverse.lendingengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    @Id
    @GeneratedValue
    private UUID id;

    private double amount;

//    @ElementCollection
    @MapKeyClass(Currency.class)
    @OneToMany(targetEntity = Money.class,cascade = CascadeType.ALL)
    public Map<Currency,Money> moneyMap = new HashMap<>();

    public void topUp(final Money money){
        if(moneyMap.get(money.getCurrency())==null){
            moneyMap.put(money.getCurrency(),money);
        }else{
            moneyMap.put(money.getCurrency(),
                    moneyMap.get(money.getCurrency()).add(money));
        }
    }

    public void withdraw(final Money money){
        final Money moneyInBalance = moneyMap.get(money.getCurrency());
        if(moneyInBalance==null){
            throw new IllegalArgumentException();
        }else{
            moneyMap.put(money.getCurrency(),moneyMap.get(money.getCurrency()).minus(money));
        }
    }

//    public Map<Currency, Money> getMoneyMap() {
//        return moneyMap;
//    }
}
