export const environment = {
  production: true,
  apiUrl: 'https://desafio-aws-api.herokuapp.com',

  tokenAllowedDomains: [ /algamoney-api.herokuapp.com/ ],
  tokenDisallowedRoutes: [/\/oauth\/token/]
};