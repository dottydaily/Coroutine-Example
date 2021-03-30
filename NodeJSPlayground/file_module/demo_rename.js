var fs = require('fs');

fs.rename('mynewfile1.txt', 'myrenaedfile.txt', (err) => {
    if (err) throw err;
    console.log('File Renamed!');
});