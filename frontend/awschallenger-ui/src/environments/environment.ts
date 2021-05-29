// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

/*export const environment = {
  production: true,
  apiUrl: 'https://awschallenger-api.herokuapp.com'
}; */

// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',

  tokenAllowedDomains: [ new RegExp('localhost:8080') ],
  tokenDisallowedRoutes: [ new RegExp('\/oauth\/token') ]
};