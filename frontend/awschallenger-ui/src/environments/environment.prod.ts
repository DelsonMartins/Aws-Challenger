export const environment = {
  production: true,
  apiUrl: 'http://ec2-3-129-216-21.us-east-2.compute.amazonaws.com:8080',
  

  tokenAllowedDomains: [ new RegExp('/ec2-3-129-216-21.us-east-2.compute.amazonaws.com/') ],
  tokenDisallowedRoutes: [ new RegExp('\/oauth\/token') ]
};