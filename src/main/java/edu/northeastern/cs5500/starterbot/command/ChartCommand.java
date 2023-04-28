package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.chart.ChartUtils;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
@Slf4j
public class ChartCommand implements SlashCommandHandler {

    @Inject
    public ChartCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "chart";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Ask the bot to fetch the chart for the ticker")
                .addOption(
                        OptionType.STRING, "ticker", "The bot will return the chart", true)
                .addOption(OptionType.INTEGER, "time", "The time period for stock data", true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /balance");

        var tickerOption = event.getOption("ticker");
        String ticker = tickerOption.getAsString();
        var timeOption = event.getOption("time");
        int time = timeOption.getAsInt();

        log.info(ticker + "   " + time);

        generateChart();

    }

    public void generateChart() {

        OHLCSeries series = new OHLCSeries("Stock Prices");
        series.add(new Day(1, 1, 2022), 100, 120, 90, 110);
        series.add(new Day(2, 1, 2022), 110, 140, 100, 120);
        series.add(new Day(3, 1, 2022), 120, 130, 110, 115);
        series.add(new Day(4, 1, 2022), 115, 125, 105, 120);
        series.add(new Day(5, 1, 2022), 120, 140, 115, 135);
        OHLCSeriesCollection dataset = new OHLCSeriesCollection();
        dataset.addSeries(series);
    
        // Create chart
        JFreeChart chart = ChartFactory.createCandlestickChart(
            "Stock Prices", // chart title
            "Date", // x axis label
            "Price", // y axis label
            dataset, // data
            true // legend
        );

        CandlestickRenderer renderer = new CandlestickRenderer();
        renderer.setDrawVolume(false);
        renderer.setUpPaint(Color.GREEN);
        renderer.setDownPaint(Color.RED);
        chart.getXYPlot().setRenderer(renderer); 


        // ChartFrame frame = new ChartFrame("Stock Prices", chart);
        // frame.pack();
        // frame.setVisible(true);


        // ChartPanel panel = new ChartPanel(chart);
        
        // try {
        //     // ChartUtils.saveChartAsPNG(new File("candlestick-chart.png"), chart, 800, 600);
        //     OutputStream out = new FileOutputStream("chartName");
        //     ChartUtils.writeChartAsPNG(out,
        //     chart,
        //     800,
        //     600);
        //     System.out.println("Chart saved as candlestick-chart.png");
        // } catch (Exception e) {
        //     System.err.println("Error saving chart: " + e.getMessage());
        // }

        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
        Rectangle r = new Rectangle(0, 0, 600, 400);
        chart.draw(g2, r);
        File f = new File("/tmp/PNGTimeSeriesChartDemo1.png");

        BufferedImage chartImage = chart.createBufferedImage( 600, 400, null); 
        try {
            ImageIO.write( chartImage, "png", f );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        
    }
    
}
