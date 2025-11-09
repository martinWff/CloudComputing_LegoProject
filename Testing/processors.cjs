const { faker } = require('@faker-js/faker');

module.exports = {
  beforeRequest: function (requestParams, context, ee, next) {
    // Generate fake user data
    context.vars.username = faker.internet.userName();
    context.vars.email = faker.internet.email();
    context.vars.password = faker.internet.password(12);
    context.vars.avatar = "https://example.com/avatar.png";

    // Generate fake session token for login/logout if needed
    context.vars.session = faker.datatype.uuid();

    return next();
  }
};
