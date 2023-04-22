# Bento's Bluebook

This is the guide to all pricing in Minecraft!

This plugin will tell you the value of any object in the game! Setting up a shop? How much should you sell a Diamond Chestplate for? Is that set of bookshelves a good deal? Stop picking prices at random and use this plugin instead!

This plugin is perfect for admins who want to set up malls or stores quickly and not break the economy. Prices are calculated from base prices for non-craftable items that you can set in the config file. A profit can be added.

## How to Use
Install (see below) and set permissions
If bluebook.signshow permission is enabled:

Place a sign with [VALUE] as the first line
Hold an object in your hand and hit the sign (or any nearby sign or chest)
The value is shown on the sign
If bluebook.show permission is enabled (default):

Hold an object in your hand and hit any nearby sign or chest
The value is shown in chat

## Problems
Please file a ticket if you find a bug.

## Commands
To find out the BlueBook price, hold an item in your hand and hit a chest or sign with it. See the screenshot for an example

`/bbreload` - Admin command to reload config.yml settings

## Permissions
Only Ops will have automatic access to the BlueBook. If you want to allow it for others, you will need to give them the following permission:

* `bluebook.show` - Permission to see the price guide
* `bluebook.reload` - Permission to reload the config.yml
* `bluebook.signshow` - Permission to have prices appear on signs with [VALUE] on them

## Installation and Configuration
(For upgrading - copy or delete your bluebook/config.yml file)
1. Download the plugin (bluebook.jar)
2. Place into your plugins folder
3. Restart your server (or reload plugins)
4. The plugin will make a folder called BlueBook. Open that folder.
5. Check config.yml and set the prices how you like. Usually the ones in there will already make sense.
6. Make sure you set the profit* to something you are happy with! (100 means the prices will be double).
7. Restart the server if you changed the prices or type /bbreload
8. Set up permissions

Enjoy!

## Upgrading
If you never customized the config.yml file, then delete it so that new items will be added to it. If you did customize config.yml, then move it to a temporary file, reload or restart the server and compare the new config.yml with your old one. New items will be placed at the bottom of the list and marked with a comment.

To Do
Any requests? Let me know!
