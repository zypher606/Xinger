'use strict';

const mongoose = require('mongoose');

const Schema = mongoose.Schema;


const postImageSchema = mongoose.Schema({ 

	email			: String, 
	descriptions	: String,
	tags			: String,
	filename		: String,
	created_at		: String
});

mongoose.Promise = global.Promise;
const conn = mongoose.createConnection('mongodb://localhost:27017/node-login');

module.exports = conn.model('post_image', postImageSchema);


// module.exports = mongoose.model('profile', profileSchema);

// mongoose.connection.close()