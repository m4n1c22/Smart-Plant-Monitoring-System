/** Cloud Functions...*/

/** Registration Functions...*/
/**
    Function Type   : Cloud Function
    Function Name   : extDeviceRegister
    Author          : Sreeram Sadasivam
    Target Function : Android/iOS/Windows
    Description     : API service provided by Parse using Cloud functions. It is used to register an external device
                      which can be an Android or iOS mobile device along with the device id of plant monitoring hardware.
    Inputs          : DeviceID    - Plant Monitoring hardware device id
                      ExtDeviceId - Android or iOS Device ID.
                      Inputs are passed as JSON string in the format:
                      Example: {"DeviceID":<deviceid>,"ExtDeviceID":<extdeviceid>}
    Outputs         : Either successful message when registration is success or "API failed". This message will be send as a JSON response.
*/
Parse.Cloud.define("extDeviceRegister", function(request, response) {
   
  Parse.Cloud.useMasterKey(); 
  var query = new Parse.Query(Parse.Installation);
  if((request.params.DeviceID == null)||(request.params.DeviceID.length == 0)) {
    response.error("Registration failed");
  }
  else if((request.params.ExtDeviceID == null)||request.params.ExtDeviceID.length == 0) {
    response.error("Registration failed");
  }
  else {
    query.equalTo("deviceToken", request.params.ExtDeviceID);
    query.first({
        success: function(results) {
       
        if(results == null) {
            var Device = Parse.Object.extend("Device");
            var device = new Device();
            var ExtDevice = Parse.Object.extend(Parse.Installation);
            var extDevice = new ExtDevice();
            var devQuery = new Parse.Query("Device");
            devQuery.equalTo("DeviceID", request.params.DeviceID);
 
            devQuery.find({
 
                success: function(devices) {
                 
                if(devices.length == 0) {
                    response.success(devices);
                }
                else {
                    extDevice.set("DeviceID", request.params.DeviceID);
                    extDevice.set("deviceToken", request.params.ExtDeviceID);
                    extDevice.save(null, {
                        success: function(extDevice) {
                        // Execute any logic that should take place after the object is saved.
                        response.success("Registration successful..."); 
                        },
                        error: function(extDevice, error) {
                        // Execute any logic that should take place if the save fails.
                        // error is a Parse.Error with an error code and message.
                        response.success('Failed to create new object, with error code: ' + error.message);
                        }
                    });         
                }   
                 
            },
                error: function(error) {
     
                }
            });
        }
        else {
 
            var Device = Parse.Object.extend("Device");
            var device = new Device();
            var ExtDevice = Parse.Installation;
            var extDevice = new ExtDevice();
            var devQuery = new Parse.Query("Device");
            devQuery.equalTo("DeviceID", request.params.DeviceID);
 
            devQuery.find({
 
                success: function(devices) {
                 
                if(devices.length == 0) {
                    response.success(devices);
                }
                else {

                    var installationObj = results;                   
                    installationObj.set("DeviceID", request.params.DeviceID);
                    installationObj.save(null, {
                        success: function(extDevice) {
                        // Execute any logic that should take place after the object is saved.
                          response.success("Registration Modified successfully...");  
                        },
                        error: function(extDevice, error) {
                        // Execute any logic that should take place if the save fails.
                        // error is a Parse.Error with an error code and message.
                        response.success('Failed to create new object, with error code: ' + error.message);
                        }
                    });         
                }   
                 
            },
                error: function(error) {
     
                }
            });
        }     
        },
        error: function() {
            //response.success(query);
            response.error("Registration failed...");
        }
    });
  }
});
 
