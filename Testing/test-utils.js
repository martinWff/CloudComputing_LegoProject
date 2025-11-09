'use strict';
const { faker } = require('@faker-js/faker');

module.exports = {
  // Generate a new user
  genNewUser: function (context, events, done) {
    context.vars.uName = faker.internet.userName().toLowerCase();
    context.vars.uEmail = faker.internet.email();
    context.vars.uPwd = faker.internet.password();
    // Generate a temporary UUID for Artillery to use before backend returns real ID
    context.vars.uId = faker.datatype.uuid();
    return done();
  },

  // Capture the response from /register and store the real user ID
  genNewUserReply: function (req, res, context, events, done) {
    try {
      const body = typeof res.body === 'string' ? JSON.parse(res.body) : res.body;
      if (body && body.id) {
        context.vars.uId = body.id; // overwrite temp ID with real backend ID
      }
    } catch (err) {
      console.error('Failed to parse /register response:', err);
    }
    return done();
  },

  // Optional: capture image ID from /media upload
  uploadImageBody: function (req, context, events, done) {
    // This is an example: if you need to send an image, you can generate placeholder
    req.body = faker.image.imageUrl(200, 200, 'lego', true);
    return done();
  }
};
