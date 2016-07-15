# ST-Trigger-My-Lights

## Summary
Trigger My Lights is a smart app that allows you to set parameters for switches, dimmers, smart color lights, smart white lights based on events from 1 to n motion sensors, 1 to n accelerometers, 1 to n contact sensors, mode changes, & when a routine is executed.

If you have several scenarios you would like to control, just install the app from "My Apps" multiple times.

## Example Scenarios
1. Turn the garage lights on (switch) when the inside garage door is opened (contact sensor), the garage door is opened (accelerometer), or the one of the garage windows are opened (contact sensor).
2. Turn the hallway lights on, level to 5% and temperature to 2750K (smart white light) if motion is detected in the hallway and it is after sundown. FYI - Sunrise / Sundown feature coming soon.
3. Turn the Family Room Lamps on, level to 20% and color to green if the routine "watch TV" is executed. FYI - Color lights coming soon.

## Installation via GitHub Integration
1. Open SmartThings IDE in your web browser and log into your account.
2. Click on the "My SmartApps" section in the navigation bar.
3. Click on "Settings".
4. Click "Add New Repository".
5. Enter "ericvitale" as the namespace.
6. Enter "ST-Trigger-My-Lights" as the repository.
7. Hit "Save".
8. Select "Update from Repo" and select "ST-Trigger-My-Lights".
9. Select "trigger-my-lights.groovy".
10. Check "Publish" and hit "Execute".

## Manual Installation (if that is your thing)
1. Open SmartThings IDE in your web browser and log into your account.
2. Click on the "My SmartApps" section in the navigation bar.
3. Click the blue "+ New SmartApp" button at the bottom of the page.
4. Click "From Code".
5. Paste in the code from "trigger-my-lights.groovy" and hit "Create". (https://github.com/ericvitale/ST-Trigger-My-Lights/blob/master/smartapps/ericvitale/trigger-my-lights.src/trigger-my-lights.groovy)
6. Click the "Publish" --> "For Me".
7. The app will appear on your app under "Marketplace" --> "My Apps"
