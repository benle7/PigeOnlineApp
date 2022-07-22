using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using pigeOnline.Data;
var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<pigeOnlineContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("pigeOnlineContext") ?? throw new InvalidOperationException("Connection string 'pigeOnlineContext' not found.")));

// Add services to the container.
builder.Services.AddControllersWithViews();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Reviews/Error");
}
app.UseStaticFiles();

app.UseRouting();

app.UseAuthorization();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Reviews}/{action=Index}/{id?}");

app.Run();
