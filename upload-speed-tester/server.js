var fs = require('fs'),
http = require('http'),
https = require('https');

var express = require('express');
var app = express();
var cors = require('cors');
var bodyParser = require("body-parser");

var opts = {
	port: 8081,
	maxUploadSize: "20971520"
};

var sslopt = {
	key: fs.readFileSync('ssl/nginx.key'),
	cert: fs.readFileSync('ssl/nginx.crt')
};

app.use(express.static(__dirname + '/html'));
app.use(bodyParser.urlencoded({ limit: '20MB', extended: true }));
app.use(cors());



app.post('/upload', function(req, res) {
	res.setHeader('Content-Type', 'application/json');
	res.send(JSON.stringify({}));
	res.end();
});

var server = https.createServer(sslopt, app).listen(opts.port);

//app.listen(opts.port);
