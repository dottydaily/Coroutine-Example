require('dotenv').config()

const express = require('express')
const app = express()
const port = process.env.PORT

app.get('/', (req, res) => {
    res.send('Hello World! from GET method.')
})

app.post('/', (req, res) => {
    res.send('Hello World! from POST method.')
})

app.put('/user', (req, res) => {
    res.send('Got a PUT request at /user.')
})

app.delete('/user', (req, res) => {
    res.send('Got a DELETE request at /user')
})

// Serve files in directory name 'images'
app.use('/files', express.static('public'))

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})