/**
    Function Type   : Cloud Function
    Function Name   : register
    Author          : Sreeram Sadasivam
    Target Function : Gadgeteer
    Description     : API service provided by Parse using Cloud functions. It is used to register the plant monitoring hardware
                      which can be a .net gadgeteer or arduino or even a embedded C based device. We pass the device id and geo location
                      of the hardware device to server API service.
    Inputs          : DeviceID    - Plant Monitoring hardware device id
                      GeoLocation - Hardware GeoLocation passed as latitude and longitude
                      Inputs are passed as JSON string in the format:
                      Example: {"DeviceID":<deviceid>,"GeoLocation":{"latitude":<latitude>,"longitude":<longitude>}}
    Outputs         : Either successful message when registration is success or "API failed". This message will be send as a JSON response.                   
                      If an existing DeviceID is passed and different geolocation is passed the entry for that deviceid in the database will
                      be modified with new geolocation provided by the provider.
*/
Parse.Cloud.define("register", function(request, response) {
  var query = new Parse.Query("Device");
  if((request.params.DeviceID == null)||request.params.DeviceID.length == 0) {
    response.error("Registration failed");
  }
  else if((request.params.GeoLocation == null)||request.params.GeoLocation.length == 0) {
    response.error("Registration failed");
  }
  else {
    query.equalTo("DeviceID", request.params.DeviceID);
    query.find({
        success: function(results) {
       
        if(results.length==0) {
            var Device = Parse.Object.extend("Device");
            var device = new Device();
            var geoPoint = new Parse.GeoPoint(request.params.GeoLocation.latitude,request.params.GeoLocation.longitude);
            device.set("DeviceID", request.params.DeviceID);
            device.set("GeoLocation", geoPoint);
            device.set("LightStatus",false);
            device.set("WaterStatus",false);
            device.save(null, {
            success: function(device) {
            // Execute any logic that should take place after the object is saved.

                Parse.Cloud.run('weatherPredictionInternalMethod', {}, {
                  success: function(results) {
                    
                  },
                  error: function(error) {
                    response.error("API Failed...");
                  }
                });

                response.success("Registration successful..."); 
            },
            error: function(device, error) {
            // Execute any logic that should take place if the save fails.
            // error is a Parse.Error with an error code and message.
                response.success('Failed to create new object, with error code: ' + error.message);
            }
            });         
        }
        else {
 
            query.first({
                success: function(results) {
       
                    var device = results;
                    var geoPoint = new Parse.GeoPoint(request.params.GeoLocation.latitude,request.params.GeoLocation.longitude);
                    device.set("GeoLocation", geoPoint);
                    device.save(null, {
                    success: function(device) {
                        // Execute any logic that should take place after the object is saved.

                        Parse.Cloud.run('weatherPredictionInternalMethod', {}, {
                          success: function(results) {
                    
                          },
                          error: function(error) {
                            response.error("API Failed...");
                          }
                        });
                        response.success("Registration Modified successfully...");  
                    },
                    error: function(device, error) {
                    // Execute any logic that should take place if the save fails.
                    // error is a Parse.Error with an error code and message.
                        response.error('Failed to create new object, with error code: ' + error.message);
                    }
    
                    });              
                },
                error: function() {
                    response.error("API Failed...");
                }
            });
        }     
        },
        error: function() {
            response.error("Registration failed");
        }
    });
  }
});

