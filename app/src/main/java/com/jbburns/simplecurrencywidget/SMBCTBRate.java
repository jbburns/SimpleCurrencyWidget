package com.jbburns.simplecurrencywidget;

import java.util.Date;

/**
 * Created by jbburns on 3/18/2016.
 */
public class SMBCTBRate {
    private Date asOfTime;
    private String counterCurrency;
    private Double sellRate;
    private Double midRate;
    private Double buyRate;
    private Double demandRate;
    private Double spotRateMin;

    public SMBCTBRate(Date asOfTime, String counterCurrency, Double sellRate, Double midRate, Double buyRate, Double demandRate, Double spotRateMin) {
        this.asOfTime = asOfTime;
        this.counterCurrency = counterCurrency;
        this.sellRate = sellRate;
        this.midRate = midRate;
        this.buyRate = buyRate;
        this.demandRate = demandRate;
        this.spotRateMin = spotRateMin;
    }

    public Date getAsOfTime() {
        return asOfTime;
    }

    public String getCounterCurrency() {
        return counterCurrency;
    }

    public Double getSellRate() {
        return sellRate;
    }

    public Double getMidRate() {
        return midRate;
    }

    public Double getBuyRate() {
        return buyRate;
    }

    public Double getDemandRate() {
        return demandRate;
    }

    public Double getSpotRateMin() {
        return spotRateMin;
    }

    @Override
    public String toString() {
        return "As of: " + asOfTime.toString()
                + "\nCounter Currency: " + this.counterCurrency
                + "\n Sell Rate: " + sellRate
                + "\n Mid Rate: " + midRate
                + "\n Buy Rate: " + buyRate
                + "\n Demand Rate: " + demandRate
                + "\n Spot Rate Minimum: " + spotRateMin;
    }
}
