/**
* author: HE Qichen 
* email: heqichen@gmail.com
* website: http://gaishi.vicp.net
* Date: 2014-4-17
*/

#ifndef __HQC_DHT11_H__
#define __HQC_DHT11_H__

#include <Arduino.h>

class Dht11
{
  private:
    int pin;
    int temperature;
    int humidity;
    
    unsigned long lastUpdateTime;
    
    byte read_dht11_dat();
    bool updateData();
  public:
    Dht11(int p);
    void init();
    int getTemperature();
    int getHumidity();
    
};

#endif
