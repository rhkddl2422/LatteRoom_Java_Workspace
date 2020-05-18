float temperature;  
int reading;  
int lm35Pin = A0;

void setup()  
{
    analogReference(INTERNAL);
    Serial.begin(9600);
    pinMode(13,OUTPUT);
    pinMode(12,OUTPUT);
}

void loop() {
  delay(3000);
  reading = analogRead(lm35Pin);
  temperature = reading / 9.31;
  
  Serial.println(temperature);
  
  if( Serial.available() ) {
      String s = Serial.readStringUntil('\n');
      s = s.substring(0,6);
      Serial.println(s);
      if(s=="COOLON"){
        digitalWrite(13,HIGH);
      } 
      else if(s=="COOLOF") {
        digitalWrite(13,LOW);
      }
      
      if( s=="HEATON"){
        digitalWrite(12,HIGH);
      } 
      else if(s=="HEATOF"){
        digitalWrite(12,LOW);
      }
  }
}
