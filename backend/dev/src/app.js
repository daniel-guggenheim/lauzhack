'use strict';

const mongoose = require('mongoose');
const request = require("request");
const express = require('express');
const app = express();
var bodyParser = require('body-parser');
var multer = require('multer'); // v1.0.5
var upload = multer(); // for parsing multipart/form-data

const GOOGLE_TOKEN_VERIFICATION_WEBSITE = 'https://www.googleapis.com/oauth2/v3/tokeninfo?id_token='

app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded


/******************** DATEBASE INIT ********************/

let mongoUrl = '';

// check if the app is running in the cloud and set the MongoDB settings accordingly
if (process.env.VCAP_SERVICES) {
    const vcapServices = JSON.parse(process.env.VCAP_SERVICES);
    mongoUrl = vcapServices.mongodb[0].credentials.uri;
} else {
    mongoUrl = 'mongodb://localhost/breakingwall';
}

// connect to our MongoDB
mongoose.connect(mongoUrl);


var userSchema = new mongoose.Schema({
    // _id is automatically created
    google_id: String,
    username: String,
    firebase_token: String,
    last_message: {
        from: { type: String },
        to: { type: String },
        date: { type: Date, default: Date.now },
        content: { type: String }
    }
});

// create a Mongoose model
const Users = mongoose.model('Users', userSchema);



/******************** MAIN ROUTING FUNCTIONS ********************/


// Main page
app.get('/', function (req, res) {
    console.log("Main page request");
    res.send('Rest API for our app.');
})

// Testing function
app.get('/test', function (req, res) {
    console.log("Got a GET test");
    var user = {
        id: 'bonjour le monde! (beurk)',
        tr: 34
    }
    res.send(user);
})



// IS USERNAME AVAILABLE (get)
app.get('/is-username-available/:username/', function (req, res) {
    var test_username = req.params.username;
    res.send('username requested: ' + test_username + ', availability: ' + is_username_available(test_username));
})



// SIGNUP (post)
app.post('/signup', upload.array(), function (req, res) {
    console.log("Sign up starting....");

    var google_token = req.body.google_token;
    var username = req.body.username;

    //Test if username is available
    is_username_available(username, function (usernameAvailable) {
        if (!usernameAvailable) {
            var error_message = 'username ' + username + ' is not available.'
            console.log(error_message)
            res.status(410).send(error_message);
            return;
        }

        // Launch request to test google token
        launch_google_id_request(res, google_token, function (google_id) {
            if (google_id != undefined) {
                if (true) { //TODO: google_id is not in database
                    // Add google_id in database
                    const myNewUser = new Users({
                        google_id: google_id,
                        username: username,
                        firebase_token: '',
                        last_message: {
                            content: '',
                        }
                    });

                    myNewUser.save((err, newUser) => {
                        if (err) { throw err; }
                        console.log('User ' + newUser.username + ' saved with success');
                    });
                    res.status(200).send({ token: google_id });
                } else {
                    //This code should never be reached, as signup must be only done after login.
                    res.status(400).send('This code should never be reached, as signup must be only done after login.');
                }
            }

        });
    });
});


// LOGIN (post)
app.post('/login', upload.array(), function (req, res) {
    console.log("Login starting....");
    var google_token = req.body.google_token;
    launch_google_id_request(res, google_token, function (google_id) {
        if (google_id != undefined) {
            if (true) { //google_id is not in database
                var message = 'Token is not in database.';
                console.log(message);
                res.status(404).send(message + '  -  ' + google_id);
            } else {
                //Google id in database == person has already logged in at least once
                res.status(200).send(google_id);
            }
        }
    });
})



// Send




/******************** HELPER FUNCTIONS ********************/

// Launch a google id request, by taking the token and sending it to google
// Send the response when it is negative
function launch_google_id_request(res, google_token, finish_login_signup) {
    console.log("Requesting google id....");
    console.log('Request on: ' + google_token);

    //Launch method to get id, with the callback when you get the id.
    get_id_from_google(google_token, function (google_response) {
        console.log('callback: ' + google_response);
        if ((google_response == undefined)) {
            var error_message = 'Error requesting google token.';
            console.log(error_message);
            res.status(500).send(error_message);
            finish_login_signup(undefined);
        } else {
            //No problem in getting code
            var google_id = google_response.aud;
            if (!google_id) {
                var error_message = 'Token is not valid.';
                console.log(error_message);
                res.status(401).send(error_message);
                finish_login_signup(undefined);
            }
            else {
                //Google id in database == person logged in already once
                console.log('google_id = ' + google_id);
                finish_login_signup(google_id);
            }
        }
    });
}



function get_id_from_google(token, callback) {
    var request_address = GOOGLE_TOKEN_VERIFICATION_WEBSITE.concat(token);
    request({
        uri: request_address,
        method: "GET",
        timeout: 10000,
        followRedirect: true,
    }, function (error, response, body) {
        if (error) {
            console.log('Error requesting google token: ', error);
            callback(undefined);
        } else if (response.statusCode != 200) {
            console.log('Error in status code when requesting google token. Status code= ', response.statusCode);
            callback(body);
        } else {
            console.log('Google request success!: ' + body);
            callback(JSON.parse(body));
        }
    });
}



function is_username_available(username, callback) {
    Users.findOne({ 'username': username }, function (err, user) {
        if (err) return handleError(err);
        const username_available = (user == undefined);
        console.log("Username available: " + username_available);
        callback(username_available);
    })
}






//Server starting
// var server = app.listen(8081, function () {
//     var host = server.address().address
//     var port = server.address().port
//     console.log(port)
//     console.log("Example app listening at http://%s:%s", host, port)
// })
// our basic route to serve the index page



/**
 * The following code comes from https://github.com/swisscom/cf-sample-app-nodejs.git
 */
// app.use('/', routes);

// catch 404 and forward to error handler
// app.use((req, res, next) => {
//   const err = new Error('Not Found');
//   err.status = 404;
//   next(err);
// });

// error handlers

// development error handler
// will print stacktrace
// if (app.get('env') === 'development') {
//   app.use((err, req, res, next) => {
//     res.status(err.status || 500).send('error', {
//       message: err.message,
//       error: err
//     });
//   });
// }

// production error handler
// no stacktraces leaked to user
// app.use((err, req, res, next) => {
//   res.status(err.status || 500).send('error', {
//     message: err.message,
//     error: {}
//   });
// });

module.exports = app;