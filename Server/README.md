### Cloud Code Setup
Use this [setup](https://www.parse.com/apps/quickstart#cloud_code/unix) for installing parse cloud code in your system.
### Server Data Config
After Installing the setup configure the data bases in Parse:
* Data Table - _Installation -> Add an extra Column "DeviceID" - type -> string<br>
* Data Table - Device -> Add columns:<br>
  * DeviceID - type(String)
  * GeoLocation - type(GeoPoint)
  * WaterStatus - type(BOOLEAN)
  * LightStatus - type(BOOLEAN)
  * IlluminatedTime- type(Date)
  * WateredTime - type(Date)
  * WeatherStatus - type(String)
  * WeatherDesc - type(String)
  * Humidity - type(number)
  * Temperature - type(number)
  * Pressure - type(number)

* Data Table - PollQueue -> Add columns:<br>
  * DeviceID - type(String)
  * WaterStatus - type(BOOLEAN)
  * LightStatus - type(BOOLEAN)
  * OverrideTime- type(Date)
