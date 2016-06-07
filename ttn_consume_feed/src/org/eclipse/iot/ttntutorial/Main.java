package org.eclipse.iot.ttntutorial;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Main {
	public static void main(String[] args) {
		final String topic = "+/devices/+/up";
		final String broker = "tcp://staging.thethingsnetwork.org:1883";
		final String username = "xxx";
		final String password = "xxx=";

		final String clientId = MqttClient.generateClientId();

		try {
			MqttClient sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
			sampleClient.setCallback(new MqttCallback() {

				@Override
				public void messageArrived(String topic, MqttMessage msg) throws Exception {
					System.out.println("Message received on topic '" + topic + "': " + new String(msg.getPayload()));

					// decode the JSON
					// TODO
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
				}

				@Override
				public void connectionLost(Throwable t) {
					System.out.println("Connection lost");
				}
			});
			MqttConnectOptions connOpts = new MqttConnectOptions();
			// TODO set connect options (login, etc) and effectively connect
			// ...
			System.out.println("Connected");
			// TODO subscribe to uplink topic
			// ...
		} catch (MqttException me) {
			me.printStackTrace();
		}
	}
}
