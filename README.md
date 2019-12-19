# BookFace

The main idea behind this application is sharing books between friends. The core functionality should allow creating a list of books user owns, seeing books of users’ friends and being able to notify a friend if the user is interested in reading a book.

Here are listed the functionalities implemented in the application:

- User can log in with a Facebook account
- Public account information and friends list is fetched with the app automatically 
- User can see personal information (name, photo)
- User can change default message that is used to ask friends for selected book
- User can search a book by title or author-name
- User can add a book from a list of found books to the list for lending
- User can see the list of own books
- User can erase all their books
- User can change the status of  own books (status shows whether a book is already taken)
- User can see friends’ books in the list.
- User can use generated message with book lending request to send it via messenger to a book owner
- User can read the description of books of his friends. 

## Installation
put your `google-services.json` file to `\app\` (for access to firebase)

put `fb.secret.xml` to `\app\src\main\res\values\` with values of your facebook-project. Example of the file:
```
<resources>
    <string name="facebook_app_id">1234567890123456</string>
    <string name="fb_login_protocol_scheme">fb1234567890123456</string>
</resources>
```
