/**
* author: HE Qichen 
* email: heqichen@gmail.com
* website: http://gaishi.vicp.net
* Date: 2014-4-17
*/

#ifndef __BLUNO_OLED_H__
#define __BLUNO_OLED_H__
#include <Wire.h>
class BlunoOled
{
  private:
    int UG2864Address;
    char ch_buf[17];
    int RES;
    int DC;
    
    void Writec(unsigned char COM);
    void Writed(unsigned char DATA);
    void SSD1306();
    void fill(unsigned char dat);
    void show_string(unsigned char x,unsigned char y,char *s);
    
    
  public:
    BlunoOled(void);
    void oled_init(void);
    void clear(void);
    void print(char *str);
    void print(int data);
    
};

#endif
