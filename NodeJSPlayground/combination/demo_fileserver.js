var http = require('http');
var fs = require('fs');

http.createServer((req, res) => {
    var url = new URL(req.url, 'http://localhost:8080/')
    var fileName = "." + url.pathname

    fs.readFile(fileName, (err, data) => {
        if (err) {
            res.writeHead(404, {'Content-Type': 'text/html'});
            return res.end("404 Not Found");
        }

        res.writeHead(200, {'Content-Type': 'text/html'});
        res.write(data);
        return res.end();
    });
}).listen(8080);