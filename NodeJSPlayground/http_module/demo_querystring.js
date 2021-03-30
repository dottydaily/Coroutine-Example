var http = require('http');

http.createServer(function (req, res) {
    res.writeHead(200, {'Content-Type': 'text/html'});
    var searchParams = new URL(req.url, "http://localhost:8080/").searchParams
    var txt = searchParams.get("year") + " " + searchParams.get("month")
    res.end(txt)
}).listen(8080);