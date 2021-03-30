var fs = require('fs');

fs.writeFile('mynewfile3.txt', 'Hello content!', (err) => {
    if (err) throw err;
    console.log('Saved');
});