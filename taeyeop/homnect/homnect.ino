#include <EEPROM.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <SimpleTimer.h>

SimpleTimer timer;

//EEPROM
#define EEPROM_SIZE 256

//AP
#define AP_SSID "HomeNect"
#define AP_PASSWORD "HomeNect605"
#define BUF_SIZE 40

//MQTT
#define MQTT_SERVER "172.20.10.3"
#define MQTT_SERVER_PORT 1883

const unsigned int ROM_SSID_SIZE_ADDR = 0;
const unsigned int ROM_SSID_ADDR = ROM_SSID_SIZE_ADDR+1;
const unsigned int ROM_PWD_SIZE_ADDR = 50;
const unsigned int ROM_PWD_ADDR = ROM_PWD_SIZE_ADDR+1;
const unsigned int ROM_TOPIC_SIZE_ADDR = 100;
const unsigned int ROM_TOPIC_ADDR = ROM_TOPIC_SIZE_ADDR+1;
const unsigned int ROM_FAULT = 0xff;

const unsigned int RELAY_PIN = 12;
const unsigned int LED_PIN = 13;
const unsigned int BUTTON_PIN = 0;
const unsigned int OP_CODE_RELAY_OFF = 0;
const unsigned int OP_CODE_RELAY_ON = 1;

char stn_ssid[BUF_SIZE];
char stn_pwd[BUF_SIZE];
char mqtt_topic[BUF_SIZE];

String mac_addr;
String update_status_payload_prefix;
String update_status_payload_suffix;

bool is_station_mode;

ESP8266WebServer server(80);
WiFiClient wifi_client;
PubSubClient mqtt_client(wifi_client);

void eeprom_read(const unsigned int addr, char *data) {
  uint8_t data_size = EEPROM.read(addr);
  for(unsigned int i=0;i<data_size && i<BUF_SIZE;i++) {
    data[i] = EEPROM.read(addr+1+i);
  }
  data[data_size] = '\0';
}
void eeprom_write(const unsigned int addr, char *data, const unsigned int data_size) {
  EEPROM.write(addr, data_size);
  for(unsigned int i=0;i<data_size;i++) {
    EEPROM.write(addr+1+i, data[i]);
  }
  EEPROM.commit();
}

void ap_init() {
  EEPROM.begin(EEPROM_SIZE);
  
  memset(stn_ssid, 0, sizeof(stn_ssid));
  memset(stn_pwd, 0, sizeof(stn_pwd));
  memset(mqtt_topic, 0, sizeof(mqtt_topic));
  
  eeprom_read(ROM_SSID_SIZE_ADDR, stn_ssid);
  Serial.print("EEPROM SSID : ");
  Serial.println(stn_ssid);
  eeprom_read(ROM_PWD_SIZE_ADDR, stn_pwd);
  Serial.print("EEPROM PWD : ");
  Serial.println(stn_pwd);
  eeprom_read(ROM_TOPIC_SIZE_ADDR, mqtt_topic);
  Serial.print("EEPROM TOPIC : ");
  Serial.println(mqtt_topic);
  Serial.println("<<AP inited!>>");
}

void ap_set_info() {
  eeprom_write(ROM_SSID_SIZE_ADDR, stn_ssid, strlen(stn_ssid));
  Serial.print("EEPROM SSID : ");
  Serial.println(stn_ssid);
  eeprom_write(ROM_PWD_SIZE_ADDR, stn_pwd, strlen(stn_pwd));
  Serial.print("EEPROM PWD : ");
  Serial.println(stn_pwd);
  eeprom_write(ROM_TOPIC_SIZE_ADDR, mqtt_topic, strlen(mqtt_topic));
  Serial.print("EEPROM TOPIC : ");
  Serial.println(mqtt_topic);
  Serial.println("<<AP seted!>>");
}

void gpio_init(void) {
  pinMode(RELAY_PIN, OUTPUT);
  pinMode(BUTTON_PIN, INPUT);
  pinMode(LED_PIN, OUTPUT);
  
  digitalWrite(RELAY_PIN, HIGH);
  digitalWrite(LED_PIN, HIGH);
  Serial.println("<<GPIO inited!>>");
}

