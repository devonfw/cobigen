export const environment: {
  production: boolean;
  restPathRoot: string;
  restServiceRoot: string;
  security: 'csrf' | 'jwt';
} = {
  production: false,
  restPathRoot: 'http://localhost:8081/${variables.domain}-server/',
  restServiceRoot: 'http://localhost:8081/${variables.domain}-server/services/rest/',
  security: 'jwt',
};
