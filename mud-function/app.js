const express = require('express')
const app = express()
const enrouten = require('express-enrouten');
const port = 3000

app.use(express.json())
app.use(enrouten({directory: 'lambdas'}))

app.listen(port, () => console.log(`Application listening on port ${port} `))
