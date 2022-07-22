#nullable disable
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using pigeOnline.Models;

namespace pigeOnline.Data
{
    public class pigeOnlineContext : DbContext
    {
        public pigeOnlineContext (DbContextOptions<pigeOnlineContext> options)
            : base(options)
        {
        }

        public DbSet<pigeOnline.Models.Review> Review { get; set; }
    }
}
