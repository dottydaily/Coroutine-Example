var fs = require('fs');

fs.unlink('mynewfile2.txt', (err) => {
    if (err) throw err;
    console.log('File deleted!');
});