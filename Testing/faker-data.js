// faker-data.js
import { faker } from '@faker-js/faker';

export function generateUser(context, events, done) {
  context.vars.user = {
    id: faker.string.uuid(),
    name: faker.person.fullName(),
    email: faker.internet.email(),
    age: faker.number.int({ min: 18, max: 70 })
  };
  return done();
}

export function generateUserId(context, events, done) {
  if (context.vars.user && Math.random() > 0.5) {
    context.vars.userId = context.vars.user.id;
  } else {
    context.vars.userId = faker.string.uuid();
  }
  return done();
}

// 👇 Add these to silence the warnings
export function metricsByEndpoint_beforeRequest(requestParams, context, ee, next) {
  return next();
}

export function metricsByEndpoint_afterResponse(requestParams, response, context, ee, next) {
  return next();
}

