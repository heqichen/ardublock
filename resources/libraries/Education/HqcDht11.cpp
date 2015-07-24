/**
* author: HE Qichen 
* email: heqichen@gmail.com
* website: http://gaishi.vicp.net
* Date: 2014-4-17
*/

#include "HqcDht11.h"
#include <Arduino.h>

Dht11::Dht11(int p)
{
  pin = p;
  lastUpdateTime = 0;
}

void Dht11::init()
{
  pinMode((pin), OUTPUT);
  digitalWrite((pin), HIGH);
}

byte Dht11::read_dht11_dat()
{
  byte n = 0;
  byte result=0;
  for(n=0; n< 8; n++)
  {
    while (!digitalRead(pin));
    delayMicroseconds(30);
    if (digitalRead((pin)) != 0 )
      bitSet(result, 7-n);
    while (digitalRead((pin)));
  }
  return result;
}

bool Dht11::updateData()
{
  if (millis() - lastUpdateTime < 200)
  {
  	return true;
  }
  byte dht11_dat[5];
  byte dht11_in;
  byte i;// start condition
  
  digitalWrite(pin, LOW);
  delay(18);
  digitalWrite(pin, HIGH);
  delayMicroseconds(1);
  pinMode(pin, INPUT);
  delayMicroseconds(40);
  
  if (digitalRead(pin))
  {
        return false;
  }
  delayMicroseconds(80);
  if (!digitalRead(pin))
  {
        return false;
  }
  //wait for second response signal:HIGH
  delayMicroseconds(80);// now ready for data reception
  for (i=0; i<5; i++)
  {
    dht11_dat[i] = read_dht11_dat();
  }  //recieved 40 bits data. Details are described in datasheet
  
  pinMode(pin, OUTPUT);
  digitalWrite(pin, HIGH);
  byte dht11_check_sum = dht11_dat[0]+dht11_dat[2];// check check_sum
  if(dht11_dat[4]!= dht11_check_sum)
  {
        return false;
  }
  humidity = dht11_dat[0];
  temperature = dht11_dat[2];
  lastUpdateTime = millis();
  return true;
}

int Dht11::getHumidity()
{
  if (updateData())
  {
    return humidity;
  }
  else
  {
    return -99;
  }
}

int Dht11::getTemperature()
{
  if (updateData())
  {
    return temperature;
  }
  else
  {
    return -99;
  }
}
