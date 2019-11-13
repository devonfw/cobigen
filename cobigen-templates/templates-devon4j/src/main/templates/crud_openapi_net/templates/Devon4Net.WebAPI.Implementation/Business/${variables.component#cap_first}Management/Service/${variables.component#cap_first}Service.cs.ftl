using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Devon4Net.Domain.UnitOfWork.Service;
using Devon4Net.Domain.UnitOfWork.UnitOfWork;
using Devon4Net.Infrastructure.Log;
using Devon4Net.WebAPI.Implementation.Business.Dto;
using Devon4Net.WebAPI.Implementation.Domain.Entities;
using Devon4Net.WebAPI.Implementation.Domain.Database;
using Devon4Net.WebAPI.Implementation.Domain.RepositoryInterfaces;

namespace Devon4Net.WebAPI.Implementation.Business.${variables.component?cap_first}Management.Service
{
    public class ${variables.component?cap_first}Service : Service<CobigenContext>, I${variables.component?cap_first}Service
    {
        private readonly I${variables.entityName?cap_first}Repository _${variables.entityName?cap_first}Repository;
        
        public ${variables.component?cap_first}Service(IUnitOfWork<CobigenContext> uoW) : base(uoW)
        {
            _${variables.entityName?cap_first}Repository = uoW.Repository<I${variables.entityName?cap_first}Repository>();
        }

        public async Task<${variables.entityName?cap_first}Dto> Get${variables.entityName?cap_first}ById(long id){

            throw new NotImplementedException("TODO: Check the _${variables.entityName}Repository.Get${variables.entityName?cap_first}ById(id) input expression");
            
            Devon4NetLogger.Debug($"Get${variables.entityName?cap_first}ById method from service ${variables.entityName?cap_first}Service with value : {id}");
            var ${variables.entityName?uncap_first} = await _${variables.entityName}Repository.Get${variables.entityName?cap_first}ById(id).ConfigureAwait(false);
            
            //TODO: return value: convert ${variables.entityName?uncap_first} entity to ${variables.entityName?uncap_first}Dto
        }

        public async Task<${variables.entityName?cap_first}Dto> Create${variables.entityName?cap_first}(${variables.entityName?cap_first}Dto ${variables.entityName?uncap_first}){
            
            throw new NotImplementedException("TODO: Check the _${variables.entityName}Repository.Set${variables.entityName?cap_first}() input expression");
            
            Devon4NetLogger.Debug($"Set${variables.entityName?cap_first} method from service ${variables.entityName?cap_first}Service");
            var ${variables.entityName?uncap_first}Result = await _${variables.entityName}Repository.Set${variables.entityName?cap_first}().ConfigureAwait(false);
            
            //TODO: return value: convert ${variables.entityName?uncap_first} entity to ${variables.entityName?uncap_first}Dto
        }

        public async Task<long> Delete${variables.entityName?cap_first}ById(long id){
                
            throw new NotImplementedException("TODO: Check the _${variables.entityName}Repository.GetFirstOrDefault() input expression");
                    
            Devon4NetLogger.Debug($"Delete${variables.entityName?cap_first}ById method from service ${variables.entityName?cap_first}Service with value : {id}");
            var ${variables.entityName?uncap_first} = await _${variables.entityName}Repository.GetFirstOrDefault().ConfigureAwait(false);

            if (${variables.entityName?uncap_first} == null)
            {
                throw new ArgumentException($"The provided Id {id} does not exists");
            }

            return await _${variables.entityName}Repository.Delete${variables.entityName?cap_first}ById(id).ConfigureAwait(false);
        }

        public async Task<List<${variables.entityName?cap_first}Dto>> FindAll${variables.entityName?cap_first}s(Expression<Func<${variables.entityName?cap_first}, bool>> predicate = null)
        {
            throw new NotImplementedException("TODO: Check the _${variables.entityName}Repository.Get${variables.entityName?cap_first}(predicate) input expression");
            
            Devon4NetLogger.Debug("Get${variables.entityName?cap_first} method from service ${variables.entityName?cap_first}Service");
            var ${variables.entityName?uncap_first}s = await _${variables.entityName}Repository.Get${variables.entityName?cap_first}(predicate).ConfigureAwait(false);
            
            //TODO: return value: convert ${variables.entityName?uncap_first} entity to ${variables.entityName?uncap_first}Dto
        }
    }
}