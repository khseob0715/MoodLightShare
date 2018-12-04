/*
  The library "Adafruit_NeoPixel.h" is download from www.adafruit.com
  You can change the data in the array "color[]" to change the color which changes when songs playing
  and the data in color[] can be find in "WS2812_Definitions.h" which is also download from www.adafruit.com
*/

#include <Adafruit_NeoPixel.h>
#include "WS2812_Definitions.h"
#include <avr/pgmspace.h>
#include <Wire.h>
#include "Arduino.h"
#include "PlainProtocol.h"
#include <Metro.h>
#include <AudioAnalyzer.h>

#define PIN 6             //Arduino Pin connect to the LED Strip 
#define LED_COUNT 180     //set the Amount of LED to 180 and this number depend on how many you really have

PlainProtocol BLUNOPlainProtocol(Serial, 115200); //set Serial baud rate to 115200
Adafruit_NeoPixel leds = Adafruit_NeoPixel(LED_COUNT, PIN, NEO_GRB + NEO_KHZ400);// NEO_GRB means the type of your LED Strip
Metro ledMetro = Metro(18);      // Metro for data receive in a regular time
Analyzer Audio = Analyzer(4, 5, 0); // Strobe->4 RST->5 Analog->0

int State01 = 2;      //  버튼 누르면 바뀌는 것. // 처음에는 사운드 비주얼 라이제이션 효과 기능 // Rocker

//적외선
int inputPin = 7;           // 적외선 센서핀
int val = 0;         // 센서 신호 변수
int pirState = LOW;  // 센서 초기값은 low

int Red = 10, Green = 0, Blue = 10, Number_Position_RGB = 100;
int SeletedTheme = 0;
int Speech_Flag = 0;
int FreqVal[7];//the spectrum value
int color[] = {0xDC143C, 0xFFA500, 0xFFFF00, 0x32CD32, 0x0000FF, 0x2F4F4F, 0x4B0082, 0xDA70D6};

int Num_Channel = 0, Buff_Channel = 0;
int Num_Color[7], Buff_Num_Color[7];
boolean Dis_En = false;
int Num_First_Color = 0, Buf_Max = 0;
int Num_Shark02_High = 0, Number_Shark02_LOW = 0;

void setup()
{
    Audio.Init();  //Init module
    leds.begin();  // Call this to start up the LED strip.
    clearLEDs();   // LED 조명 제거
    leds.show();   // LED 업데이트
  
    pinMode(inputPin, INPUT);  //센서 input
  
    Serial.begin(115200);
  
    TCCR1B &= ~((1 << CS12) | (1 << CS11) | (1 << CS10)); //Clock select: SYSCLK divde 8;
    TCCR1B |= (1 << CS11);
    TCCR2B &= ~((1 << CS12) | (1 << CS11) | (1 << CS10)); //Clock select: SYSCLK divde 8;
    TCCR2B |= (1 << CS11);

    randomSeed(analogRead(A0));
}

