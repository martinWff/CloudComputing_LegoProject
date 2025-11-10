const crypto = require('crypto');

function genNewUser(userContext, events, done) {
  // Generate a random unique username and email
  const uniqueId = crypto.randomBytes(4).toString('hex'); // 8-char hex
  const uName = `user_${uniqueId}`;
  const uEmail = `user_${uniqueId}@example.com`;
  const uPwd = `Pwd_${uniqueId}#`;

  // Save them to the virtual user context
  userContext.vars.uName = uName;
  userContext.vars.uEmail = uEmail;
  userContext.vars.uPwd = uPwd;

  return done();
}

module.exports = {
  genNewUser
};
