/**
 *  Trigger My Lights
 *  Version 1.0.0 - 07/11/16
 *
 *  1.0.0 - Initial release
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
 *  You can find this smart app @ https://github.com/ericvitale/ST-Trigger-My-Lights
 *  You can find my other device handlers & SmartApps @ https://github.com/ericvitale
 *
 */
 
definition(
	name: "Trigger My Lights",
	namespace: "ericvitale",
	author: "ericvitale@gmail.com",
	description: "Set on/off, level, color, and color temperature of a set of lights based on motion, acceleration, and a contact sensor.",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {
	page name: "mainPage"
}

def mainPage() {
	dynamicPage(name: "mainPage", title: "", install: true, uninstall: true) {
    
    	section("Switches") {
			input "switches", "capability.switch", title: "Switches", multiple: true, required: false
    	}
        
        section("Dimmers") {
        	input "dimmers", "capability.switchLevel", title: "Dimmers", multiple: true, required: false
            input "selectedDimmerLevel", "number", title: "Dimmer Level", description: "Set your dimmers to...", required: false, defaultValue: 100
        }
        
        section("Color Lights") {
        	input "colorLights", "capability.colorControl", title: "Color Lights", multiple: true, required: false
            input "selectedColorLightsColor", "enum", title: "Select Color", required: false, options: ["Blue", "Green", "Red", "Yello", "Orange", "Pink", "Purple", "Random"]
            input "selectedColorLightsLevel", "number", title: "Level", required: false, defaultValue: 100
        }
        
        section("Color Temperature Lights") {
        	input "colorTemperatureLights", "capability.colorTemperature", title: "Color Temperature Lights", multiple: true, required: false
            input "selectedColorTemperatureLightsTemperature", "number", title: "Color Temperature", description: "2700 - 9000", range: "2700..9000", required: false
            input "selectedColorTemperatureLightsLevel", "number", title: "Level", defaultValue: 100, required: false
        }
        
        section("Schedule") {
        	input "useTimer", "bool", title: "Turn Off After", required: true, defaultValue: false
        	input "timer", "number", title: "Minutes", required: false, defaultValue: 10
        }
   	
    	section("Sensors") {
	        input "motionSensors", "capability.motionSensor", title: "Motion Sensors", multiple: true, required: false
            input "accSensors", "capability.accelerationSensor", title: "Acceleration Sensors", multiple: true, required: false
            input "contacts", "capability.contactSensor", title: "Contact Sensors", multiple: true, required: false
		}
        
        section("Restrictions") {
        	//input "modes", "mode", title: "Only in Modes", multiple: true, required: false
            //input "useTimeRange", "bool", title: "Only in Time Range", required: false, defaultValue: false
            input "active", "bool", title: "Rules Active?", required: true, defaultValue: true
        }
    
	    section([mobileOnly:true], "Options") {
			label(title: "Assign a name", required: false)
            input "logging", "enum", title: "Log Level", required: true, defaultValue: "DEBUG", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
    	}
	}
}

def determineLogLevel(data) {
	if(data.toUpperCase() == "TRACE") {
    	return 0
    } else if(data.toUpperCase() == "DEBUG") {
    	return 1
    } else if(data.toUpperCase() == "INFO") {
    	return 2
    } else if(data.toUpperCase() == "WARN") {
    	return 3
    } else {
    	return 4
    }
}

def log(data, type) {
    
    data = "NameTBD -- " + data
    
    try {
        if(determineLogLevel(type) >= determineLogLevel(logging)) {
            if(type.toUpperCase() == "TRACE") {
                log.trace "${data}"
            } else if(type.toUpperCase() == "DEBUG") {
                log.debug "${data}"
            } else if(type.toUpperCase() == "INFO") {
                log.info "${data}"
            } else if(type.toUpperCase() == "WARN") {
                log.warn "${data}"
            } else if(type.toUpperCase() == "ERROR") {
                log.error "${data}"
            } else {
                log.error "Toggimmer -- Invalid Log Setting"
            }
        }
    } catch(e) {
    	log.error ${e}
    }
}

def installed() {   
	log("Begin installed.", "DEBUG")
	initalization() 
    log("End installed.", "DEBUG")
}

def updated() {
	log("Begin updated().", "DEBUG")
	unsubscribe()
    unschedule()
	initalization()
    log("End updated().", "DEBUG")
}

def initalization() {
	log("Begin intialization().", "DEBUG")
    
    if(active) {
    	subscribe(motionSensors, "motion", motionHandler)
        subscribe(accSensors, "acceleration.active", accelerationHandler)
        subscribe(contacts, "contact.open", contactHandler)
        log("Subscriptions to devices made.", "INFO")   
    } else {
    	log("App is set to inactive in settings.", "INFO")
    }
    
    setRoomActive(false)

    log("End initialization().", "DEBUG")
}

def motionHandler(evt) {
	log("Begin motionHandler(evt).", "DEBUG")
	triggerLights()
    log("End motionHandler(evt).", "DEBUG")
}

def accelerationHandler(evt) {
	log("Begin accelerationHandler(evt).", "DEBUG")
    triggerLights()
    log("End accelerationHandler(evt).", "DEBUG")
}

def contactHandler(evt) {
	log("Begin contactHandler(evt).", "DEBUG")
	triggerLights()
    log("End contactHandler(evt).", "DEBUG")
}

def triggerLights() {
	log("Begin triggerLights().", "DEBUG")

    if(!isRoomActive()) {
    	setSwitches()
        setDimmers(selectedDimmerLevel)
        setColorLights(selectedColorLightsLevel, selectedColorLightsColor)
        setColorTemperatureLights(selectedColorTemperatureLightsLevel, selectedColorTemperatureLightsTemperature)
        setColorLights(selectedColorLightsLevel, selectedColorLightsColor)
		setRoomActive(true)
        log("Lights triggered.", "INFO")
        
        if(useTimer) {
        	setSchedule()
        } else {
        	runIn(60, reset)
        }
    } else {
    	log("Room is active, ignorining command.", "DEBUG")
    }

	log("End triggerLights().", "DEBUG")
}

def setSwitches() {
	log("Begin setSwitches().", "DEBUG")
    
    switches.each { it->
    	it.on()
    }
    
    log("End setSwitches().", "DEBUG")
}

def setDimmers(valueLevel) {
	log("Begin setDimmers(onOff, value).", "DEBUG")
    
    dimmers.each { it->
    	it.setLevel(valueLevel)
        it.on()
    }
    
    log("End setDimmers(onOff, value).", "DEBUG")
}

def setColorLights(valueLevel, valueColor) {
	log("Begin setColorLights(onOff, valueLevel, valueColor).", "DEBUG")
    def colorMap = getColorMap(valueColor)
    
	log("Color = ${valueColor}.", "DEBUG")
    log("Hue = ${colorMap['hue']}.", "DEBUG")
    log("Saturation = ${colorMap['saturation']}.", "DEBUG")
    
    colorLights.each { it->
        it.on()
        //it.setColor(colorMap)
    	it.setHue(colorMap['hue'])
        it.setSaturation(colorMap['saturation'])
        it.setLevel(valueLevel)
    }
    
    
    log("End setColorLights(onOff, valueLevel, valueColor).", "DEBUG")
}

def setColorTemperatureLights(valueLevel, valueColorTemperature) {
	log("Begin setColorTemperatureLights(onOff, valueLevel, valueColorTemperature).", "DEBUG")
    
    colorTemperatureLights.each { it->
    	it.setLevel(value)
        it.setColorTemperature(valueColorTemperature)
        it.on()
    }
    
    log("End setColorTemperatureLights(onOff, valueLevel, valueColorTemperature).", "DEBUG")
}

def setAllLightsOff() {
	log("Begin setAllLightsOff().", "DEBUG")
    	setAllLights("off")
        log("Turned lights off per the schedule.", "INFO")
    log("End setAllLightsOff().", "DEBUG")
}

def setAllLights(onOff) {
	log("Begin setAllLights(onOff)", "DEBUG")
    
    if(onOff.toLowerCase() == "off") {
    	switches.each { it->
     		it.off()
        }
        
        dimmers.each { it->
        	it.off()
        }
        
        colorTemperatureLights.each { it->
        	it.off()
        }
        
        colorLights.each { it->
        	it.off()
        }
    } else {
    	switches.each { it->
     		it.on()
        }
        
        dimmers.each { it->
        	it.on()
        }
        
        colorTemperatureLights.each { it->
        	it.on()
        }
        
        colorLights.each { it->
        	it.on()
        }
    }
    
    setRoomActive(false)
    
    log("End setAllLights(onOff)", "DEBUG")
}

def setSchedule() {
	log("Begin setSchedule().", "DEBUG")
    if(useTimer) {
    	runIn(timer*60, setAllLightsOff)
        log("Setting timer to turn off lights in ${timer} minutes.", "INFO")
    }
    log("End setSchedule().", "DEBUG")
}

def reschedule() {
	log("Begin reschedule().", "DEBUG")
    unschedule()
    setSchedule()
    log("End reschedule().", "DEBUG")
}

def isRoomActive() {
	log("Begin isRoomActive() -- Has return value.", "DEBUG")
    
    if(state.roomActive == null) {
    	state.roomActive = false
    }
    
    return state.roomActive
}

def setRoomActive(val) {
	log("Being setRoomActive().", "DEBUG")
    state.roomActive = val
    log("End setRoomActive().", "DEBUG")
}

def reset() {
	log("Begin reset().", "DEBUG")
	setRoomActive(false)
	log("End reset().", "DEBUG")
}

def getColorMap(val) {
	
    def colorMap = [:]
    
	switch(val.toLowerCase()) {
    	case "blue":
        	colorMap['hue'] = "240"
            colorMap['saturation'] = "100"
            colorMap['level'] = "50"
            break
        case "red":
        	colorMap['hue'] = "0"
            colorMap['saturation'] = "100"
            colorMap['level'] = "50"
            break
        case "yellow":
            colorMap['hue'] = "60"
            colorMap['saturation'] = "100"
            colorMap['level'] = "50"
        default:
            colorMap['hue'] = "60"
            colorMap['saturation'] = "100"
            colorMap['level'] = "50"	
    }
    
	return colorMap
}