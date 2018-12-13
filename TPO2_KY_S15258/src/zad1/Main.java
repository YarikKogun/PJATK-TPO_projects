/**
 *
 *  @author Kohun Yaroslav S15258
 *
 */

package zad1;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;

import org.json.*;

public class Main {
  public static void main(String[] args) {
    Service service = new Service("Poland");
    String serviceWeather = service.getWeather("Warsaw");
    Double serviceRateFor = service.getRateFor("USD");
    Double serviceNBPRate = service.getNBPRate();

    String  weather;
    JSONObject jsonObject = new JSONObject(serviceWeather);
    weather = jsonObject.getJSONObject("main").getDouble("temp")+"°C, ";
    weather += jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

    JFrame jFrame = new JFrame("---Service---");
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    jFrame.getContentPane().setLayout(null);

    final JFXPanel browser = new JFXPanel();

    jFrame.getContentPane().add(browser);
    jFrame.setVisible(true);

    browser.setSize(new Dimension(600, 800));
    browser.setLocation(new Point(0, 330));

    jFrame.getContentPane().setPreferredSize(new Dimension(600, 800));

    JLabel weatherLab = new JLabel("Weather: " + weather);
    weatherLab.setVerticalAlignment(SwingConstants.BOTTOM);
    weatherLab.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
    weatherLab.setBounds(60, 0, 500, 100);
    jFrame.getContentPane().add(weatherLab);

    JLabel currencyLab = new JLabel("1.0 " + Service.getRate() + " = " + serviceRateFor +" "+ Service.getCurrency());
    currencyLab.setVerticalAlignment(SwingConstants.BOTTOM);
    currencyLab.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
    currencyLab.setBounds(60, 100, 500, 100);
    jFrame.getContentPane().add(currencyLab);
    
    JLabel rateLab = new JLabel("1.0 "  + Service.getCurrency() + " = "+ serviceNBPRate + " PLN");
    rateLab.setVerticalAlignment(SwingConstants.BOTTOM);
    rateLab.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
    rateLab.setBounds(60, 200, 500, 100);
    jFrame.getContentPane().add(rateLab);
    
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        Group group = new Group();
        Scene scene = new Scene(group);
        browser.setScene(scene);

        WebView webView = new WebView();

        group.getChildren().add(webView);
        webView.setMinSize(600, 800);
        webView.setMaxSize(600, 800);

        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://uk.wikipedia.org/wiki/" + Service.getCity());
      }
    });

    jFrame.pack();
    jFrame.setResizable(false);
  }
}
