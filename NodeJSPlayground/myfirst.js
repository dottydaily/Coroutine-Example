var http = require('http');
var dateTime = require('./myfirstmodule')

http.createServer(function (req, res) {
    res.writeHead(200, {'Content-Type': 'text/html'});
    res.write("THe date and time are currently: " + dateTime.myDateTime());
    res.write("URL : " + req.url)
    res.end('Hello World!');
}).listen(8080);