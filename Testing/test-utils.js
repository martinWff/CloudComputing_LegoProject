'use strict';
const { faker } = require('@faker-js/faker');

module.exports = {
  genNewUser: function (context, events, done) {
    context.vars.uName = faker.internet.username().toLowerCase();
    context.vars.uEmail = faker.internet.email();
    context.vars.uPwd = faker.internet.password();
    return done();
  }
};