/** Prediction Functions...*/ 
/**
    Function Type   : Cloud Function
    Function Name   : weatherStatusForDevice
    Author          : Sreeram Sadasivam
    Target Function : Android/iOS/Windows,Gadgeteer Device
    Description     : API service provided by Parse using Cloud functions. It is used to provide the external device or the gadgeteer device
                      with the weather status for its registered hardware device (incase of external device) and knowing the weather
                      status of the location in which the gadgeteer is placed (incase of the gadgeteer device). This API is invoked 
                      by both the external device and gadgeteer device. It is accessed by the external device for providing the user, the choice
                      to overriding the default setup. Whereas in case of gadgeteer, it is provided to perform machine curation thereby the
                      gadgeteer can trigger the control of plants by watering and lighting based on the curation provided by it.
    Inputs          : ExtDeviceID - Android or iOS Device ID. (If invoked by Android /iOS device)
                      DeviceID - Gadgeteer Device ID. (If invoked by gadgeteer)
                      Inputs are passed as JSON string in the format:
                      Example: {"ExtDeviceID":<extdeviceid>}                    
    Outputs         : Possible outputs are API failed or weather report for the registered hardware by the external device(if invoked by
                      external device). The same in case if invoked by the gadgeteer device.
                      Outputs are returned as JSON string in the format:
                      Example: {"ExtDeviceID":<extdeviceid>,"DeviceID":<deviceid>,"WeatherStatus":<mainweather>,"WeatherDesc":<weather description>,"Temperature":<temperature>,"Pressure":<pressure>,"Humidity":<humidity}
                      Note: Same response string without ExtDeviceID incase of API invoking from the gadgeteer device.
*/
Parse.Cloud.define("weatherStatusForDevice", function(request, response) {
   
   Parse.Cloud.useMasterKey(); 
    var devQuery = new Parse.Query("Device");
    var query = new Parse.Query(Parse.Installation);
    if(((request.params.ExtDeviceID == null)||request.params.ExtDeviceID.length == 0)&&((request.params.DeviceID == null)||request.params.DeviceID.length == 0)) {
        response.error("API failed");
    }
    else if((request.params.ExtDeviceID == null)||request.params.ExtDeviceID.length == 0){

      devQuery.equalTo("DeviceID", request.params.DeviceID);
      devQuery.first({
          success: function(device) {
       
            var object = device;
            if(device) {
              var predictionJSON = {"DeviceID":request.params.DeviceID,"WeatherStatus":object.get("WeatherStatus"),"WeatherDesc":object.get("WeatherDesc"),"Temperature":object.get("Temperature"),"Pressure":object.get("Pressure"),"Humidity":object.get("Humidity")};
              JSON.stringify(predictionJSON);
              response.success(predictionJSON);
            }
            else {
              response.error("DeviceID doesn't exists...");
            }
                      
          },
          error: function() {
              response.error("API Failed...");
        }
      });

    }
    else if((request.params.DeviceID == null)||request.params.DeviceID.length == 0){
        query.equalTo("deviceToken", request.params.ExtDeviceID);
        query.find({
            success: function(results) {
       
                if(results.length==0) {
                    response.error("ExtDeviceID missing...");
                }
                else {
                    var object = results;
                    devQuery.equalTo("DeviceID", results[0].get("DeviceID"));
                    devQuery.first({
                        success: function(device) {
       
                            var object = device;
                            var predictionJSON = {"ExtDeviceID":request.params.ExtDeviceID,"DeviceID":object.get("DeviceID"),"WeatherStatus":object.get("WeatherStatus"),"WeatherDesc":object.get("WeatherDesc"),"Temperature":object.get("Temperature"),"Pressure":object.get("Pressure"),"Humidity":object.get("Humidity")};
                            JSON.stringify(predictionJSON);
                            response.success(predictionJSON);
                      
                        },
                        error: function() {
                            response.error("API Failed...");
                        }
                    });
                }        
            },
            error: function() {
                response.error("API Failed...");
            }
        });
    }  
    else {
      response.error("Invalid Parameters to API...");
    }
});



