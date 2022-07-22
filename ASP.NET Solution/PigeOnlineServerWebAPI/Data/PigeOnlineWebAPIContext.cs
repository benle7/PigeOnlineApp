#nullable disable
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using PigeOnlineWebAPI;

namespace PigeOnlineWebAPI.Data
{
    public class PigeOnlineWebAPIContext : DbContext
    {
        public PigeOnlineWebAPIContext (DbContextOptions<PigeOnlineWebAPIContext> options)
            : base(options)
        {
        }

        public DbSet<PigeOnlineWebAPI.Chat> Chat { get; set; }

        public DbSet<PigeOnlineWebAPI.Message> Message { get; set; }

        public DbSet<PigeOnlineWebAPI.User> User { get; set; }
    }
}
