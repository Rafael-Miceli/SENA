// ---------------------------------------------------------------------------
// This example code was used to successfully communicate with 15 ultrasonic sensors. You can adjust
// the number of sensors in your project by changing SONAR_NUM and the number of NewPing objects in the
// "sonar" array. You also need to change the pins for each sensor for the NewPing objects. Each sensor
// is pinged at 33ms intervals. So, one cycle of all sensors takes 495ms (33 * 15 = 495ms). The results
// are sent to the "oneSensorCycle" function which currently just displays the distance data. Your project
// would normally process the sensor results in this function (for example, decide if a robot needs to
// turn and call the turn function). Keep in mind this example is event-driven. Your complete sketch needs
// to be written so there's no "delay" commands and the loop() cycles at faster than a 33ms rate. If other
// processes take longer than 33ms, you'll need to increase PING_INTERVAL so it doesn't get behind.
// ---------------------------------------------------------------------------
#include <NewPing.h>
#include <UTFT.h>
UTFT myGLCD(NIC35WS,38,39,40,41); //3.5" TFTLCD for arduino 2560 from mcufriend.com
#include <memorysaver.h>
extern uint8_t SmallFont[];
int dist1,inv,val=0;
int dist2,inv1,val2=0;
int ultima_distancia;


#define SONAR_NUM     2 // Number or sensors.
#define MAX_DISTANCE 400  // Maximum distance (in cm) to ping.
#define PING_INTERVAL 33 // Milliseconds between sensor pings (29ms is about the min to avoid cross-sensor echo).

unsigned long pingTimer[SONAR_NUM]; // Holds the times when the next ping should happen for each sensor.
unsigned int cm[SONAR_NUM];         // Where the ping distances are stored.
uint8_t currentSensor = 0;          // Keeps track of which sensor is active.

NewPing sonar[SONAR_NUM] = {     // Sensor object array.
  NewPing(A0, A1, MAX_DISTANCE), // Each sensor's trigger pin, echo pin, and max distance to ping.
  NewPing(A2, A3, MAX_DISTANCE),
  /*NewPing(45, 20, MAX_DISTANCE),
  NewPing(21, 22, MAX_DISTANCE),
  NewPing(23, 24, MAX_DISTANCE),
  NewPing(25, 26, MAX_DISTANCE),
  NewPing(27, 28, MAX_DISTANCE),
  NewPing(29, 30, MAX_DISTANCE),
  NewPing(31, 32, MAX_DISTANCE),
  NewPing(34, 33, MAX_DISTANCE),
  NewPing(35, 36, MAX_DISTANCE),
  NewPing(37, 38, MAX_DISTANCE),
  NewPing(39, 40, MAX_DISTANCE),
  NewPing(50, 51, MAX_DISTANCE),
  NewPing(52, 53, MAX_DISTANCE)*/
};

#include <UIPEthernet.h> 


// Ethernet shield MAC address (sticker in the back)
byte mac[6] = { 0x54, 0x34, 0x41, 0x30, 0x30, 0x31};
//static uint8_t ip[4] = {192, 168, 1, 15}; 
//static uint16_t port = 80; // Use port 80 - the standard for HTTP                                     

IPAddress ip(192,168,0,55);

//EthernetServer server(80);

// Azure Mobile Service address
// You can find this in your service dashboard
const char *server = "arduinoapp.azure-mobile.net";

// Azure Mobile Service table name
// The name of the table you created
const char *table_name = "ClienteTeste2";

// Azure Mobile Service Application Key
// You can find this key in the 'Manage Keys' menu on the dashboard
const char *ams_key = "QkTMsFHSEaNGuiKVsywYYHpHnIHMUB64";

EthernetClient client;

char buffer[64];

/*
** Send an HTTP POST request to the Azure Mobile Service data API
*/

void send_request(int value)
{
  Serial.println("\nconnecting...");

  if (client.connect(server, 80)) {

    Serial.print("sending ");
    Serial.println(value);

    // POST URI
    sprintf(buffer, "POST /tables/%s HTTP/1.1", table_name);
    client.println(buffer);

    // Host header
    sprintf(buffer, "Host: %s", server);
    client.println(buffer);

    // Azure Mobile Services application key
    sprintf(buffer, "X-ZUMO-APPLICATION: %s", ams_key);
    client.println(buffer);

    // JSON content type
    client.println("Content-Type: application/json");

    // POST body
    sprintf(buffer, "{\"Nivel\": %d}", value);

    // Content length
    client.print("Content-Length: ");
    client.println(strlen(buffer));

    // End of headers
    client.println();

    // Request body
    client.println(buffer);
    
  } else {
    Serial.println("connection failed");
  }
}

