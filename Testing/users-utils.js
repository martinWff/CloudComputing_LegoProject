'use strict';
const { faker } = require('@faker-js/faker');

module.exports = {
  // Generate fake user data
  genNewUser: function (context, events, done) {
    context.vars.username = faker.internet.username().toLowerCase();
    context.vars.email = faker.internet.email();
    context.vars.password = faker.internet.password({ length: 10 });
    context.vars.avatar = 'https://example.com/avatar.png';
    done();
  },

  // Optional dummy metrics handler (prevents warning)
  metricsByEndpoint_beforeRequest: function (requestParams, context, ee, next) {
    // no-op: avoids Artillery warnings
    return next();
  }
};