/**
    Function Type   : Cloud Function
    Function Name   : weatherForecastForDevice
    Author          : Sreeram Sadasivam
    Target Function : Gadgeteer Device and Android/iOS/Windows
    Description     : API service provided by Parse using Cloud functions. This API responds differently based on the requestor.
                      If the requestor is an Android/iOS/Windows mobile device then,
                      the API forecasts the weather for the next 24hrs of the location of plant monitoring device to which the mobile
                      device is subscribed to.
                      If it is the gadgeteer, the API provides whether it will rain in the next 3hr timeslot based on the devicetime provided 
                      by the gadgeteer. It will return boolean value for rain to the gadgeteer device based on the condition. 
    Inputs          : If gadgeteer,
                      DeviceID - Gadgeteer Device ID.
                      DeviceTime - Gadgeteer Device Time in seconds from the Epoch(1 Jan 1970 00:00hrs)
                      Inputs are passed as JSON string in the format:
                      Example: {"DeviceID":<deviceid>,"DeviceTime":<devicetime>}                    
                      If Android/iOS/Windows,
                      ExtDeviceID - Device token of the device
                      Example - {"ExtDeviceID":<devicetoken>}                      
    Outputs         : Possible outputs are API failed or DeviceID doesn't exist or weather forecast for the gadgeteer device in two different 
                      ways as explained above.
                      Outputs are returned as JSON string in the format:
                      If Gadgeteer,
                      {"DeviceID":<deviceid>,"Rain":true}
                      If Android/iOS/Windows,
                      {"ExtDeviceID":<devicetoken,"Weather":"Clear Sky"}
*/
Parse.Cloud.define("weatherForecastForDevice", function(request, response) {
   
    Parse.Cloud.useMasterKey(); 
    var devQuery = new Parse.Query("Device");
    var query = new Parse.Query(Parse.Installation);
  
    if(((request.params.DeviceID == null)||(request.params.DeviceID.length == 0)||(request.params.DeviceTime == null)||(request.params.DeviceTime.length == 0))&&((request.params.ExtDeviceID == null)||request.params.ExtDeviceID.length == 0)) {
        response.error("API failed");
    }
    else if((request.params.ExtDeviceID == null)||request.params.ExtDeviceID.length == 0) {

      devQuery.equalTo("DeviceID", request.params.DeviceID);
      devQuery.first({
          success: function(device) {
       
            var object = device;
            if(device) {

                var geoPoint = new Parse.GeoPoint(device.get("GeoLocation"));
                
                Parse.Cloud.httpRequest({

                  url: 'http://api.openweathermap.org/data/2.5/forecast',
                  params: {
                        lat : geoPoint.latitude,
                        lon : geoPoint.longitude 
                    },
                  success: function (httpResponse) {

                    console.log(httpResponse.text);
                    var timeDiff = request.params.DeviceTime-httpResponse.data.list[0].dt;
                    if(timeDiff<0) {
                      response.success("Use existing forecast data.");  
                    }
                    else {
                      var timeSlot = timeDiff/10800;
                      if(timeSlot>=8) {
                        response.error("Invalid Device Time...");
                      }
                      else {                      
                        var predictionIndex = Math.floor(timeSlot);
                        var isRain = false;
                        
                        if(httpResponse.data.list[predictionIndex].weather[0].main === "Rain") {
                          isRain = true;
                        }
                        else {
                          isRain = false;
                        }

                        var predictionJSON = {"DeviceID":request.params.DeviceID,"Rain":isRain}; 
                        //var predictionJSON = {"DeviceID":request.params.DeviceID,"Forecast":httpResponse.data.list.slice(0,8)};
                        JSON.stringify(predictionJSON);
                        response.success(predictionJSON);
                      }
                    }

                  },
                  error:function (httpResponse) {
                    console.error('Request failed with response code ' + httpResponse.status);
                    response.error(httpResponse.status);
                  }
                });

              
            }
            else {
              response.error("DeviceID doesn't exists...");
            }
                      
          },
          error: function() {
              response.error("API Failed...");
        }
      });

    }
    else if((request.params.DeviceID == null)||(request.params.DeviceID.length == 0)||(request.params.DeviceTime == null)||(request.params.DeviceTime.length == 0)) {
      


        query.equalTo("deviceToken", request.params.ExtDeviceID);
        query.find({
            success: function(results) {
       
                if(results.length==0) {
                    response.error("ExtDeviceID missing...");
                }
                else {
                    var object = results[0];
                    devQuery.equalTo("DeviceID", object.get("DeviceID"));
                    devQuery.first({
                          success: function(device) {
       
                            var object = device;
                            if(device) {

                            var geoPoint = new Parse.GeoPoint(device.get("GeoLocation"));
                
                            Parse.Cloud.httpRequest({

                              url: 'http://api.openweathermap.org/data/2.5/forecast',
                              params: {
                                  lat : geoPoint.latitude,
                                  lon : geoPoint.longitude 
                              },
                              success: function (httpResponse) {

                                console.log(httpResponse.text);
                                var rain=0,clear=0,cloud=0,weatherPrediction;
                                for(var i=0;i<8;i++) {
                                  if(httpResponse.data.list[i].weather[0].main === "Rain") {
                                    rain++;          
                                  }
                                  else if(httpResponse.data.list[i].weather[0].main === "Clear") {
                                    clear++;
                                  }
                                  else {
                                    cloud++;                                        
                                  }
                                }
                                if(rain===0) {
                                    if(cloud/8 >= 0.8) {
                                      weatherPrediction = "Mostly Cloudy";
                                    }    
                                    else if(cloud/8 >= 0.2) {
                                      weatherPrediction = "Partly Cloudy";
                                    }
                                    else {
                                      weatherPrediction = "Clear Sky";
                                    }
                                }
                                else {
                                    if(rain/8 >= 0.8) {
                                      weatherPrediction = "Very High Rain";
                                    }    
                                    else if(rain/8 >= 0.2) {
                                      weatherPrediction = "Light Rain";
                                    }
                                    else {
                                      weatherPrediction = "Overcast with Occasional Rain";
                                    } 
                                }
                                var predictionJSON = {"ExtDeviceID":request.params.ExtDeviceID,"Weather":weatherPrediction}; 
                                //var predictionJSON = {"DeviceID":request.params.DeviceID,"Forecast":httpResponse.data.list.slice(0,8)};
                                JSON.stringify(predictionJSON);
                                response.success(predictionJSON);
                              },
                              error:function (httpResponse) {
                                console.error('Request failed with response code ' + httpResponse.status);
                                response.error(httpResponse.status);
                              }
                            });
                          }
                          else {
                              response.error("DeviceID doesn't exists...");
                          }
                      
                        },
                        error: function() {
                          response.error("API Failed...");
                       }
                  });
  
                    
                }        
            },
            error: function() {
                response.error("API Failed...");
            }
        });



    }


});