/*
** Wait for response
*/

void wait_response()
{
  while (!client.available()) {
    if (!client.connected()) {
      return;
    }
  }
}

/*
** Read the response and dump to serial
*/

void read_response()
{
  bool print = true;
  
  while (client.available()) {
    char c = client.read();
    // Print only until the first carriage return
    if (c == '\n')
      print = false;
    if (print)
      Serial.print(c);
  }
}

/*
** Close the connection
*/

void end_request()
{
  client.stop();
}



void setup() {
  myGLCD.InitLCD();
  myGLCD.setFont(SmallFont);

  myGLCD.setColor(64, 64, 64);
  myGLCD.fillRect(0, 226, 399, 239);
  myGLCD.setColor(255, 255, 255);
  //myGLCD.setBackColor(255, 0, 0);
  myGLCD.print("*** CAIXA 1 ", LEFT, 1);
  myGLCD.print("*** CAIXA 2 ", RIGHT, 1);
  myGLCD.setBackColor(64, 64, 64);
  myGLCD.setColor(255,255,0);
  myGLCD.print("<SISTERNA 1 >", LEFT, 227);
  myGLCD.print("<SISTERNA 2 >", RIGHT, 227);

  myGLCD.setColor(0, 0, 255);
  myGLCD.drawRect(0, 14, 399, 225); // RETANGULO EXTERNO DO LCD

  myGLCD.setColor(0, 0, 255);
  myGLCD.setBackColor(0, 0, 0);
 myGLCD.drawLine(199, 15, 199, 224);// LINHA VERTIVAL DO MEIO LCD
 myGLCD.drawLine(1, 119, 398, 119); // LINHA HORIZONTAL DO MEIO DO lcd
 for (int i=9; i<390; i+=10)
   myGLCD.drawLine(i, 117, i, 121);
 for (int i=19; i<220; i+=10)
  myGLCD.drawLine(197, i, 201, i);

     pingTimer[0] = millis() + 75;           // First ping starts at 75ms, gives time for the Arduino to chill before starting.
  for (uint8_t i = 1; i < SONAR_NUM; i++) // Set the starting time for each sensor.
    pingTimer[i] = pingTimer[i - 1] + PING_INTERVAL;


  delay(200);
    //millis();
  
  Ethernet.begin(mac, ip);
//  server.begin();

  /* DIO pin used for the CS function. Note that even if you are not driving this
     function from your Arduino board, you must still configure this as an output 
     otherwise the SD library functions will not work. */
     pinMode(10, OUTPUT);
     Serial.begin(9600);


     Serial.println("ethernet");

     if (Ethernet.begin(mac) == 0) {
      Serial.println("ethernet failed");
    //for (;;) ;
    }
  // give the Ethernet shield a second to initialize:
    delay(1000);
 //millis();

    ultima_distancia = -1; 
  }

  void loop() {
  for (uint8_t i = 0; i < SONAR_NUM; i++) { // Loop through all the sensors.
    if (millis() >= pingTimer[i]) {         // Is it this sensor's time to ping?
      pingTimer[i] += PING_INTERVAL * SONAR_NUM;  // Set next time this sensor will be pinged.
      if (i == 0 && currentSensor == SONAR_NUM - 1) oneSensorCycle(); // Sensor ping cycle complete, do something with the results.
      sonar[currentSensor].timer_stop();          // Make sure previous timer is canceled before starting a new ping (insurance).
      currentSensor = i;                          // Sensor being accessed.
      cm[currentSensor] = 0;                      // Make distance zero in case there's no ping echo for this sensor.
      sonar[currentSensor].ping_timer(echoCheck); // Do the ping (processing continues, interrupt will call echoCheck to look for echo).
      distance1();
      distance2();
 /* send_request(val);
  wait_response();
  read_response();
  end_request();*/

 // delay(1000);
  
}
}
  // The rest of your code would go here.
}

void echoCheck() { // If ping received, set the sensor distance to array.
  if (sonar[currentSensor].check_timer())
    cm[currentSensor] = sonar[currentSensor].ping_result / US_ROUNDTRIP_CM;
}

void oneSensorCycle() { // Sensor ping cycle complete, do something with the results.
  for (uint8_t i = 0; i < SONAR_NUM; i++) {
    Serial.print(i);
    Serial.print("=");
    Serial.print(cm[i]);
    Serial.print("cm ");
  }
  
}

