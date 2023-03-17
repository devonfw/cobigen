using Devon4Net.WebAPI.Implementation.Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace Devon4Net.WebAPI.Implementation.Domain.Database
{
    /// <summary>
    /// Cobigen database context definition
    /// </summary>
    public class CobigenContext : DbContext
    {
        /// <summary>
        /// Cobigen context definition
        /// </summary>
        /// <param name="options"></param>
        public CobigenContext(DbContextOptions<CobigenContext> options)
            : base(options)
        {
        }

        /// <summary>
        /// Any extra configuration should be here
        /// </summary>
        /// <param name="optionsBuilder"></param>
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
        }

        /// <summary>
        /// Model rules definition
        /// </summary>
        /// <param name="modelBuilder"></param>
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            //TO-DO: Create here your model and entity requirements
            //Please read the following documentation to get more information: 
            //https://docs.microsoft.com/en-us/ef/core/miscellaneous/configuring-dbcontext
        }
    }
}