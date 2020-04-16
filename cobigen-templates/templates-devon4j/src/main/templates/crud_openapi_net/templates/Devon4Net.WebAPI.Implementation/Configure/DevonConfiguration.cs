using System;
using Devon4Net.WebAPI.Implementation.Business.TodoManagement.Service;
using Devon4Net.WebAPI.Implementation.Business.EmployeeManagement.Service;
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
            services.AddTransient<IEmployeeService, EmployeeService>();

            //Repositories
            services.AddTransient<ITodoRepository, TodoRepository>();
            services.AddTransient<IEmployeeRepository, EmployeeRepository>();

            throw new NotImplementedException("Please add the services and repositories DI");
        }
    }
}
