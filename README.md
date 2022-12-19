# ChatApp
It is an open-source app with real-time messaging using Firebase and Node Js.

Original source: https://github.com/krishkamani/ChatApp

Note: the code is updated to modern Android version (compile version SDK 33), tested on real devices 
with Android 12 and Android 8.

This version **WhatsappChatSdk33Newer** get some additions like a paginated RecyclerView for 
the chat message.

You need to setup your Firebase service and download your own google-services.json 

* For Authentication use Email+Password and Google

In Firebase console use project **SimpleChat** (simplechat-6671c)

* For Authentication use Email+Password

* For Realtime Database use this rule (see : https://medium.com/@juliomacr/10-firebase-realtime-database-rule-templates-d4894a118a98)

```plaintext
// Only authenticated users can access/write data
{
 "rules": {
 ".read": "auth != null",
 ".write": "auth != null"
 }
}
```

```plaintext
{
  "rules": {
    // User profiles are only readable/writable by the user who owns it
    "users": {
      "$UID": {
        ".read": "auth.uid == $UID",
        ".write": "auth.uid == $UID"
      }
    },

    // Posts can be read by anyone but only written by logged-in users.
    "posts": {
      ".read": true,
      ".write": "auth.uid != null",

      "$POSTID": {
        // UID must match logged in user and is fixed once set
        "uid": {
          ".validate": "(data.exists() && data.val() == newData.val()) || newData.val() == auth.uid"
        },

        // User can only update own stars
        "stars": {
          "$UID": {
              ".validate": "auth.uid == $UID"
          }
        }
      }
    },

    // User posts can be read by anyone but only written by the user that owns it,
    // and with a matching UID
    "user-posts": {
      ".read": true,

      "$UID": {
        "$POSTID": {
          ".write": "auth.uid == $UID",
        	".validate": "data.exists() || newData.child('uid').val() == auth.uid"
        }
      }
    },


    // Comments can be read by anyone but only written by a logged in user
    "post-comments": {
      ".read": true,
      ".write": "auth.uid != null",

      "$POSTID": {
        "$COMMENTID": {
          // UID must match logged in user and is fixed once set
          "uid": {
              ".validate": "(data.exists() && data.val() == newData.val()) || newData.val() == auth.uid"
          }
        }
      }
    }
  }
}
```

old Rule with Timeout:
```plaintext
{
  "rules": {
    ".read": "now < 1670886000000",  // 2022-12-13
    ".write": "now < 1670886000000",  // 2022-12-13
  }
}
```

* For storage use this rule:
```plaintext
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if true;
    }
  }
}
```

#### Note: I am working on a new version Android Studio 3.6

Don't Forget to <a href="https://www.youtube.com/channel/UCV8auqEr_jx606MqyeyIPpw?sub_confirmation=1">Subscribe</a> My Channel , like video and share to your friends. If you want to learn any new things then comment over that. We will make new video on that As soon As Possible.

## Features:
- Login with email or Phone Number
- Private messaging (one to one)
- Rich messaging(text, image, pdf, word, all other files)
- For sender Delete message for me or everyone
- For Receiver Delete message for me
- If image then View in full screen
- Push Notification
- For profile settings Image cropper functions
- Powered by Firebase
- Firebase UI
- If Any files then download and View it
- Find friends
- All candidate details with Username and status
- For sender Send chat Request and also cancel that request
- For receiver Accept chat request and delete that request
- If Request accept then its came to contact details
- User send request then receiver get notification (Using Node Js)

## Images:
![11](https://user-images.githubusercontent.com/52067673/83349772-fe76cb80-a354-11ea-9114-f5481930dd33.PNG)

![12](https://user-images.githubusercontent.com/52067673/83349968-3a5e6080-a356-11ea-8b04-73bb9d48496f.PNG)

![13](https://user-images.githubusercontent.com/52067673/83350057-f15adc00-a356-11ea-8bd7-fbf09e9652ad.PNG)

![14](https://user-images.githubusercontent.com/52067673/83350128-878f0200-a357-11ea-94dc-070bba60fbe5.PNG)


# Downloading the source code

You can download complete Chat App source code as a <a href="https://github.com/krishkamani/ChatApp/archive/master.zip"> Zip file</a>. 
you will have to download the complete source code everytime it is updated on the repository.

### Stay Up to date
It is important for you stay up to date with the new features, latest updates and bug fixing. Ensure to Star( * ) the project on Github and get notified whenever any update coming.
## Take this steps to download ChatApp
Note: Make sure you can use latest android version 3.6

1) First Launch Android Studio
2) Open the download folder and unzip ChatApp-master.zip file.
3) Open the project name ChatApp from the folder where you have downloaded the code using menu File -> Open 
4) It may take a while to build the project for the first time.
5) Once the build is over, run on the device using menu Run -> Run (app) (Launch app in emulator or phone)
6) You can see a login page as above.

## Contact
For any query or suggestions you can contact me :<br>
Email: kdkamani00@gmail.com
