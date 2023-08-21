# E-Cinema

This is a mock web app. The purpose of building this app was to learn how to build apps in Spring Boot. I used many different free tutorials online for learning: Youtube, W3Schools, etc. This app allows customers to purchase tickets to see movie showings. There are two types of user authorities: customers and admins. A user account can be either a customer, moderator, or admin account or any combination of the three. Customers are able to save payment card information to their account and purchase tickets. Admins are able to add, edit, and delete entities such as movies, showtimes, showrooms, and so on. Admins can also lock users' accounts and reset a user's password. Moderators have the single ability to delete reviews if necessary.

The app has many cool features like the ability to write reviews for movies, book multiple seats each with a different ticket type, like and dislike tickets, and so much more!

To run this app yourself, you will need to provide an email address and an app-specific password as environment variables. This email will be used as the "business" email, i.e. the email from which all "business" or "admin" messages will come from. To generate an app-specific password using gmail, follow the steps at https://support.google.com/accounts/answer/185833?hl=en.

Make sure that Docker is running, open the Terminal and cd into the directory containing the Dockerfile, and input the following commands. Do not include quotation marks for env vars.

        $ docker network create ecinema-network

        $ docker container run --name mysqldb --network ecinema-network -e MYSQL_DATABASE=ecinemadb -e MYSQL_ROOT_PASSWORD=root -d mysql:8

        $ docker image build -t ecinema .

            ("email" = email address)
            ("password" = app-specific password for email)

        $ docker container run --network ecinema-network --name ecinema-container -p 8080:8080 -e emailAddress="email" -e emailPassword="password" -d ecinema

            (optional command to see log output of app, "id" = id of the running container)

        $ docker container logs -f "id"

Now you may visit @ "localhost:8080/" to begin using the app!

If you would like to log into the root admin account, use "RootUser123" or "admin@gmail.com" for the username and "password123?!" for the password. Because the email is fake, some functionality of this admin will not work by default. If you would like to use a real email address, then go to the file src/main/java/com/ecinema/app/configs/InitializationConfig.java and change the email in line 64 that reads "rootUserForm.setEmail("admin@gmail.com");". Also, along with that, anything default such as movies, showrooms, and so on can be changed by editing the InitializationConfig.java (or any other file for that matter) if you just want to goof around with the project. :)

![1](img/1.png?raw=true "1")
![2](img/2.png?raw=true "2")
![3](img/3.png?raw=true "3")
![4](img/4.png?raw=true "4")
![5](img/5.png?raw=true "5")
![6](img/6.png?raw=true "6")
![7](img/7.png?raw=true "7")