/**
    Function Type   : Cloud Function
    Function Name   : weatherPredictionInternalMethod
    Author          : Sreeram Sadasivam
    Target Function : Server
    Description     : Weather Prediction Internal Method service provided for Parse Cloud Jobs. It is used to update the weather conditions of all registered
                      hardware with its geo location by invoking an external API service. This method is invoked internally from the cloud job.
                       
    Inputs          : {}
    Outputs         : It modifies the server with new set of weather prediction systems.
*/
Parse.Cloud.define("weatherPredictionInternalMethod", function(request, response) {
   
  var counter = 0;
  // Query for all users
  var query = new Parse.Query("Device");
  query.each(function(device) {
 
        var geoPoint = new Parse.GeoPoint(device.get("GeoLocation"));
         
        //console.log(geoPoint);
        Parse.Cloud.httpRequest({
            url: 'http://api.openweathermap.org/data/2.5/weather',
            params: {
                        lat : geoPoint.latitude,
                        lon : geoPoint.longitude 
                    }
            }).then(function(httpResponse) {
                         
                    var jsonRespObj = httpResponse.data;
                    //status.message(jsonRespObj.weather[0]);
                    console.log(jsonRespObj);
 
                    //response.success(jsonRespObj.weather[0].main);
                    device.set("WeatherStatus",jsonRespObj.weather[0].main);
                    device.set("WeatherDesc",jsonRespObj.weather[0].description);
                    device.set("Temperature",jsonRespObj.main.temp);
                    device.set("Pressure",jsonRespObj.main.pressure);
                    device.set("Humidity",jsonRespObj.main.humidity);
                    device.save(null, {
                          success: function(device) {
                          // Execute any logic that should take place after the object is saved.

                          },
                          error: function(device, error) {
                          // Execute any logic that should take place if the save fails.
                          // error is a Parse.Error with an error code and message.
                        
                          }
    
                   });
                    //response.success(httpResponse.data);
                    }, function(httpResponse) {
                 
        });
 
 
      if (counter % 100 === 0) {
        // Set the  job's progress status
        //response.success(counter + " devices processed.");
      }
      counter += 1;
      return device.save();
      //response.success(device.get("WeatherStatus"));
  }).then(function() {
    // Set the job's success status
    //response.success("weatherPredictionJob completed successfully.");
  }, function(error) {
    // Set the job's error status
    response.error("API Failed...");
  });
});
 

/** Device Dependent Functions...*/ 
/**
    Function Type   : Cloud Function
    Function Name   : deviceStatus
    Author          : Sreeram Sadasivam
    Target Function : Android/iOS/Windows
    Description     : API service provided by Parse using Cloud functions. It is used to provide the external device
                      with the device status of its registered hardware device.
    Inputs          : ExtDeviceId - Android or iOS or Windows Device ID.
                      Inputs are passed as JSON string in the format:
                      Example: {"ExtDeviceID":<extdeviceid>}                    
    Outputs         : Possible outputs are API failed or device status of the registered hardware by the external device.
                      Outputs are returned as JSON string in the format:
                      Example: {"ExtDeviceID":<extdeviceid>,"DeviceID":<deviceid>,"LightStatus":true/false,"WaterStatus":true/false,"IlluminatedTime":<date&timeoflastilluminated>,"WateredTime":<date&timeoflastwatered>}
*/
Parse.Cloud.define("deviceStatus", function(request, response) {
   
   Parse.Cloud.useMasterKey(); 
    var devQuery = new Parse.Query("Device");
    var query = new Parse.Query(Parse.Installation);
    if((request.params.ExtDeviceID == null)||request.params.ExtDeviceID.length == 0) {
        response.error("API failed");
    }
    else {
        query.equalTo("deviceToken", request.params.ExtDeviceID);
        query.find({
            success: function(results) {
       
                if(results.length==0) {
                    response.error("ExtDeviceID missing...");
                }
                else {
                    var object = results;
                    devQuery.equalTo("DeviceID", results[0].get("DeviceID"));
                    devQuery.first({
                        success: function(device) {
       
                            var object = device;
                            var predictionJSON = {"ExtDeviceID":request.params.ExtDeviceID,"DeviceID":object.get("DeviceID"),"LightStatus":object.get("LightStatus"),"WaterStatus":object.get("WaterStatus"),"IlluminatedTime":object.get("IlluminatedTime"),"WateredTime":object.get("WateredTime")};
                            JSON.stringify(predictionJSON);
                            response.success(predictionJSON);
                      
                        },
                        error: function() {
                            response.error("API Failed...");
                        }
                    });
                }        
            },
            error: function() {
                response.error("API Failed...");
            }
        });
    }  
});
 


