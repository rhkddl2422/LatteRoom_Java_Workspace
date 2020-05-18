void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(10,OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available()){
    String s = Serial.readStringUntil('\n');
    int lightPower = s.toInt();
    
    analogWrite(10,lightPower);
    
    }
}
