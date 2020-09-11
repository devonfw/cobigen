export const environment: {
  production: boolean;
  restPathRoot: string;
  restServiceRoot: string;
  security: 'csrf' | 'jwt';
} = {
  production: false,
  restPathRoot: 'http://localhost:8081/',
  restServiceRoot: 'http://localhost:8081/services/rest/',
  security: 'jwt',
};
