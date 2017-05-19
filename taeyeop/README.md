
# Design

+ Hardware
  + [Sonoff](http://sonoff.itead.cc/en/)
    + ESP8266
    + Relay
  + [FTDI](https://randomnerdtutorials.com/how-to-flash-a-custom-firmware-to-sonoff/)
  
+ Software Environment
  + Arduino IDE
  
+ IoT protocol
  + [ESP8266 MQTT client](http://www.hardcopyworld.com/ngine/aduino/index.php/archives/2804)
  + Arduino "PubSubClient" library

+ Function
  + Power on/off
  + Timing schedule

# Environment

+ ESP8266 Library
  + https://github.com/esp8266/Arduino
  + follow "Installing with Boards Manager"
  
+ Additional Library
  + pubsubclient : mqtt client
    + Need to config MQTT_MAX_PACKET_SIZE in document/arduino/libraries/pubsubclient/src/pubsubclient.h
  + arduinoJson : json parsing
  + How to Add Library : Sketch -> Include Library -> Manage Library

+ Internal Library
  + EEPROM : flash data load/store
