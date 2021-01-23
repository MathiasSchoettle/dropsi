## Documentation and Tutorial for the Cloud Filesystem DROPSI

###### Mathias Schöttle - MtrNr. 3147337

## 1. Deployment
1. Kill old process
2. Delete the old jar
3. Copy the new jar to directory
4. execute start.sh

## 2. Example Account
###### This account is pre-filled with files and folders

    username: Albus Dumbledore
    password: cockroachcluster

## 3. Swagger

# TODO

## 3. Design Decisions

> **Q:**  
> Why use DTOs for the Files and Folders in the REST API instead of the already existing Entities?  
> **A:**  
> The Entities used internally have attributes that should not be exposed to the users and systems using the API
> i.e. permissions or accesslogs.  
> Ignoring them via **@JsonIgnore** is not an option as these objects will be parsed back on the consumer side resulting in fields containing null.  
> Removing these attributes from the class and only having an unidirectional connection is not desirable as we would not be able to take advantage of the ORM features like __CascadeType.REMOVE__  
> DTOs make life easier for the consumer as well as for the implementation while only having the overhead of a few more entities and a parser.

## 3. Tutorial

### 3.1 Login & Sign Up
Register a new Account with a new email / username and login in with the username and password.

### 3.2 Settings
Clicking on the image in the top right corner of the main page will bring you to the settings page.  
Here you can view and edit information of your account.

### 3.3 Mainpage
On the main page you can upload new files and generate new folders.   
These are then displayed in the currently selected folder.

Click on a folder to move into it.
Click on a file to view it in browser ( *not all file types supported* )   
You can also download or delete them.   
The dropdown reveals more options.

##### 3.3.1 Info
By clicking on the info tab in the dropdown a new page is shown including
metadata of the file and the corresponding accesslogs.   
The file / folder name can also be changed here.

##### 3.3.2 Move
By clicking the Move option a new view is shown in which only the folders
are shown.   
Navigate through the folders to the desired locations to
move the selected file / folder.

##### 3.3.3 Copy
Copy simply copies the selected file / folder and content into the same folder.

##### 3.3.4 Share
Clicking on share opens the share view. Filter for other users and add them to the list.   
Click on the Share button to share the file / folder with the other users.

### 3.4 Shares
In the Shares tab all files / folders which have been shared with you are shown, sorted by the corresponding Accounts.   
These files / folders can be downloaded or the permission can be revoked by clicking on the x.

### 3.5 Shared
In this tab all the files / folders ***you*** shared are shown. Every share has the accounts it is shared with shown underneath it.  
Click on a avatar to remove the permission of this single account or remove all of the permissions by clicking on the button in the top right of the card.