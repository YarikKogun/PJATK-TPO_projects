/**
 *
 *  @author Kohun Yaroslav S15258
 *
 */

package zad1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;

public class Service {
    private static final String WEATHER_KEY     = "&appid=a61c1d6a07209b93c2eb6eb70f126e32&units=metric";
    private static final String CURRENCY_KEY    = "f6752ab1c49f54ea675bc904f6a813f0";

    private static String country;
    private static String city;
    private static String currency;
    private static String rate;

    public Service(String country) {
        this.country = country;

        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);

            if (l.getDisplayCountry().equals(this.country)) {
                currency = Currency.getInstance(l).getCurrencyCode();
                break;
            }
        }
    }

    private static String getPage(String urlStr) throws IOException {
        URL url = new URL(urlStr);

        InputStream inputStream = url.openStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder stringBuilder = new StringBuilder();
        int chr;

        while ((chr = bufferedReader.read()) != -1)
            stringBuilder.append((char) chr);

        inputStream.close();

        return stringBuilder.toString();
    }

    public String getWeather(String city) {
        this.city = city;
        try {
            return getPage("http://api.openweathermap.org/data/2.5/weather?q=" + this.city + WEATHER_KEY);
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }

    public Double getRateFor(String c) {
        rate = c;
        try {
            JSONObject json = new JSONObject(getPage("http://data.fixer.io/api/latest?access_key="+CURRENCY_KEY));

            BigDecimal d1 = new BigDecimal(json.getJSONObject("rates").getDouble(currency));
            BigDecimal d2 = new BigDecimal(json.getJSONObject("rates").getDouble(rate));

            return (d1.divide(d2, 4, BigDecimal.ROUND_DOWN)).doubleValue();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double getNBPRate() {

        if (currency.equals("PLN"))
            return 1.0;

        BigDecimal c1;
        BigDecimal c2 = null;

        try {
            String page = getPage("http://www.nbp.pl/kursy/kursya.html");

            if (!page.contains(currency))
                page = getPage("http://www.nbp.pl/kursy/kursyb.html");

            int tmp = page.indexOf(currency);
            page = page.substring(tmp - 4, tmp + 60);

            if (page.charAt(0) == '1')
                c1 = new BigDecimal(100);
            else
                c1 = new BigDecimal(1);

            page = page.substring(45).replaceAll(",", ".");

            Pattern p = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)");
            Matcher m = p.matcher(page);
            while (m.find())
                c2 = new BigDecimal(m.group(1));

            return c2.divide(c1, 4, BigDecimal.ROUND_DOWN).doubleValue();

        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }

    public static String getCity() {return city;}
    public static String getCountry() {return country;}
    public static String getCurrency() {return currency;}
    public static String getRate() {return rate;}
}