void distance1(){
  grid1();
  dist1=cm[0];    
}

void grid1(){


  if(dist1<=15){

    myGLCD.setColor(VGA_AQUA);
    myGLCD.fillRect(20,120,120,130);
  }

  else {
    myGLCD.setColor(VGA_WHITE);
    myGLCD.fillRect(20,120,120,130);

  }

  if (dist1<=30){
    myGLCD.setColor(VGA_AQUA);
    myGLCD.fillRect(20,132,120,142);

  }else{
    myGLCD.setColor(VGA_WHITE);
    myGLCD.fillRect(20,132,120,142);
    
  }
  if(dist1<=45){
   myGLCD.setColor(VGA_AQUA);
   myGLCD.fillRect(20,144,120,154);
 }else{
   myGLCD.setColor(VGA_WHITE);
   myGLCD.fillRect(20,144,120,154);
 }  
 if(dist1<=60){
   myGLCD.setColor(VGA_AQUA);
   myGLCD.fillRect(20,156,120,166);
 }else{
   myGLCD.setColor(VGA_WHITE);
   myGLCD.fillRect(20,156,120,166);
 }
 if(dist1<=75){
  myGLCD.setColor(VGA_AQUA);
  myGLCD.fillRect(20,168,120,178);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,168,120,178);
}
if(dist1<=90){
  myGLCD.setColor(VGA_AQUA);
  myGLCD.fillRect(20,180,120,190);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,180,120,190);
}
if(dist1<=105){
  myGLCD.setColor(VGA_AQUA);
  myGLCD.fillRect(20,192,120,202);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,192,120,202);
}
if(dist1<=120){
  myGLCD.setColor(VGA_YELLOW);
  myGLCD.fillRect(20,204,120,214);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,204,120,214);
}
if(dist1<=135){
  myGLCD.setColor(VGA_RED);
  myGLCD.fillRect(20,216,120,224);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,216,120,224);
}
}

void distance2(){
  grid2();
  dist2=cm[1];

}

void grid2(){


  if(dist2<=15){

    myGLCD.setColor(VGA_AQUA);
    myGLCD.fillRect(20,15,120,24);
  }

  else {
    myGLCD.setColor(VGA_WHITE);
    myGLCD.fillRect(20,15,120,24);

  }

  if (dist2<=30){
    myGLCD.setColor(VGA_AQUA);
    myGLCD.fillRect(20,26,120,35);

  }else{
    myGLCD.setColor(VGA_WHITE);
    myGLCD.fillRect(20,26,120,35);
    
  }
  if(dist2<=45){
   myGLCD.setColor(VGA_AQUA);
   myGLCD.fillRect(20,37,120,46);
 }else{
   myGLCD.setColor(VGA_WHITE);
   myGLCD.fillRect(20,37,120,46);
 }  
 if(dist2<=60){
   myGLCD.setColor(VGA_AQUA);
   myGLCD.fillRect(20,48,120,57);
 }else{
   myGLCD.setColor(VGA_WHITE);
   myGLCD.fillRect(20,48,120,57);
 }
 if(dist2<=75){
  myGLCD.setColor(VGA_AQUA);
  myGLCD.fillRect(20,59,120,68);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,59,120,68);
}
if(dist2<=90){
  myGLCD.setColor(VGA_AQUA);
  myGLCD.fillRect(20,70,120,79);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,70,120,79);
}
if(dist2<=105){
  myGLCD.setColor(VGA_AQUA);
  myGLCD.fillRect(20,81,120,90);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,81,120,90);
}
if(dist2<=120){
  myGLCD.setColor(VGA_YELLOW);
  myGLCD.fillRect(20,92,120,101);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,92,120,101);
}
if(dist2<=135){
  myGLCD.setColor(VGA_RED);
  myGLCD.fillRect(20,103,120,113);
}else{
  myGLCD.setColor(VGA_WHITE);
  myGLCD.fillRect(20,103,120,113);
}



myGLCD.printNumI(cm[1],170,14,3);



    myGLCD.printNumI(cm[0],170,119,3);//(sonar[1].ping_result / US_ROUNDTRIP_CM,90,100,20);
    


 if (dist1 != ultima_distancia)
 // { 
    
  //  ultima_distancia = dist1;
//if(dist1==20){



    send_request(dist1);
    wait_response();
    read_response();
    end_request();      		
 // }
  //else{
 // client.stop();
//}
// if (read_response == dist1){
// client.stop();
 //}

// char c = client.read();

    




    Serial.println();
  }

