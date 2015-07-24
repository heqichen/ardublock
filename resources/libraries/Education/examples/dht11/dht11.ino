/**
* author: HE Qichen 
* email: heqichen@gmail.com
* website: http://gaishi.vicp.net
* Date: 2014-4-17
*/


#include <Wire.h>
#include <BlunoOled00.h>
#include <HqcDht11.h>
BlunoOled oled;



Dht11 dht11(2);

void setup()
{
  dht11.init();
  oled.oled_init();
}

void loop()
{
  oled.print("humi: ");
  delay(100);
  oled.print(dht11.getHumidity());
  delay(300);
  oled.print("temp: ");
  delay(100);
  oled.print(dht11.getTemperature());
  delay(300);
  
  
  
}