void loop()
{

    val = digitalRead(inputPin);

    if (BLUNOPlainProtocol.available())
    {
      if (BLUNOPlainProtocol.receivedCommand == "RGBLED") //get command
      {
        State01 = 3;
        Red  = BLUNOPlainProtocol.receivedContent[0];
        Green = BLUNOPlainProtocol.receivedContent[1];
        Blue = BLUNOPlainProtocol.receivedContent[2];
      } else if (BLUNOPlainProtocol.receivedCommand == "ROCKER") {
        State01 = 2;
      } else if (BLUNOPlainProtocol.receivedCommand == "THEME") {
        State01 = 4;
        SeletedTheme = BLUNOPlainProtocol.receivedContent[0];
      } else if (BLUNOPlainProtocol.receivedCommand == "CUSTOM") {
        State01 = 5;
      } else if (BLUNOPlainProtocol.receivedCommand == "SLEEP") {
        State01 = 6;
      } else if (BLUNOPlainProtocol.receivedCommand == "SPEECH") {
        State01 = 7;
        Speech_Flag = BLUNOPlainProtocol.receivedContent[0];
      }
  
    }
  
    if (ledMetro.check() == 1)//time for metro
    {
      if (State01 == 1) // 전부 지우진 초기 상태
      {
        clearLEDs();  // Turn off all LEDs
        leds.show();
      }
      else if (State01 == 2)  // 사운드 비주얼라이제이션 효과
      {
        Rock_With_Song();   //leds.show();
      }
      else if (State01 == 3)  // 내가 선택한 색상.
      {
        for (int i = 0; i < LED_COUNT; i++)
        {
          if (i % 7 == 0)
            leds.setPixelColor(i, Red, Green, 0); //change the color
          else if (i % 3 == 0)
            leds.setPixelColor(i, 0, Green, Blue); //change the color
          else if (i % 2 == 0)
            leds.setPixelColor(i, Red, Green, Blue); //change the color
          else
            leds.setPixelColor(i, Red, 0, Blue); //change the color
        }
        leds.show();
      }
      else if (State01 == 4) // 테마
      {
        switch (SeletedTheme)
        {
          case 101:
            theme01();
            break;
          case 102:
            break;
          case 103:
            break;
          case 104:
            break;
          case 201:
          smallStar();
            break;
          case 202:
            break;
          case 203:
            break;
          case 204:
            break;
  
        }
      }else if(State01 == 5){
        
      }else if(State01 == 6){  // 잠 잘때
        for(int i = 0 ; i < 25; i++){
          leds.setPixelColor(i, 0, 32, 0); //change the color
        }
        leds.show();
          if (val == HIGH)
          {
              if (pirState == LOW) {
                pirState = HIGH;
              }
          }
          else
          {
              if (pirState == HIGH) {
                pirState = LOW;
              }
          }
      }else if(State01 == 7){
        switch(Speech_Flag){
          case 0:  // 끄기
            clearLEDs();  // Turn off all LEDs
            leds.show();
            break;
          case 1:  // 키기
            for(int i = 0 ; i < 180; i++){
              leds.setPixelColor(i, 0xFFFF9301);
            }
            leds.show();
            break;
          case 2:  // 노래
            Rock_With_Song();   //leds.show();
            break;
          case 3:  // 수면모드
            break;
        }
      }
    }
}
int theme01_color[] = {0xFE4C40, 0xFF3232, 0xFF0000, 0xFFD732, 0xFF607F, 0xFF8C0A, 0xB90000, 0xFF5050};
void theme01(){
  
  for (int i = 0; i < LED_COUNT; i++)
  {
    if (i < LED_COUNT / 7)
    {
      leds.setPixelColor(i, theme01_color[random(8)]);
    }
    else if (i < (LED_COUNT / 7) * 2)   {
      leds.setPixelColor(i, theme01_color[random(8)]);
    }
    else if (i < (LED_COUNT / 7) * 3)   {
      leds.setPixelColor(i, theme01_color[random(8)]);
    }
    else if (i < (LED_COUNT / 7) * 4)   {
     leds.setPixelColor(i, theme01_color[random(8)]);
    }
    else if (i < (LED_COUNT / 7) * 5)   {
      leds.setPixelColor(i, theme01_color[random(8)]);
    }
    else if (i < (LED_COUNT / 7) * 6)   {
      leds.setPixelColor(i, theme01_color[random(8)]);
    }
    else if (i < LED_COUNT)         {
      leds.setPixelColor(i, theme01_color[random(8)]);
    }
  }
  leds.show();
}

void clearLEDs()  // LED 전부 지우기
{
    for (int i = 0; i < LED_COUNT; i++)  leds.setPixelColor(i, 0);
}


void rainbow(byte startPosition)
{
    int rainbowScale =  192 / LED_COUNT;
    leds.setPixelColor( startPosition, rainbowOrder((rainbowScale * ( startPosition + startPosition)) % 192));
    leds.show();
}


uint32_t rainbowOrder(byte position)
{
    if (position < 31)
    {
      return leds.Color(0xFF, position * 8, 0);
    }
    else if (position < 63)
    {
      position -= 31;
      return leds.Color(0xFF - position * 8, 0xFF, 0);
    }
    else if (position < 95)
    {
      position -= 63;
      return leds.Color(0, 0xFF, position * 8);
    }
    else if (position < 127)
    {
      position -= 95;
      return leds.Color(0, 0xFF - position * 8, 0xFF);
    }
    else if (position < 159)
    {
      position -= 127;
      return leds.Color(position * 8, 0, 0xFF);
    }
    else  //160 <position< 191
    {
      position -= 159;
      return leds.Color(0xFF, 0x00, 0xFF - position * 8);
    }
}


