'use strict';

const mongoose = require('mongoose');

const Schema = mongoose.Schema;


const profileSchema = mongoose.Schema({ 

	name 			: String,
	email			: String, 
	bio				: String,
	profile_pic		: String,
	cover_video		: String
});

mongoose.Promise = global.Promise;
const conn = mongoose.createConnection('mongodb://localhost:27017/node-login');

module.exports = conn.model('profile', profileSchema);


// module.exports = mongoose.model('profile', profileSchema);

// mongoose.connection.close()