using Devon4Net.WebAPI.Implementation.Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace Devon4Net.WebAPI.Implementation.Domain.Database
{
    public class CobigenContext : DbContext
    {
        public CobigenContext(DbContextOptions<CobigenContext> options)
            : base(options)
        {
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {

        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            //TO-DO: Create here your model and entity requirements
            //Please read the following documentation to get more information: 
            //https://docs.microsoft.com/en-us/ef/core/miscellaneous/configuring-dbcontext
        }
    }
}