void recv_ap_info() {
  String ssid = server.arg("ssid");
  String pwd = server.arg("pwd");
  String topic = server.arg("topic");
  strncpy(stn_ssid, ssid.c_str(), ssid.length());
  stn_ssid[ssid.length()] = '\0';
  strncpy(stn_pwd, pwd.c_str(), pwd.length());
  stn_pwd[pwd.length()] = '\0';
  strncpy(mqtt_topic, topic.c_str(), topic.length());
  mqtt_topic[topic.length()] = '\0';

  Serial.print("ssid=");
  Serial.print(stn_ssid);
  Serial.print(" password=");
  Serial.print(stn_pwd);
  Serial.print(" topic=");
  Serial.println(mqtt_topic);
  server.send(200, "text/html", "{\"data\":{\"result\":\"success\"}}");
  ap_set_info();

  server.stop();
  wifi_init();
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] =>");
  Serial.println((char *)payload);
  
  DynamicJsonBuffer jsonBuffer;
  JsonObject& root = jsonBuffer.parseObject((char *)payload);
  JsonObject& data = root["data"];

  String to = data["to"];
  Serial.print("data[to] : ");
  Serial.println(to);
  if(to != String("thing")) return;
  
  String category = data["category"];
  Serial.print("data[category] : ");
  Serial.println(category);
  if(category != String("control")) return;
  
  String macAddr = data["value"]["macAddr"];
  Serial.print("data[value][macAddr] : ");
  Serial.println(macAddr);
  if(macAddr != mac_addr) return;
  
  unsigned int opCode = data["value"]["opCode"];
  Serial.print("data[opCode] : ");
  Serial.println(opCode);
  if(opCode == OP_CODE_RELAY_ON) digitalWrite(RELAY_PIN, LOW);
  else if(opCode == OP_CODE_RELAY_OFF) digitalWrite(RELAY_PIN, HIGH);  
}

void wifi_init(void) {
  if(strlen(stn_ssid) > 0) {
    unsigned int count = 0;
    Serial.print("SSID : ");
    Serial.print(stn_ssid);
    Serial.print(" PWD : ");
    Serial.println(stn_pwd);
    Serial.print(" WIFI connecting...");
    WiFi.softAPdisconnect();
    WiFi.begin(stn_ssid, stn_pwd);
    while (WiFi.status() != WL_CONNECTED) {
      delay(300);
      Serial.print(".");
    }
    if(count < 20) {
      Serial.println("");
      Serial.println("WiFi connected!");
      Serial.print("IP address: ");
      Serial.println(WiFi.localIP());
      
      Serial.println("");
      Serial.print("Mqtt server host: ");
      Serial.print(MQTT_SERVER);
      Serial.print(" Mqtt server port: ");
      Serial.println(MQTT_SERVER_PORT);
      mqtt_client.setServer(MQTT_SERVER, MQTT_SERVER_PORT);
      mqtt_client.setCallback(callback);
      is_station_mode = true;
      digitalWrite(RELAY_PIN, LOW);
      Serial.println("<<WiFi Station inited!>>");
      return;
    }
  }
  
  Serial.println("");
  Serial.println("AP mode started...");
  WiFi.disconnect();
  WiFi.softAP(AP_SSID, AP_PASSWORD);
  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(myIP);

  server.on("/ap/info", recv_ap_info);
  server.begin();
  is_station_mode = false;
  digitalWrite(RELAY_PIN, HIGH);
  Serial.println("<<WiFi AP inited!>>");
}

void mac_init() {
  uint8_t mac_uint8[6];
  char mac_char[20];
  
  WiFi.macAddress(mac_uint8);
  sprintf(mac_char, "%02X:%02X:%02X:%02X:%02X:%02X", mac_uint8[0], mac_uint8[1], mac_uint8[2], mac_uint8[3], mac_uint8[4], mac_uint8[5]);
  mac_addr = String(mac_char);
  update_status_payload_prefix = "{\"data\":{\"from\":\"thing\",\"to\":\"app\",\"category\":\"updateStatus\",\"value\":{\"macAddr\":\"" + mac_addr + "\",\"status\":\"";
  update_status_payload_suffix = "\"}}}";
  
  Serial.print("WiFi MAC ADDR : ");
  Serial.println(mac_addr);
  Serial.println("<<MAC inited!>>");
}

void timeout_callback() {
  if(!mqtt_client.connected()) return;

  String str = update_status_payload_prefix + digitalRead(RELAY_PIN) + update_status_payload_suffix;
  Serial.print("PAYLOAD : ");
  Serial.println(str);
  const char *payload = str.c_str();
  mqtt_client.publish(mqtt_topic, payload);
}

void timer_init() {
  timer.setInterval(1000, timeout_callback);
}

void mqtt_pub_init() {
    if(WiFi.status() != WL_CONNECTED) wifi_init();
    while (!mqtt_client.connected()) {
        if (mqtt_client.connect("ESP8266Client")) {
            Serial.print("connected! sub mqtt topic : ");
            Serial.println(mqtt_topic);
            mqtt_client.subscribe(mqtt_topic);
        } else {
            Serial.print("failed, rc=");
            Serial.println(mqtt_client.state());
            delay(1000);
        }
    }
}

void setup() {
  Serial.begin(115200);
  Serial.println("Setup Start!");

  ap_init();
  gpio_init();
  mac_init();
  wifi_init();
  timer_init();
}

void loop() {
  timer.run();
  if(!is_station_mode) {
    server.handleClient();
  } else {
    if (!mqtt_client.connected()) {
        mqtt_pub_init();
    }
    mqtt_client.loop();
  }
}
