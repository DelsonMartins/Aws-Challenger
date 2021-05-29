export const environment = {
  production: true,
  apiUrl: 'https://desafio-aws-api.herokuapp.com',

  tokenAllowedDomains: [ new RegExp('/desaf-api.herokuapp.com/') ],
  tokenDisallowedRoutes: [ new RegExp('\/oauth\/token') ]
};