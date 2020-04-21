[![Build Status](https://travis-ci.org/condolent/channel-guardian.svg?branch=master)](https://travis-ci.org/condolent/channel-guardian) ![GitHub issues](https://img.shields.io/github/issues/condolent/channel-guardian) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/condolent/channel-guardian) [![Discord](https://img.shields.io/discord/702177588038074468)](https://discord.gg/CBDT4eV)
# Channel Guardian
Channel Guardian allows you to password protect text channels via one simple command. The bot will then hide the channel for everyone without the correct role.

# Usage
* `g!password` - Get information regarding how to access a protected channel.
* `g!protect <#channel> <password>` - Password protect the mentioned channel. Hiding it for everyone else.
* `g!unprotect <#channel>` - Removes the password protection and resets it to its previous state.

---

## Setup
1. Download the [latest release](https://github.com/condolent/channel-guardian/releases/latest)
2. Create the file `resources/bot.properties` and paste this into the file:
```properties
token=yourtoken
db.host=hostip
db.name=database
db.user=username
db.pass=password
```
3. Replace the property values in your file with your settings.
4. Build, done!