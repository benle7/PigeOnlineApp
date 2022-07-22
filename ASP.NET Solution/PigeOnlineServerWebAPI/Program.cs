using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using PigeOnlineWebAPI.Data;
using PigeOnlineWebAPI;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using PigeOnlineWebAPI.Hubs;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<PigeOnlineWebAPIContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("PigeOnlineWebAPIContext") ?? throw new InvalidOperationException("Connection string 'PigeOnlineWebAPIContext' not found.")));

// Add services to the container.

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme).AddJwtBearer(options =>
{
    options.RequireHttpsMetadata = false;
    options.SaveToken = true;
    options.TokenValidationParameters = new TokenValidationParameters()
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidAudience = builder.Configuration["JWTParams:Audience"],
        ValidIssuer = builder.Configuration["JWTParams:Issuer"],
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(builder.Configuration["JWTParams:SecretKey"]))
    };

});

builder.Services.AddSignalR();
builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddCors(options =>
{
    options.AddPolicy("Allow All",
        builder =>
        {
            builder.AllowAnyHeader().AllowAnyMethod().SetIsOriginAllowed((host) => true).AllowCredentials();
                   
        });

});
builder.Services.AddSingleton<IAndroidMessaging, AndroidMessaging>();
builder.Services.AddScoped<IPigeOnlineService, PigeOnlineService>();
var app = builder.Build();


// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthentication();
app.UseCors("Allow All");
app.MapControllers();
app.UseRouting();
app.UseAuthorization();
app.UseEndpoints(app =>
{
    app.MapHub<PigeOnlineHub>("/PigeOnlineHub");
});

app.Run();