/**
    Function Type   : Cloud Function
    Function Name   : updateDeviceStatus
    Author          : Sreeram Sadasivam
    Target Function : Gadgeteer
    Push Target     : Android/iOS/Window
    Description     : API service provided by Parse using Cloud functions. The API service is used by gadgeteer device to push the changes
                      to the Android/iOS/Windows mobile device which are subscribed to the given gadgeteer device.
    Inputs          : DeviceID    - Plant Monitoring hardware device id
                      LightStatus or WaterStatus or both with values as true or false.
                      Inputs are passed as JSON string in the format:
                      Example: {"DeviceID":<deviceid>,"LightStatus":true,"WaterStatus":false}
    Outputs         : A JSON String of the event provided by the gadgeteer device is sent to the various subscribed devices for the given
                      gadgeteer device. And success or error message is send to the gadgeteer device. A "Push successful..." or "Push Failed..."
                      or "API Failed" is send as JSON string with appropriate error code if it is present.
*/ 

Parse.Cloud.define("updateDeviceStatus", function(request, response) {
  Parse.Cloud.useMasterKey();
  var requestID   = request.params.DeviceID;
  var query       = new Parse.Query(Parse.Installation);
  var devQuery    = new Parse.Query("Device");
  var devChange   = false;

  if(requestID) {

  devQuery.equalTo("DeviceID",requestID);
  devQuery.first({

    success: function(devices) {
       
      var device = devices;
      var lastUpdated = new Date();
      var lastIlluminatedTime;
      var lastWateredTime;
      if((request.params.LightStatus != null)&&(request.params.LightStatus.length!=0)) {
        device.set("LightStatus", request.params.LightStatus);
        device.set("IlluminatedTime", lastUpdated);
        lastIlluminatedTime = lastUpdated;
        devChange = true;
      }
      if((request.params.WaterStatus != null)&&(request.params.WaterStatus.length!=0)) {
        device.set("WaterStatus", request.params.WaterStatus);
        device.set("WateredTime", lastUpdated);
        lastWateredTime = lastUpdated;
        devChange = true;
      }
      
      device.save(null, {
          success: function(device) {
                        // Execute any logic that should take place after the object is saved.

            //response.success("Registration Modified successfully...");  
          },
          error: function(device, error) {
            // Execute any logic that should take place if the save fails.
            // error is a Parse.Error with an error code and message.
            //response.error('Failed to create new object, with error code: ' + error.message);
          }
      });

      if(devChange) {

      var updateDataJSON = {"DeviceID":requestID,"LightStatus":request.params.LightStatus,"WaterStatus":request.params.WaterStatus,"IlluminatedTime":lastIlluminatedTime,"WateredTime":lastWateredTime};
      //JSON.stringify(updateDataJSON);
      query.equalTo("DeviceID", requestID);
      Parse.Push.send({ 
          where: query,
          data: {
            alert: updateDataJSON
          }
        }, {
        success: function() {
          response.success("Push successful..");
        },
        error: function(error) {
          response.error("Push Failed...");
        }
      });
      }
      else {
        response.success("No change...")
      }
    },
    error: function() {
      response.error("API Failed...");
    }
  });
  }
  else {
    response.error("DeviceID doesn't exist...");
  }
});

