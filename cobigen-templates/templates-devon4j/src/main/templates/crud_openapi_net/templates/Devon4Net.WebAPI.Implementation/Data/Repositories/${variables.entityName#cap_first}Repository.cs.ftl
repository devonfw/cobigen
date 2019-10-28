using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Devon4Net.Domain.UnitOfWork.Repository;
using Devon4Net.Infrastructure.Log;
using Devon4Net.WebAPI.Implementation.Domain.Database;
using Devon4Net.WebAPI.Implementation.Domain.Entities;
using Devon4Net.WebAPI.Implementation.Domain.RepositoryInterfaces;

namespace Devon4Net.WebAPI.Implementation.Data.Repositories
{
    public class ${variables.entityName?cap_first}Repository : Repository<${variables.entityName?cap_first}>, I${variables.entityName?cap_first}Repository
    {
        public ${variables.entityName?cap_first}Repository(CobigenContext context) : base(context)
        {
        }

        public async Task<IList<${variables.entityName?cap_first}>> Get${variables.entityName?cap_first}(Expression<Func<${variables.entityName?cap_first}, bool>> predicate = null)
        {
            Devon4NetLogger.Debug("Get${variables.entityName?cap_first} method from ${variables.entityName?cap_first}Repository ${variables.entityName?cap_first}Service");
            return await Get(predicate).ConfigureAwait(false);
        }

        public async Task<${variables.entityName?cap_first}> Get${variables.entityName?cap_first}ById(long id)
        {
            Devon4NetLogger.Debug($"Get${variables.entityName?cap_first}ById method from repository ${variables.entityName?cap_first}Service with value : {id}");
            Devon4NetLogger.Information("Replace GetFirstOrDefault() to Get() with the needed expresion");
            return await GetFirstOrDefault().ConfigureAwait(false);
        }

        public async Task<${variables.entityName?cap_first}> Set${variables.entityName?cap_first}()
        {
            Devon4NetLogger.Debug($"Set${variables.entityName?cap_first} method from repository ${variables.entityName?cap_first}Service");
            Devon4NetLogger.Information("Replace input parameter Create(new Sale()) with the object that is needed to be created");
            return await Create(new ${variables.entityName?cap_first}()).ConfigureAwait(false);
        }

        public async Task<long> Delete${variables.entityName?cap_first}ById(long id)
        {
            Devon4NetLogger.Debug($"Delete${variables.entityName?cap_first}ById method from repository ${variables.entityName?cap_first}Service with value : {id}");
            Devon4NetLogger.Information("Add Expresion to delete");
            var deleted = await Delete().ConfigureAwait(false);

            if (deleted)
            {
                return id;
            }

            throw  new ApplicationException($"The ${variables.entityName?cap_first} entity {id} has not been deleted.");
        }
    }
}
