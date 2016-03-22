package com.jbburns.simplecurrencywidget;

import android.util.Xml;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by jbburns on 3/18/2016.
 */
public class SMBCTBRateProvider {
    // We don't use namespaces
    private static final String ns = null;
    private List<SMBCTBRate> rateList;

    public SMBCTBRate getRateByBaseCurrency (String currency){
        SMBCTBRate rate = null;
           if(!setRates()){
               return null;
           }

        for (int i = 0; i < rateList.size(); i++){
            SMBCTBRate SMBCTBRate = rateList.get(i);
            if (SMBCTBRate.getCounterCurrency().equals(currency)) {
                rate = SMBCTBRate;
            }
        }

        return rate;
    }

    public Boolean setRates() {
        RetrieveSMBCTBFeed getFeed = new RetrieveSMBCTBFeed();
        try {
            getFeed.execute().get();
            String result = getFeed.getResult();
            InputStream in = IOUtils.toInputStream(result);
            rateList = parseRates(in);
            return true;
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<SMBCTBRate> getRateList() throws XmlPullParserException, IOException,ParseException {
        RetrieveSMBCTBFeed getFeed = new RetrieveSMBCTBFeed();
        try {
            getFeed.execute().get();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        String result = getFeed.getResult();
        InputStream in = IOUtils.toInputStream(result);

        return parseRates(in);
    }

    public List<SMBCTBRate> parseRates(InputStream in) throws XmlPullParserException, IOException,ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return parseRatesXML(parser);
        } finally {
            in.close();
        }
    }

    private List<SMBCTBRate> parseRatesXML(XmlPullParser parser) throws XmlPullParserException, IOException,ParseException {
        //List rateList = new ArrayList;
        List<SMBCTBRate> rateList = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "ratetables");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String attributeName = parser.getAttributeName(0);
            String attributeValue = parser.getAttributeValue(ns, parser.getAttributeName(0));
            // Starts by looking for the entry tag
            if (name.equals("ratetable") && attributeName.equals("id") && attributeValue.equals("foreignexchange1")) {
                rateList = parseRateTable(parser);
            } else {
                skip(parser);
            }
        }
        return rateList;
    }

    private List parseRateTable(XmlPullParser parser) throws XmlPullParserException, IOException,ParseException {
        List rateList = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String attributeName = parser.getAttributeName(0);
            String attributeValue = parser.getAttributeValue(ns, parser.getAttributeName(0));
            // Starts by looking for the entry tag
            if (name.equals("content") && attributeName.equals("font-language") && attributeValue.equals("en")) {
                parser.next();
                Date asOfTime = readCaption(parser);
                rateList = readRates(parser, asOfTime);
            } else {
                skip(parser);
            }
        }

        return rateList;
    }

    private Date readCaption(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException{
        parser.require(XmlPullParser.START_TAG, ns, "caption");
        String caption = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "caption");
        String cutCaption = caption.replaceAll("As of : ", "");
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date asOfTime = inputFormat.parse(cutCaption);

        return asOfTime;
    }

    private List readRates(XmlPullParser parser, Date asOfTime) throws  XmlPullParserException, IOException, ParseException{
        List rates = new ArrayList();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("row") && (parser.getAttributeCount() == 0)) {
                SMBCTBRate rate = readRate(parser,asOfTime);
                rates.add(rate);
            } else {
                skip(parser);
            }
        }

        return rates;
    }

    private SMBCTBRate readRate (XmlPullParser parser, Date asofTime) throws  XmlPullParserException, IOException, ParseException{

        String counterCurrency = null;
        Double sellRate = null;
        Double midRate = null;
        Double buyRate = null;
        Double demandRate = null;
        Double spotRateMin = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String attributeName = parser.getAttributeName(0);
            String attributeValue = parser.getAttributeValue(ns, parser.getAttributeName(0));
            //System.out.println("Name: [" + name + "] attr name: [" + attributeName + "] Attr value: [" + attributeValue +"]");
            if (name.equals("col") && attributeName.equals("id") && attributeValue.equals("foreignexchange1_id_1")) {
                counterCurrency = readCounterCurrency(parser);
                // System.out.println("counterCurrency is" + counterCurrency);
            } else if (name.equals("col") && attributeName.equals("id") && attributeValue.equals("foreignexchange1_id_2")) {
                sellRate = readRateAmount(parser);
                // System.out.println("sell rate is" + sellRate);
            } else if (name.equals("col") && attributeName.equals("id") && attributeValue.equals("foreignexchange1_id_3")) {
                midRate = readRateAmount(parser);
                //  System.out.println("mid rate is" + midRate);
            } else if (name.equals("col") && attributeName.equals("id") && attributeValue.equals("foreignexchange1_id_4")) {
                buyRate = readRateAmount(parser);
                // System.out.println("sell rate is" + buyRate);
            } else if (name.equals("col") && attributeName.equals("id") && attributeValue.equals("foreignexchange1_id_5")) {
                demandRate = readRateAmount(parser);
                // System.out.println("sell rate is" + demandRate);
            } else if (name.equals("col") && attributeName.equals("id") && attributeValue.equals("foreignexchange1_id_6")) {
                spotRateMin = readRateAmount(parser);
                //System.out.println("sell rate is" + spotRateMin);
            }
            else {
                System.out.println("Skipped something probably shouldnt have");
                skip(parser);
            }
        }

        return new SMBCTBRate(asofTime,counterCurrency,sellRate,midRate,buyRate,demandRate,spotRateMin);
    }

    private String readCounterCurrency(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "col");
        String counterCurrency = readText(parser);
        String chopped = counterCurrency.replaceAll("^.*\\(", "");
        counterCurrency = chopped;
        chopped = counterCurrency.replaceAll("\\)", "");
        counterCurrency = chopped;
        parser.require(XmlPullParser.END_TAG, ns, "col");
        return counterCurrency;
    }

    private Double readRateAmount(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "col");
        Double rateAmount = Double.parseDouble(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "col");
        return rateAmount;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