/**
    Function Type   : Cloud Function
    Function Name   : manualOverride
    Author          : Sreeram Sadasivam
    Target Function : Android/iOS/Window
    Description     : API service provided by Parse using Cloud functions. The API service is used by the mobile device which can control the 
                      subscribed hardware. This API service provides the mobile device the ability to override the settings in the subscribed
                      hardware device. It can turn on/off the Light or Water according to its need. Since there is no push mechanism in place
                      for gadgeteer module supported by Parse server. We have to revert to the polling mechanism. Therefore, when there is a 
                      manualOverride call, the event is passed to a PollQueue from which the Gadgeteer device will invoke PollQueue for overriding
                      events. It is done by the gadgeteer device by invoking the API -"pollForDevice".
    Inputs          : ExtDeviceId - Android or iOS or Windows Device ID.
                      LightStatus or WaterStatus or both with values as true or false.
                      Inputs are passed as JSON string in the format:
                      Example: {"ExtDeviceID":<extdeviceid>,"LightStatus":true,"WaterStatus":false}
    Outputs         : A JSON String saying whether the override was successful or not. Success messages as "Poll added success..." or failure
                      messages as "API Failed..." or "Poll add Failed..." or "Device doesn't exist..." or "No change..."
*/ 

Parse.Cloud.define("manualOverride", function(request, response) {

  Parse.Cloud.useMasterKey(); 
  var query = new Parse.Query(Parse.Installation);
  if((request.params.ExtDeviceID == null)||request.params.ExtDeviceID.length == 0) {
    response.error("API failed");
  }
  else {
    query.equalTo("deviceToken", request.params.ExtDeviceID);
    query.first({
        success: function(results) {
       
        if(results == null) {
            response.error("API failed...");
        }
        else {
 
            var PollQueue     = Parse.Object.extend("PollQueue");
            var Device        = Parse.Object.extend("Device");
            var pQueue        = new PollQueue();
            var polledDevice  = new Device();
            var pollQuery     = new Parse.Query("PollQueue");
            var devQuery      = new Parse.Query("Device");
            var pollDeviceID  = results.get("DeviceID");
            
            var isOverrideRequired = false;

            if((pollDeviceID == null)||(pollDeviceID.length==0)) {
              response.error("DeviceID doesn't exist...");
            }
            else {
            pollQuery.equalTo("DeviceID", pollDeviceID);
 
            pollQuery.first({
 
                success: function(queues) {
                 
                if(queues == null||queues.length == 0) {

                    devQuery.equalTo("DeviceID",pollDeviceID);
                    devQuery.first({
                      success: function(object) {
                      // Successfully retrieved the object.
                        if (object) {
                            polledDevice = object;
                        } else {
                          response.error("DeviceID doesn't exist...");
                        }
                      },
                      error: function(error) {
                         response.error("API Failed...");
                      }
                    });
                                                                              
                    
                    if((request.params.LightStatus != null)&&(request.params.LightStatus.length != 0)&&(Boolean(Boolean(request.params.LightStatus) != polledDevice.get("LightStatus")))) {         
                      
                      pQueue.set("LightStatus", request.params.LightStatus);
                      isOverrideRequired = true;
                    }
                    if((request.params.WaterStatus != null)&&(request.params.WaterStatus.length != 0)&&(Boolean(Boolean(request.params.WaterStatus) != polledDevice.get("WaterStatus")))) {         
                      
                      pQueue.set("WaterStatus", request.params.WaterStatus);
                      isOverrideRequired = true;
                    }
                    if(isOverrideRequired) {

                      pQueue.set("DeviceID",pollDeviceID );
                      pQueue.save(null, {
                        success: function(extDevice) {
                        // Execute any logic that should take place after the object is saved.
                          console.log((Boolean(request.params.WaterStatus) != polledDevice.get("WaterStatus")));
                          response.success("Poll added success..."); 
                        },
                        error: function(extDevice, error) {
                        // Execute any logic that should take place if the save fails.
                        // error is a Parse.Error with an error code and message.
                        response.error("Poll added failed...");
                        }
                      });         
                    }
                    else {
                      response.success("No change...");
                    }
                }
                else {

                    pQueue = queues;          
                    devQuery.equalTo("DeviceID",pollDeviceID);
                    devQuery.first({
                      success: function(object) {
                      // Successfully retrieved the object.
                        if (object) {
                            polledDevice = object;
                        } else {
                          response.error("DeviceID doesn't exist...");
                        }
                      },
                      error: function(error) {
                         response.error("API failed...");
                      }
                    });
                                                                              
                    
                    if((request.params.LightStatus != null)&&(request.params.LightStatus.length != 0)) {                 
                      pQueue.set("LightStatus", request.params.LightStatus);
                    }
                    if((request.params.WaterStatus != null)&&(request.params.WaterStatus.length != 0)) {         
                      pQueue.set("WaterStatus", request.params.WaterStatus);
                    }
                
                    pQueue.set("DeviceID",pollDeviceID );
                    pQueue.save(null, {
                        success: function(extDevice) {
                        // Execute any logic that should take place after the object is saved.
                          response.success("Poll added success");  
                        },
                        error: function(extDevice, error) {
                        // Execute any logic that should take place if the save fails.
                        // error is a Parse.Error with an error code and message.
                        response.error("Poll add Failed...");
                        }
                    }); 
                }   
                 
            },
                error: function(error) {
                  response.error("API failed...");
                }
            });
        }
        }     
        },
        error: function() {
            //response.success(query);
            response.error("API failed...");
        }
    });
  }
});

