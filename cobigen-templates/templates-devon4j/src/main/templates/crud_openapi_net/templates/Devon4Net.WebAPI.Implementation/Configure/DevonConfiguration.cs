using System;
using Devon4Net.WebAPI.Implementation.Business.TodoManagement.Service;
using Devon4Net.WebAPI.Implementation.Data.Repositories;
using Devon4Net.WebAPI.Implementation.Domain.RepositoryInterfaces;
using Microsoft.Extensions.DependencyInjection;

namespace Devon4Net.WebAPI.Implementation.Configure
{
    public static class DevonConfiguration
    {
        public static void SetupDevonDependencyInjection(this IServiceCollection services)
        {
            //Services
            services.AddTransient<ITodoService, TodoService>();

            //Repositories
            services.AddTransient<ITodoRepository, TodoRepository>();

            throw new NotImplementedException("Please add the services and repositories DI");
        }
    }
}
