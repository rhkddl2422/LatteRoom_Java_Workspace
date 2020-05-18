#include <Servo.h> 
 
int servoPin = 9;
float temperature;  
int reading;  
int lm35Pin = A0;
Servo servo;
int preDegree=0;
void setup()  
{
    analogReference(INTERNAL);
    Serial.begin(9600);
    pinMode(13,OUTPUT);
    servo.attach(servoPin);
    servo.write(preDegree);
}

void loop()  
{
//    reading = analogRead(lm35Pin);
//    temperature = reading / 9.31;
//    
//    Serial.println(temperature);
//    delay(1000);

//    
if( Serial.available() ) {
String s = Serial.readStringUntil('\n');

int degree = s.toInt();


    if(degree>=90){
          servo.write(degree);
          }else{
          
    if(preDegree>degree){
      for(int i=preDegree; i>degree; i--){
        servo.write(i);
        delay(20);
        }
      }else if(degree>preDegree){
        for(int i=preDegree; i<degree; i++){
          servo.write(i);
          delay(20);
          }
        }
        preDegree=degree;
    }}
    delay(3000);
    for(int i=preDegree; i>=0; i--){
        servo.write(i);
        delay(20);
        }
    preDegree=0;
}
