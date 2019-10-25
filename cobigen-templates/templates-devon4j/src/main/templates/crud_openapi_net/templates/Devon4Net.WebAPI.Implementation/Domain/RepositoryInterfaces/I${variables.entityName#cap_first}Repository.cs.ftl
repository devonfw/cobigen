using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Devon4Net.Domain.UnitOfWork.Repository;
using Devon4Net.WebAPI.Implementation.Domain.Entities;

namespace Devon4Net.WebAPI.Implementation.Domain.RepositoryInterfaces
{
    public interface I${variables.entityName?cap_first}Repository : IRepository<${variables.entityName?cap_first}>
    {
        Task<IList<${variables.entityName?cap_first}>> Get${variables.entityName?cap_first}(Expression<Func<${variables.entityName?cap_first}, bool>> predicate = null);
        Task<${variables.entityName?cap_first}> Get${variables.entityName?cap_first}ById(long id);
        Task<${variables.entityName?cap_first}> Set${variables.entityName?cap_first}();
        Task<long> Delete${variables.entityName?cap_first}ById(long id);
    }
}
        