void Rock_With_Song()
{
    Buff_Channel = Num_Channel; // Buff_Channnel can store the number of the channel which has the max spectrum value
    Audio.ReadFreq(FreqVal);    // get the spectrum value
  
    for (int i = 0; i < 6; i++)
    {
      if (FreqVal[Num_Channel] < FreqVal[i])  Num_Channel = i; // get the number of the channel which has the max spectrum value
      Buff_Num_Color[i] = Num_Color[i];                    //store the value for the using below
    }
  
    if (FreqVal[Num_Channel] < 400)      {
      Number_Shark02_LOW++;
    } //count if a low voice started
  
    if (Buf_Max != Num_Channel && FreqVal[Num_Channel] > 300) // judge if the sound changed
    {
      Num_First_Color++;
      Dis_En = true;                                        //enable the display
      if (FreqVal[Num_Channel] > 400)
        Number_Shark02_LOW = 0;  //reset the count of low voice
  
      if (Num_First_Color == 7)
        Num_First_Color = 0;
  
      for (int i = 0; i < 7; i++)
      {
        int x = random(0, 6);
        if (i == 0)   Num_Color[i] = Num_First_Color;    //recycle the value
        else       Num_Color[i] = Buff_Num_Color[x];     //change the color randomly
      }
    }
  
    Buf_Max = Num_Channel;
  
    if ( (Buf_Max == 5 || Buf_Max == 4 ) && FreqVal[Buf_Max] > 700) //count when the  High vlaue of the sound started
    {
      Num_Shark02_High++;
    }
    else Num_Shark02_High = 0;                                //reset the count of the High_value_count
  
    if (Num_Shark02_High > 22)                               //time of High value voice reached
    {
      for (int i = 0; i < LED_COUNT / 2; i++)                //these are effects of color changing
      {
        leds.setPixelColor(i, rainbowOrder(i));              //rising from two origin points
        leds.setPixelColor(i + LED_COUNT / 2, rainbowOrder(i));
        leds.show();
        if (i > LED_COUNT / 4) delay(1);
      }
      for (int i = 0; i < LED_COUNT / 2; i++)
      {
        leds.setPixelColor(LED_COUNT - i, rainbowOrder(i));
        leds.setPixelColor(i + LED_COUNT / 2, rainbowOrder(i));
        leds.show();
      }
      for (int i = 0; i < LED_COUNT; i++)
      {
        leds.setPixelColor(i, GOLD);
      }
  
      leds.show();
      Audio.ReadFreq(FreqVal);
  
      if (FreqVal[4] > 800)                                      //if High sound value continues, take another effect out!
      {
        for (int x = 0; x < 6; x++)
        {
          if (FreqVal[x] > 1000)
          {
            for (int y = 0; y < LED_COUNT / 2; y++)    {
              leds.setPixelColor(LED_COUNT - y, RED);
              leds.setPixelColor(LED_COUNT / 2 - y, RED);
              leds.show();
            }
            x = 7;
          }
        }
      }
      Num_Shark02_High = 0;                                //reset the count when effect playing finished
    }
  
  
    if (Number_Shark02_LOW > 40)                          //when the time of low value sound reached
    {
      for (int i = 0; i < LED_COUNT / 2; i++)              //close the light from two point
      {
        leds.setPixelColor(i, 0);
        leds.setPixelColor(LED_COUNT - i, 0);
        leds.show();
      }
      Number_Shark02_LOW = 0;
      Dis_En = false;
    }
  
  
    if (Dis_En == true)
      Display();
}

void Display()
{
  for (int i = 0; i < LED_COUNT; i++)
  {
    if (i < LED_COUNT / 7)
    {
      leds.setPixelColor(i, color[Num_Color[0]]);
    }
    else if (i < (LED_COUNT / 7) * 2)   {
      leds.setPixelColor(i, color[Num_Color[1]]);
    }
    else if (i < (LED_COUNT / 7) * 3)   {
      leds.setPixelColor(i, color[Num_Color[2]]);
    }
    else if (i < (LED_COUNT / 7) * 4)   {
      leds.setPixelColor(i, color[Num_Color[3]]);
    }
    else if (i < (LED_COUNT / 7) * 5)   {
      leds.setPixelColor(i, color[Num_Color[4]]);
    }
    else if (i < (LED_COUNT / 7) * 6)   {
      leds.setPixelColor(i, color[Num_Color[5]]);
    }
    else if (i < LED_COUNT)         {
      leds.setPixelColor(i, color[Num_Color[6]]);
    }
  }
  leds.show();
}

void smallStar(){
  int index = (int)random(50);
  int plus;
  
  for(int i = 0 ; i < 20; i++){
    plus = (int)random(120);
    leds.setPixelColor(index+plus, 0xFFFFFF);
    leds.show();
  }
  
  for(int bri = 10 ; bri < 250; bri+=10){
    leds.setBrightness(bri);
    leds.show();
    delay(100);
  }

  for(int bri = 250 ; bri >= 0; bri -=10){
    leds.setBrightness(bri);
    leds.show();
    delay(100);
  }
  
  leds.show();
}
