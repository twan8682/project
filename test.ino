#include <SoftwareSerial.h>        // 블루투스 시리얼 통신 라이브러리 추가
#define BT_RXD 8
#define BT_TXD 7
int trigPin = 6;
int echoPin = 5;
int min = 9999;
int check = 0;
SoftwareSerial bluetooth(BT_RXD, BT_TXD);


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  bluetooth.begin(9600);
  pinMode(echoPin, INPUT);   // echoPin 입력
  pinMode(trigPin, OUTPUT);  // trigPin 출력

}

void loop() {

  long duration, distance, i;
  digitalWrite(trigPin, HIGH);  // trigPin에서 초음파 발생(echoPin도 HIGH)
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH);    // echoPin 이 HIGH를 유지한 시간을 저장 한다.
  distance = ((float)(340 * duration) / 10000) / 2;

  if (distance > 50)
  {
    if (check == 0)
    {
      if (min > 10)
      {
        bluetooth.print("간격좁혀주세요");
        Serial.println("간격좁혀주세요");
      }
      else if (min >= 7)
      {
        bluetooth.print("정상입니다.");
        Serial.println("정상입니다.");
      }
      else if (min >= 1)
      {
        bluetooth.print("넓혀주세요 ");
        Serial.println("넓혀주세요 ");
      }
      else
      {

      }
      min = 9999;
      check = 1;
    }
  }
  else
  {
    if (min > distance)
    {
      min = distance;
    }
    check = 0;
  }
  delay(50);
}
