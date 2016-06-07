package org.eclipse.iot.ttntutorial;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.TimeStringConverter;

public class Main extends Application {
	@Override
	public void start(Stage stage) {
		stage.setTitle("Live temperature monitoring");
		// defining the axes
		final NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Time");
		xAxis.setForceZeroInRange(false);
		xAxis.setTickLabelFormatter(new StringConverter<Number>() {
			TimeStringConverter tsc = new TimeStringConverter("HH:mm:ss");

			@Override
			public String toString(Number t) {
				return tsc.toString(new Date(t.longValue()));
			}

			@Override
			public Number fromString(String string) {
				return 1;
			}
		});

		final NumberAxis yAxis = new NumberAxis();
		yAxis.setForceZeroInRange(false);

		// creating the chart
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle("Live luminosity monitoring");
		// defining a series
		final Series<Number, Number> series = new XYChart.Series<Number, Number>();
		series.setName("Luminosity");

		Scene scene = new Scene(lineChart, 800, 600);
		lineChart.getData().add(series);

		try {
			final String topic = "+/devices/+/up";
			final String broker = "tcp://staging.thethingsnetwork.org:1883";
			final String username = "xxx";
			final String password = "xxx=";

			final String clientId = MqttClient.generateClientId();

			final MqttClient mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());

			mqttClient.setCallback(new MqttCallback() {

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {

					try {
						System.out.println(
								"Message received on topic '" + topic + "': " + new String(message.getPayload()));

						// decode the JSON
						JsonElement root = new JsonParser().parse(new String(message.getPayload()));
						final float luminosity = root.getAsJsonObject().get("fields").getAsJsonObject().get("lux")
								.getAsFloat();

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								series.getData().add(new Data<Number, Number>(System.currentTimeMillis(), luminosity));
							}
						});

					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					// not used
				}

				@Override
				public void connectionLost(Throwable cause) {
					System.out.println("Connection lost: " + cause.getLocalizedMessage());
				}
			});
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setUserName(username);
			connOpts.setPassword(password.toCharArray());
			System.out.println("Connecting to broker: " + broker);
			mqttClient.connect(connOpts);
			System.out.println("Connected");
			mqttClient.subscribe(topic);

		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}