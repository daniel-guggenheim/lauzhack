'use strict';

const mongoose = require('mongoose');
const request = require("request");
const express = require('express');
const app = express();
var bodyParser = require('body-parser');
var multer = require('multer'); // v1.0.5
var upload = multer(); // for parsing multipart/form-data

const GOOGLE_TOKEN_VERIFICATION_WEBSITE = 'https://www.googleapis.com/oauth2/v3/tokeninfo?id_token='


let mongoUrl = '';

// check if the app is running in the cloud and set the MongoDB settings accordingly
if (process.env.VCAP_SERVICES) {
  const vcapServices = JSON.parse(process.env.VCAP_SERVICES);
  mongoUrl = vcapServices.mongodb[0].credentials.uri;
} else {
  mongoUrl = 'mongodb://localhost/db';
}

// connect to our MongoDB
mongoose.connect(mongoUrl);



app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded





// GET : is-username-available
app.get('/is-username-available/:username/', function (req, res) {
    var test_username = req.params.username;
    res.send('username requested: ' + test_username + ', availability: ' + is_username_available(test_username));
})

// POST: signup
app.get('/signup/:username/:google_token', function (req, res) {
    console.log("Login starting....");
    var google_response = get_id_from_google(req.params.google_token);

    if ((google_response == null) || !google_response.aud) {
        console.log(google_response.aud);
        var error_message = 'Error requesting google token. Network problems or token does not exist.\n';
        console.log(error_message);
        res.send(error_message + google_response);
    } else {
        var google_id = google_response.aud;
        res.send(google_response);
        // if (database.contains(google_id)) { //TODO
        //     //Login case
        //     //Generate our_token
        //     //Add token to database
        //     res.send('our_token')
        // } else {
        //     res.send('no_user_in_database')
        // }
    }
})



// POST: login
app.post('/login', upload.array(), function (req, res) {
    console.log("Login starting....");
    // var google_token = req.params.google_token;
    var google_token = req.body.google_token;
    // console.log(req.body);
    console.log('Request on: ' + google_token);
    get_id_from_google(google_token,function(google_response){
        console.log('callback: '+google_response);
    if ((google_response == null) || !google_response.aud) {
        var error_message = 'Error requesting google token. Network problems or token does not exist.';
        console.log(error_message);
        res.send(error_message);
    } else {
        var google_id = google_response.aud
        console.log('google_id = ' + google_id);
        res.send(google_response);
        // if (database.contains(google_id)) { //TODO
        //     //Login case
        //     //Generate our_token
        //     //Add token to database
        //     res.send('our_token')
        // } else {
        //     res.send('no_user_in_database')
        // }
    }

    });
    
})



function is_username_available(username) {
    return false;
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
            callback(null);
        } else if (response.statusCode != 200) {
            console.log('Error in status code when requesting google token. Status code= ', response.statusCode);
            callback(body);
        } else {
            console.log('Google request success!: ' + body);
            callback(JSON.parse(body));
        }
    });
}








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