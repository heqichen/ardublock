/**
* author: HE Qichen 
* email: heqichen@gmail.com
* website: http://gaishi.vicp.net
* Date: 2014-4-17
*/


#include <Wire.h>
#include <BlunoOled00.h>

BlunoOled oled;


void setup()
{
  oled.oled_init();
}

int i = 0;

void loop()
{
  oled.print("current value: ");
  delay(200);
  oled.print(i);
  delay(500);
  ++i;
}
