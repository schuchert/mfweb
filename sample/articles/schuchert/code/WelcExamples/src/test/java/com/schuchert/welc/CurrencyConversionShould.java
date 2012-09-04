package com.schuchert.welc;

import mockit.NonStrictExpectations;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparisons.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class CurrencyConversionShould {
    //<codeFragment name="test_v1">
    @Test
    public void returnExpectedConversion_v1() {
        CurrencyConversion.convertFromTo("USD", "EUR");
    }
    //</codeFragment>

    //<codeFragment name="test_v2">
    @Test
    public void returnExpectedConversion_v2() {
        //<codeFragment name="test_v2_jmockit">
        new NonStrictExpectations(CurrencyConversion.class) {
            {
                CurrencyConversion.currencySymbols();
                result = mapFrom("USD", "EUR");
            }
        };
        //</codeFragment>
        CurrencyConversion.convertFromTo("USD", "EUR");
    }

    private Map<String, String> mapFrom(String... keyValuePairs) {
        Map<String, String> result = new ConcurrentHashMap<String, String>();
        for (int i = 0; i < keyValuePairs.length; ++i)
            result.put(keyValuePairs[i], keyValuePairs[i]);
        return result;
    }
    //</codeFragment>

    //<codeFragment name="test_v3">
    @Test
    public void returnExpectedConversion_v3() throws Exception {
        //<codeFragment name="test_v3_setup">
        //<codeFragment name="test_v3_setup_io">
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                "<div id=\"converter_results\"><ul><li><b>1 USD = 0.98 EUR</b>"
                        .getBytes());
        //</codeFragment>

        //<codeFragment name="test_v3_setup_jmockit">
        new NonStrictExpectations() {
            DefaultHttpClient httpclient;
            HttpResponse response;
            HttpEntity entity;

            {
                httpclient.execute((HttpUriRequest) any);
                result = response;
                response.getEntity();
                result = entity;
                entity.getContent();
                result = bais;
            }
        };
        //</codeFragment>
        //</codeFragment>

        new NonStrictExpectations(CurrencyConversion.class) {
            {
                CurrencyConversion.currencySymbols();
                result = mapFrom("USD", "EUR");
            }
        };
        BigDecimal result = CurrencyConversion.convertFromTo("USD", "EUR");
        assertThat(result.subtract(new BigDecimal(2)), is(lessThanOrEqualTo(new BigDecimal(0.001))));
    }
    //</codeFragment>

    //<codeFragment name="test_v4">
    @Test
    public void returnExpectedConversion_v4() throws Exception {
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                "<div id=\"converter_results\"><ul><li><b>1 USD = 0.98 EUR</b>"
                        .getBytes());

        new NonStrictExpectations() {
            DefaultHttpClient httpclient;
            HttpResponse response;
            HttpEntity entity;

            {
                httpclient.execute((HttpUriRequest) any);
                result = response;
                response.getEntity();
                result = entity;
                entity.getContent();
                result = bais;
            }
        };

        new NonStrictExpectations(CurrencyConversion2.class) {
            {
                CurrencyConversion2.currencySymbols();
                result = mapFrom("USD", "EUR");
            }
        };
        BigDecimal result = CurrencyConversion2.convertFromTo("USD", "EUR");
        assertThat(result.subtract(new BigDecimal(2)), is(lessThanOrEqualTo(new BigDecimal(0.001))));
    }
    //</codeFragment>

    //<codeFragment name="test_v5">
    class CurrencyConversion2_testingSubclass extends CurrencyConversion2 {
        @Override
        public void validateCurrencies(String fromCurrency, String toCurrency) {
        }

        @Override
        public Map<String, String> getAllCurrencySymbols() {
            return mapFrom("USD", "EUR");
        }

        @Override
        public String getPage(String fromCurrency, String toCurrency) {
            return "<div id=\"converter_results\"><ul><li><b>1 USD = 0.98 EUR</b>";
        }
    }

    @Test
    public void returnExpectedConversion_v5() throws Exception {
        CurrencyConversion2 original = CurrencyConversion2.reset(new CurrencyConversion2_testingSubclass());

        BigDecimal result = CurrencyConversion2.convertFromTo("USD", "EUR");

        assertThat(result.subtract(new BigDecimal(2)), is(lessThanOrEqualTo(new BigDecimal(0.001))));

        CurrencyConversion2.reset(original);
    }
    //</codeFragment>

    //<codeFragment name="test_final">
    @Test
    public void returnExpectedConversion_final() throws Exception {
        new NonStrictExpectations(CurrencyConversion2.class) {
            CurrencyConversion2 c;
            {
                c.validateCurrencies(anyString, anyString);
                c.getPage(anyString, anyString);
                result = "<div id=\"converter_results\"><ul><li><b>1 USD = 0.98 EUR</b>";
            }
        };
        BigDecimal result = CurrencyConversion2.convertFromTo("USD", "EUR");
        assertThat(result.subtract(new BigDecimal(2)), is(lessThanOrEqualTo(new BigDecimal(0.001))));
    }
    //</codeFragment>
}
