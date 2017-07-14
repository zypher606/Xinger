'use strict';

const user = require('../models/user');
const post_image = require('../models/post_image');
const bcrypt = require('bcryptjs');


	
exports.imageUpload = (req, res, email) => 

	new Promise((resolve,reject) => {

		
		const postedFilename = req.file.filename;
		console.log(req.file);
		console.log(req.file.filename);
	    console.log(req.body);


	    const newPostImage = new post_image({

			email			: email,
			descriptions	: "Descriptions",
			tags			: "troll,hot",
			filename		: postedFilename,
			created_at		: new Date()

		});



		newPostImage.save()

		.then(() => {
			resolve({ status: 201, message: 'Image uploaded !' });
		})

		.catch(err => {

			if (err.code == 11000) {
						
				reject({ status: 409, message: 'Image already uploaded !' });

			} else {

				reject({ status: 500, message: 'Internal Server Error !' });
			}
		});



	    return res.status(204).end();

	});

	