/**
    Function Type   : Cloud Function
    Function Name   : pollForDevice
    Author          : Sreeram Sadasivam
    Target Function : Gadgeteer
    Description     : API service provided by Parse using Cloud functions. It is used to Poll the server from the Gadgeteer device. This API service
                      is used as an alternative to push service provided by Parse. Since Push service is currently not available for gadgeteer devices,
                      polling mechanism is put into place. The gadgeteer device will poll the server every userdefined time from gadgeteer device to
                      see if there is any polling servicing to be done.
    Inputs          : DeviceID    - Plant Monitoring hardware device id
                      Inputs are passed as JSON string in the format:
                      Example: {"DeviceID":<deviceid>}
    Outputs         : If there is a poll service to be done then, JSON string will have what servicing needs to be done. 
                      Example: {"DeviceID":<deviceid>,"LightStatus":true,"WaterStatus":false}
                      If there is not service present in the queue then, return JSON will be {"result":"No Change"}
                      Or none of the above happens it will return an error as "API Failed" or "DeviceID doesn't exist" with error code as 141.
*/


Parse.Cloud.define("pollForDevice", function(request, response) {

  var pollQuery = new Parse.Query("PollQueue");
  if((request.params.DeviceID == null)||request.params.DeviceID.length == 0) {
    response.error("API Failed...");
  }
  else {
           pollQuery.equalTo("DeviceID", request.params.DeviceID);
 
            pollQuery.first({
 
                success: function(queues) {
                  if(queues != null && queues.length != 0) {  
                    var polledDevice = queues;
                    var polledDataJSON = {"DeviceID":request.params.DeviceID,"LightStatus":polledDevice.get("LightStatus"),"WaterStatus":polledDevice.get("WaterStatus")};
                    JSON.stringify(polledDataJSON);
                    polledDevice.destroy({
                      success: function(pollDeviceID) {
                      response.success(polledDataJSON);
              
                      },
                      error: function(pollDeviceID, error) {
                      // The delete failed.
                      // error is a Parse.Error with an error code and message.
                      }
                    });
                  }
                  else {
                    var devQuery = new Parse.Query("Device");
                    devQuery.equalTo("DeviceID",request.params.DeviceID);
                    devQuery.first({
                      success: function(object) {
                      // Successfully retrieved the object.
                        if (object) {
                            response.success("No Change");
                        } else {
                          response.error("DeviceID doesn't exist...");
                        }
                      },
                      error: function(error) {
                         response.error("API Failed...");
                      }
                    });
                    
                  }
                },
                error: function(error) {
                    response.error("API Failed...");
                }
            });
 
  }
});


/** Jobs...*/
 
/**
    Function Type   : Cloud Background Job
    Function Name   : weatherPredictionJob
    Author          : Sreeram Sadasivam
    Description     : Background Job service provided by Parse using Cloud Jobs. It is used to update the weather conditions of all registered
                      hardware with its geo location by invoking an external API service. This job is run from Scheduled jobs tab in Parse server
                      for every 15mins.
                       
    Inputs          : {}
    Outputs         : It modifies the server with new set of weather prediction systems.
*/
Parse.Cloud.job("weatherPredictionJob", function(request, status) {
  // ... set up variables
  Parse.Cloud.run('weatherPredictionInternalMethod', {}, {
    success: function(results) {
      status.success("weatherPredictionJob completed successfully.");
    },
    error: function(error) {
      status.error("API Failed...");
    }
  });
});

/**Testing purposes...*/
 
Parse.Cloud.define("hello", function(request, response) {
  response.success(new Date());
});

