using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;
using AutoMapper;
using Extensions;
using Microsoft.Extensions.Configuration;
using Newtonsoft.Json;
using Devon4Net.Domain.Entities;
using Devon4Net.Domain.Entities.Models;
using Devon4Net.Infrastructure.Extensions;

using Devon4Net.Business.Common.Dto;

namespace Devon4Net.Business.Common.${variables.component?cap_first}Management.Service
{
    public class ${variables.component?cap_first}Service : I${variables.component?cap_first}Service
    {
        private IConfiguration Configuration { get; set; }
        private IMapper Mapper { get; set; }

        public ${variables.component?cap_first}Service(IMapper mapper, IConfiguration configuration)
        {
            Configuration = configuration;
            Mapper = mapper;
        }

        public async Task<${variables.entityName?cap_first}Dto> Get${variables.entityName?cap_first}(long id){

            throw new NotImplementedException("TODO: Please add ${variables.entityName?cap_first}Service into the method AddBusinessCommonDependencyInjectionService that you can find it in Bussiness/Devon4Net.Business.Common/Configuration/BusinessCommonConfiguration.cs");
        }

        public async Task<${variables.entityName?cap_first}Dto> Save${variables.entityName?cap_first}(${variables.entityName?cap_first}Dto ${variables.entityName?uncap_first}){
            
            throw new NotImplementedException("TODO: Please add ${variables.entityName?cap_first}Service into the method AddBusinessCommonDependencyInjectionService that you can find it in Bussiness/Devon4Net.Business.Common/Configuration/BusinessCommonConfiguration.cs");
        }

        public async Task<bool> Delete${variables.entityName?cap_first}(long id){
                
            throw new NotImplementedException("TODO: Please add ${variables.entityName?cap_first}Service into the method AddBusinessCommonDependencyInjectionService that you can find it in Bussiness/Devon4Net.Business.Common/Configuration/BusinessCommonConfiguration.cs");
        }

        public async Task<List<${variables.entityName?cap_first}Dto>> FindAll${variables.entityName?cap_first}s(){

            throw new NotImplementedException("TODO: Please add ${variables.entityName?cap_first}Service into the method AddBusinessCommonDependencyInjectionService that you can find it in Bussiness/Devon4Net.Business.Common/Configuration/BusinessCommonConfiguration.cs");
        }
    }
}