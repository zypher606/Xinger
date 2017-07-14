'use strict';

const user = require('../models/user');

const profile = require('../models/profile');

const bcrypt = require('bcryptjs');

exports.registerUser = (name, email, password) => 

	new Promise((resolve,reject) => {

	    const salt = bcrypt.genSaltSync(10);
		const hash = bcrypt.hashSync(password, salt);

		const newUser = new user({

			name: name,
			email: email,
			hashed_password: hash,
			created_at: new Date()

		});



		const newProfile = new profile({

			name: name,
			email: email,
			bio: 'my bio data is here',
			profile_pic: 'default.jpg',
			cover_video: 'default.mp4'

		});
		



		newUser.save()

		.then(() => {
			resolve({ status: 201, message: 'User Registered Sucessfully !' });
			newProfile.save();
		})

		.catch(err => {

			if (err.code == 11000) {
						
				reject({ status: 409, message: 'User Already Registered !' });

			} else {

				reject({ status: 500, message: 'Internal Server Error !' });
			}
		});


		



	});


